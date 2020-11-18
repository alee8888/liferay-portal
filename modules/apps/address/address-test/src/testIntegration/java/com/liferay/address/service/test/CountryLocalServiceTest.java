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

package com.liferay.address.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Albert Lee
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class CountryLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddCountry() throws Exception {
		boolean billingAllowed = true;
		String number = RandomTestUtil.randomString();
		double position = RandomTestUtil.randomDouble();
		boolean shippingAllowed = true;
		boolean subjectToVAT = true;

		Country country = _addCountry(
			billingAllowed, number, position, shippingAllowed, subjectToVAT);

		Assert.assertEquals(billingAllowed, country.isBillingAllowed());
		Assert.assertEquals(number, country.getNumber());
		Assert.assertEquals(position, country.getPosition(), 0);
		Assert.assertEquals(shippingAllowed, country.isShippingAllowed());
		Assert.assertEquals(subjectToVAT, country.isSubjectToVAT());

		_countryLocalService.deleteCountry(country);

		long countryId = country.getCountryId();

		Assert.assertNull(_countryLocalService.fetchCountry(countryId));
		Assert.assertNull(_regionService.getRegions(countryId));

		List<Organization> organizationList = _organizationLocalService.search(
			country.getCompanyId(),
			OrganizationConstants.ANY_PARENT_ORGANIZATION_ID, null, null, null,
			countryId, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (Organization organization : organizationList) {
			Assert.assertEquals(0, organization.getCountryId());
			Assert.assertEquals(0, organization.getRegionId());
		}
	}

	@Test
	public void testUpdateCountry() throws Exception {
		Country country = _addCountry();

		Country updatedCountry = _countryLocalService.updateCountry(
			country.getCountryId(), country.isActive(), country.getA2(),
			country.getA3(), false, country.getIdd(), country.getName(),
			country.getNumber(), 12345, false, false, null);

		Assert.assertEquals(
			updatedCountry.isBillingAllowed(), country.isBillingAllowed());
		Assert.assertEquals(updatedCountry.getNumber(), country.getNumber());
		Assert.assertEquals(
			updatedCountry.getPosition(), country.getPosition(), 0);
		Assert.assertEquals(
			updatedCountry.isShippingAllowed(), country.isSubjectToVAT());
		Assert.assertEquals(
			updatedCountry.isSubjectToVAT(), country.isSubjectToVAT());
	}

	private Country _addCountry() throws Exception {
		return _addCountry(
			true, RandomTestUtil.randomString(), RandomTestUtil.randomDouble(),
			true, true);
	}

	private Country _addCountry(
			boolean billingAllowed, String number, double position,
			boolean shippingAllowed, boolean subjectToVAT)
		throws Exception {

		return _countryLocalService.addCountry(
			true, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			billingAllowed, RandomTestUtil.randomString(), number,
			RandomTestUtil.randomString(), position, shippingAllowed,
			subjectToVAT, null, ServiceContextTestUtil.getServiceContext());
	}

	@Inject
	private static CountryLocalService _countryLocalService;

	@Inject
	private static OrganizationLocalService _organizationLocalService;

	@Inject
	private static RegionService _regionService;

}