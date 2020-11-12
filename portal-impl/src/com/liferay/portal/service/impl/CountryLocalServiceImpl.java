/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.CountryA2Exception;
import com.liferay.portal.kernel.exception.CountryA3Exception;
import com.liferay.portal.kernel.exception.CountryIddException;
import com.liferay.portal.kernel.exception.CountryNameException;
import com.liferay.portal.kernel.exception.CountryNumberException;
import com.liferay.portal.kernel.exception.DuplicateCountryException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.base.CountryLocalServiceBaseImpl;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @see CountryLocalServiceBaseImpl
 */
public class CountryLocalServiceImpl extends CountryLocalServiceBaseImpl {

	@Override
	public Country addCountry(
			boolean active, String a2, String a3, boolean billingAllowed,
			String idd, String name, String number, double position,
			boolean shippingAllowed, boolean subjectToVAT,
			Map<String, String> titleMap, ServiceContext serviceContext)
		throws PortalException {

		if (fetchCountryByC_A2(serviceContext.getCompanyId(), a2) != null) {
			throw new DuplicateCountryException();
		}

		validate(titleMap, a2, a3, number, idd);

		User user = _userLocalService.getUser(serviceContext.getUserId());

		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getDefault());

		long countryId = counterLocalService.increment();

		Country country = countryPersistence.create(countryId);

		country.setCompanyId(user.getCompanyId());
		country.setUserId(user.getUserId());
		country.setUserName(user.getFullName());
		country.setA2(a2);
		country.setA3(a3);
		country.setActive(active);
		country.setBillingAllowed(billingAllowed);
		country.setDefaultLanguageId(defaultLanguageId);
		country.setIdd(idd);
		country.setName(name);
		country.setNumber(number);
		country.setPosition(position);
		country.setShippingAllowed(shippingAllowed);
		country.setSubjectToVAT(subjectToVAT);

		return countryPersistence.update(country);
	}

	@Override
	public void deleteCountries(long companyId) {
		List<Country> countries = countryPersistence.findByCompanyId(companyId);

		for (Country country : countries) {
			countryLocalService.deleteCountry(country);
		}
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public Country deleteCountry(Country country) {

		// Country

		countryPersistence.remove(country);

		// Regions

		long countryId = country.getCountryId();

		regionPersistence.removeByCountryId(countryId);

		// Addresses

		_addressLocalService.deleteCountryAddresses(countryId);

		// Organizations

		List<Organization> organizationList = _organizationLocalService.search(
			country.getCompanyId(),
			OrganizationConstants.ANY_PARENT_ORGANIZATION_ID, null, null, null,
			countryId, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (Organization organization : organizationList) {
			organization.setCountryId(0);
			organization.setRegionId(0);

			_organizationLocalService.updateOrganization(organization);
		}

		return country;
	}

	@Override
	public Country deleteCountry(long countryId) throws PortalException {
		Country country = countryPersistence.findByPrimaryKey(countryId);

		return countryLocalService.deleteCountry(country);
	}

	@Override
	public Country fetchCountryByC_A2(long countryId, String a2) {
		return countryPersistence.fetchByC_A2(countryId, a2);
	}

	@Override
	public Country fetchCountryByC_N(long companyId, String number) {
		return countryPersistence.fetchByC_N(companyId, number);
	}

	@Override
	public List<Country> getCountries(
		long companyId, int start, int end,
		OrderByComparator<Country> orderByComparator) {

		return countryPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public Country getCountry(long companyId, String a2)
		throws PortalException {

		return countryPersistence.findByC_A2(companyId, a2);
	}

	@Override
	public Country updateCountry(
			long countryId, boolean active, String a2, String a3,
			boolean billingAllowed, String idd, String name, String number,
			double position, boolean shippingAllowed, boolean subjectToVAT,
			Map<String, String> titleMap)
		throws PortalException {

		Country country = countryPersistence.findByPrimaryKey(countryId);

		validate(titleMap, a2, a3, number, idd);

		country.setA2(a2);
		country.setA3(a3);
		country.setActive(active);
		country.setBillingAllowed(billingAllowed);
		country.setIdd(idd);
		country.setName(name);
		country.setNumber(number);
		country.setPosition(position);
		country.setShippingAllowed(shippingAllowed);
		country.setSubjectToVAT(subjectToVAT);

		return countryPersistence.update(country);
	}

	protected void validate(
			Map<String, String> titleMap, String a2, String a3, String number,
			String idd)
		throws PortalException {

		Locale locale = LocaleUtil.getSiteDefault();

		String title = titleMap.get(locale.toString());

		if (Validator.isNull(title)) {
			throw new CountryNameException();
		}

		if (Validator.isNotNull(a2) && (a2.length() != 2)) {
			throw new CountryA2Exception();
		}

		if (Validator.isNotNull(a3) && (a3.length() != 3)) {
			throw new CountryA3Exception();
		}

		if (Validator.isNull(number)) {
			throw new CountryNumberException();
		}

		if (Validator.isNull(idd)) {
			throw new CountryIddException();
		}
	}

	@BeanReference(type = AddressLocalService.class)
	private AddressLocalService _addressLocalService;

	@BeanReference(type = OrganizationLocalService.class)
	private OrganizationLocalService _organizationLocalService;

	@BeanReference(type = UserLocalService.class)
	private UserLocalService _userLocalService;

}