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

package com.liferay.account.admin.web.internal.servlet.taglib.util;

import com.liferay.account.admin.web.internal.display.AccountEntryDisplay;
import com.liferay.account.admin.web.internal.display.AccountUserDisplay;
import com.liferay.account.admin.web.internal.security.permission.resource.AccountEntryPermission;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.List;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Albert Lee
 */
public class AccountUserActionDropdownItemsProvider {

	public AccountUserActionDropdownItemsProvider(
		AccountEntryDisplay accountEntryDisplay, AccountUserDisplay accountUserDisplay, PermissionChecker permissionChecker, RenderRequest renderRequest, RenderResponse renderResponse) {

		_accountEntryDisplay = accountEntryDisplay;
		_accountUserDisplay = accountUserDisplay;
		_permissionChecker = permissionChecker;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		return DropdownItemListBuilder.add(
			() -> AccountEntryPermission.contains(_permissionChecker,
				_accountEntryDisplay.getAccountEntryId(), ActionKeys.MANAGE_USERS),
			dropdownItem -> {
				dropdownItem.putData("action", "removeAccountUsers");
				dropdownItem.putData(
					"removeAccountUsersURL",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/account_admin/remove_account_users"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"accountEntryId", _accountEntryDisplay.getAccountEntryId()
					).setParameter(
						"accountUserIds", _accountUserDisplay.getUserId()
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "remove"));
			}
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;
	private final AccountEntryDisplay _accountEntryDisplay;
	private final AccountUserDisplay _accountUserDisplay;
	private final PermissionChecker _permissionChecker;

}