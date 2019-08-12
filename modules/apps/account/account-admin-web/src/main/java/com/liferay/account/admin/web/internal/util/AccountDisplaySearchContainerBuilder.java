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

package com.liferay.account.admin.web.internal.util;

import com.liferay.account.admin.web.internal.display.AccountDisplay;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Pei-Jung Lan
 */
public class AccountDisplaySearchContainerBuilder {

	public static SearchContainer getAccountDisplaySearchContainer(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		SearchContainer accountDisplaySearchContainer = new SearchContainer(
			liferayPortletRequest, liferayPortletResponse.createRenderURL(),
			null, "no-accounts-were-found");

		accountDisplaySearchContainer.setId("accounts");

		String orderByCol = ParamUtil.getString(
			liferayPortletRequest, "orderByCol", "name");

		accountDisplaySearchContainer.setOrderByCol(orderByCol);

		String orderByType = ParamUtil.getString(
			liferayPortletRequest, "orderByType", "asc");

		accountDisplaySearchContainer.setOrderByType(orderByType);

		accountDisplaySearchContainer.setRowChecker(
			new EmptyOnClickRowChecker(liferayPortletResponse));

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(liferayPortletRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String navigation = ParamUtil.getString(
			liferayPortletRequest, "navigation", "active");

		List<AccountEntry> accountEntries =
			AccountEntryLocalServiceUtil.getAccountEntries(
				themeDisplay.getCompanyId(), _getStatus(navigation),
				accountDisplaySearchContainer.getStart(),
				accountDisplaySearchContainer.getEnd(),
				accountDisplaySearchContainer.getOrderByComparator());

		Stream<AccountEntry> accountEntryStream = accountEntries.stream();

		List<AccountDisplay> accountDisplays = accountEntryStream.map(
			AccountDisplayBuilder::getAccountDisplay
		).collect(
			Collectors.toList()
		);

		accountDisplaySearchContainer.setResults(accountDisplays);
		accountDisplaySearchContainer.setTotal(accountDisplays.size());

		return accountDisplaySearchContainer;
	}

	private static int _getStatus(String navigation) {
		if (Objects.equals(navigation, "inactive")) {
			return WorkflowConstants.STATUS_INACTIVE;
		}

		return WorkflowConstants.STATUS_APPROVED;
	}

}