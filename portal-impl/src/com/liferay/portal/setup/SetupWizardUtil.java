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

package com.liferay.portal.setup;

import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsValues;

import java.io.IOException;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;

/**
 * @author Manuel de la Peña
 * @author Julio Camarero
 * @author Brian Wing Shun Chan
 * @author Miguel Pastor
 */
public class SetupWizardUtil {

	public static final String PROPERTIES_FILE_NAME =
		"portal-setup-wizard.properties";

	public static String getDefaultLanguageId() {
		Locale defaultLocale = LocaleUtil.getDefault();

		return LocaleUtil.toLanguageId(defaultLocale);
	}

	public static boolean isDefaultDatabase(HttpServletRequest request) {
		boolean hsqldb = ParamUtil.getBoolean(
			request, "defaultDatabase",
			PropsValues.JDBC_DEFAULT_URL.contains("hsqldb"));

		boolean jndi = Validator.isNotNull(PropsValues.JDBC_DEFAULT_JNDI_NAME);

		return hsqldb && !jndi;
	}

	public static void updateLanguage(
		HttpServletRequest request, HttpServletResponse response) {

		String languageId = ParamUtil.getString(
			request, "companyLocale", getDefaultLanguageId());

		Locale locale = LocaleUtil.fromLanguageId(languageId);

		if (!LanguageUtil.isAvailableLocale(locale)) {
			return;
		}

		HttpSession session = request.getSession();

		session.setAttribute(Globals.LOCALE_KEY, locale);
		session.setAttribute(WebKeys.SETUP_WIZARD_DEFAULT_LOCALE, languageId);

		LanguageUtil.updateCookie(request, response, locale);

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		themeDisplay.setLanguageId(languageId);
		themeDisplay.setLocale(locale);
	}

	public static void updateSetup(
			HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		UnicodeProperties unicodeProperties = PropertiesParamUtil.getProperties(
			request, _PROPERTIES_PREFIX);

		unicodeProperties.setProperty(
			PropsKeys.LIFERAY_HOME,
			SystemProperties.get(PropsKeys.LIFERAY_HOME));

		_processOtherProperties(request, unicodeProperties);

		updateLanguage(request, response);

		unicodeProperties.put(
			PropsKeys.SETUP_WIZARD_ENABLED, String.valueOf(false));

		_addDefaultData(request, response, unicodeProperties);

		HttpSession session = request.getSession();

		session.setAttribute(
			WebKeys.SETUP_WIZARD_PROPERTIES, unicodeProperties);
		session.setAttribute(
			WebKeys.SETUP_WIZARD_PROPERTIES_FILE_CREATED,
			_writePropertiesFile(unicodeProperties));
	}

	private static void _addDefaultData(
			HttpServletRequest request, HttpServletResponse response,
			UnicodeProperties unicodeProperties)
		throws Exception {

		Company company = CompanyLocalServiceUtil.getCompanyById(
			PortalInstances.getDefaultCompanyId());

		String languageId = ParamUtil.getString(
			request, "companyLocale", getDefaultLanguageId());

		String companyName = ParamUtil.getString(
			request, "companyName", PropsValues.COMPANY_DEFAULT_NAME);

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		themeDisplay.setCompany(company);

		String emailAddress = ParamUtil.getString(
			request, "adminEmailAddress",
			PropsValues.DEFAULT_ADMIN_EMAIL_ADDRESS_PREFIX + StringPool.AT +
				company.getMx());

		PropsValues.ADMIN_EMAIL_FROM_ADDRESS = emailAddress;

		unicodeProperties.put(PropsKeys.ADMIN_EMAIL_FROM_ADDRESS, emailAddress);

		String firstName = ParamUtil.getString(
			request, "adminFirstName", PropsValues.DEFAULT_ADMIN_FIRST_NAME);
		String lastName = ParamUtil.getString(
			request, "adminLastName", PropsValues.DEFAULT_ADMIN_LAST_NAME);

		Boolean addSampleData = ParamUtil.getBoolean(
			request, "addSampleData", false);

		SetupWizardSampleDataUtil.addSampleData(
			company.getCompanyId(), companyName, themeDisplay.getLocale(),
			languageId, firstName, lastName, emailAddress, true, addSampleData);

		company = CompanyLocalServiceUtil.fetchCompany(company.getCompanyId());

		themeDisplay.setCompany(company);

		User adminUser = UserLocalServiceUtil.fetchUserByEmailAddress(
			company.getCompanyId(), emailAddress);

		PropsValues.ADMIN_EMAIL_FROM_NAME = adminUser.getFullName();

		unicodeProperties.put(
			PropsKeys.ADMIN_EMAIL_FROM_NAME, adminUser.getFullName());

		HttpSession session = request.getSession();

		session.setAttribute(WebKeys.EMAIL_ADDRESS, emailAddress);
		session.setAttribute(WebKeys.SETUP_WIZARD_PASSWORD_UPDATED, true);
		session.setAttribute(WebKeys.USER, adminUser);
		session.setAttribute(WebKeys.USER_ID, adminUser.getUserId());

		EventsProcessorUtil.process(
			PropsKeys.LOGIN_EVENTS_POST, PropsValues.LOGIN_EVENTS_POST, request,
			response);
	}

	private static void _processOtherProperties(
			HttpServletRequest request, UnicodeProperties unicodeProperties)
		throws Exception {

		_processProperty(
			request, unicodeProperties, "adminFirstName",
			PropsKeys.DEFAULT_ADMIN_FIRST_NAME,
			PropsValues.DEFAULT_ADMIN_FIRST_NAME);
		_processProperty(
			request, unicodeProperties, "adminLastName",
			PropsKeys.DEFAULT_ADMIN_LAST_NAME,
			PropsValues.DEFAULT_ADMIN_LAST_NAME);
		_processProperty(
			request, unicodeProperties, "companyName",
			PropsKeys.COMPANY_DEFAULT_NAME, PropsValues.COMPANY_DEFAULT_NAME);
	}

	private static void _processProperty(
			HttpServletRequest request, UnicodeProperties unicodeProperties,
			String parameterName, String propertyKey, String defaultValue)
		throws Exception {

		String value = ParamUtil.getString(
			request, parameterName, defaultValue);

		if (!value.equals(defaultValue)) {
			unicodeProperties.put(propertyKey, value);
		}
	}

	private static boolean _writePropertiesFile(
		UnicodeProperties unicodeProperties) {

		try {
			FileUtil.write(
				PropsValues.LIFERAY_HOME, PROPERTIES_FILE_NAME,
				unicodeProperties.toString());

			if (FileUtil.exists(
					PropsValues.LIFERAY_HOME + StringPool.SLASH +
						PROPERTIES_FILE_NAME)) {

				return true;
			}
		}
		catch (IOException ioe) {
			_log.error(ioe, ioe);
		}

		return false;
	}

	private static final String _PROPERTIES_PREFIX = "properties--";

	private static final Log _log = LogFactoryUtil.getLog(
		SetupWizardUtil.class);

}