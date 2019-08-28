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

package com.liferay.account.admin.web.internal.action;

import com.liferay.account.constants.AccountsPortletKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WebsiteLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Albert Lee
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + AccountsPortletKeys.ACCOUNTS_ADMIN,
		"mvc.command.name=/account_admin/edit_account"
	},
	service = MVCActionCommand.class
)
public class EditAccountMVCActionCommand extends BaseMVCActionCommand {

	protected AccountEntry addAccountEntry(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long parentAccountEntryId = ParamUtil.getInteger(
			actionRequest, "parentAccountEntryId");
		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");
		int status = ParamUtil.getInteger(actionRequest, "status");

		byte[] logoBytes = null;

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		if (fileEntryId > 0) {
			FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

			logoBytes = FileUtil.getBytes(fileEntry.getContentStream());
		}

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			themeDisplay.getUserId(), parentAccountEntryId, name, description,
			logoBytes, status);

		String url = ParamUtil.getString(actionRequest, "url");

		if (Validator.isNotNull(url)) {
			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				AccountEntry.class.getName(), actionRequest);

			_addWebsite(
				themeDisplay.getUserId(), AccountEntry.class.getName(),
				accountEntry.getAccountEntryId(), url, 0, true, serviceContext);
		}

		return accountEntry;
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		String redirect = ParamUtil.getString(actionRequest, "redirect");

		try {
			AccountEntry accountEntry = null;

			if (cmd.equals(Constants.ADD)) {
				accountEntry = addAccountEntry(actionRequest);

				redirect = _http.setParameter(
					redirect, actionResponse.getNamespace() + "accountEntryId",
					accountEntry.getAccountEntryId());
			}
			else if (cmd.equals(Constants.UPDATE)) {
				updateAccountEntry(actionRequest);
			}

			if (Validator.isNotNull(redirect)) {
				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
		catch (Exception e) {
			String mvcPath = "/edit_account.jsp";

			if (e instanceof PrincipalException) {
				SessionErrors.add(actionRequest, e.getClass());

				mvcPath = "/error.jsp";
			}
			else {
				throw e;
			}

			actionResponse.setRenderParameter("mvcPath", mvcPath);
		}
	}

	protected void updateAccountEntry(ActionRequest actionRequest)
		throws Exception {

		long accountEntryId = ParamUtil.getLong(
			actionRequest, "accountEntryId");

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			accountEntryId);

		long parentAccountEntryId = ParamUtil.getInteger(
			actionRequest, "parentAccountEntryId");
		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");
		boolean deleteLogo = ParamUtil.getBoolean(actionRequest, "deleteLogo");
		int status = ParamUtil.getInteger(actionRequest, "status");

		byte[] logoBytes = null;

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		if (fileEntryId > 0) {
			FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

			logoBytes = FileUtil.getBytes(fileEntry.getContentStream());
		}

		_accountEntryLocalService.updateAccountEntry(
			accountEntryId, parentAccountEntryId, name, description, deleteLogo,
			logoBytes, status);

		String url = ParamUtil.getString(actionRequest, "url");

		_updateWebsite(accountEntry, url);
	}

	private void _addWebsite(
			long userId, String className, long classPK, String url,
			long typeId, boolean primary, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.fetchUser(userId);
		long classNameId = _classNameLocalService.getClassNameId(className);

		long websiteId = _counterLocalService.increment();

		Website website = _websiteLocalService.createWebsite(websiteId);

		website.setUuid(serviceContext.getUuid());
		website.setCompanyId(user.getCompanyId());
		website.setUserId(user.getUserId());
		website.setUserName(user.getFullName());
		website.setClassNameId(classNameId);
		website.setClassPK(classPK);
		website.setUrl(url);
		website.setTypeId(typeId);
		website.setPrimary(primary);

		_websiteLocalService.updateWebsite(website);
	}

	private void _updateWebsite(AccountEntry accountEntry, String url)
		throws PortalException {

		List<Website> websites = _websiteLocalService.getWebsites(
			accountEntry.getCompanyId(), AccountEntry.class.getName(),
			accountEntry.getPrimaryKey());

		if (!websites.isEmpty()) {
			Website website = websites.get(0);

			website.setUrl(url);

			_websiteLocalService.updateWebsite(website);
		}
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private Http _http;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WebsiteLocalService _websiteLocalService;

}