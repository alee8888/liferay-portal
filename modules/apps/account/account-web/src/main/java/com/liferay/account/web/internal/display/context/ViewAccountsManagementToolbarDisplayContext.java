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

package com.liferay.account.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemList;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.usersadmin.search.UserSearch;
import com.liferay.portlet.usersadmin.search.UserSearchTerms;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Albert Lee
 */
public class ViewAccountsManagementToolbarDisplayContext {

	public ViewAccountsManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_currentURL = PortletURLUtil.getCurrent(
			_renderRequest, _renderResponse);
	}

	public List<DropdownItem> getActionDropdownItems() {
		return new DropdownItemList() {

		};
	}

	public String getClearResultsURL() {
		PortletURL clearResultsURL = getPortletURL();

		clearResultsURL.setParameter("keywords", StringPool.BLANK);
		clearResultsURL.setParameter("navigation", (String)null);

		return clearResultsURL.toString();
	}

	public CreationMenu getCreationMenu() throws PortalException {
		return new CreationMenu() {

		};
	}

	public List<DropdownItem> getFilterDropdownItems() {
		return new DropdownItemList() {

		};
	}

	public List<LabelItem> getFilterLabelItems() {
		return new LabelItemList() {

		};
	}

	public String getOrderByCol() {
		return _userSearch.getOrderByCol();
	}

	public String getOrderByType() {
		return _userSearch.getOrderByType();
	}

	public PortletURL getPortletURL() {
		try {
			PortletURL portletURL = PortletURLUtil.clone(
				_currentURL, _renderResponse);

			portletURL.setParameter("orderByCol", getOrderByCol());
			portletURL.setParameter("orderByType", getOrderByType());

			return portletURL;
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn(e, e);
			}

			return _renderResponse.createRenderURL();
		}
	}

	public String getSearchActionURL() {
		PortletURL searchActionURL = getPortletURL();

		return searchActionURL.toString();
	}

	public SearchContainer getSearchContainer() {
		if (_userSearch != null) {
			return _userSearch;
		}

		PortletURL portletURL = (PortletURL)_httpServletRequest.getAttribute(
			"view.jsp-portletURL");

		UserSearch userSearch = new UserSearch(
			_renderRequest, "cur2", portletURL);

		RowChecker rowChecker = new EmptyOnClickRowChecker(_renderResponse);

		rowChecker.setRowIds("rowIdsUser");

		userSearch.setRowChecker(rowChecker);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		UserSearchTerms searchTerms =
			(UserSearchTerms)userSearch.getSearchTerms();

		int total = UserLocalServiceUtil.searchCount(
			themeDisplay.getCompanyId(), searchTerms.getKeywords(),
			searchTerms.getStatus(), new LinkedHashMap<String, Object>());

		userSearch.setTotal(total);

		List<User> results = UserLocalServiceUtil.search(
			themeDisplay.getCompanyId(), searchTerms.getKeywords(),
			searchTerms.getStatus(), new LinkedHashMap<String, Object>(),
			userSearch.getStart(), userSearch.getEnd(),
			userSearch.getOrderByComparator());

		userSearch.setResults(results);

		_userSearch = userSearch;

		return _userSearch;
	}

	public String getSortingURL() {
		PortletURL sortingURL = getPortletURL();

		sortingURL.setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc");

		return sortingURL.toString();
	}

	public boolean isShowDeleteButton() {
		UserSearchTerms searchTerms =
			(UserSearchTerms)getSearchContainer().getSearchTerms();

		if ((searchTerms.getStatus() != WorkflowConstants.STATUS_ANY) &&
			(searchTerms.isActive() ||
			 (!searchTerms.isActive() && PropsValues.USERS_DELETE))) {

			return true;
		}

		return false;
	}

	public boolean isShowRestoreButton() {
		UserSearchTerms searchTerms =
			(UserSearchTerms)getSearchContainer().getSearchTerms();

		if ((searchTerms.getStatus() != WorkflowConstants.STATUS_ANY) &&
			!searchTerms.isActive()) {

			return true;
		}

		return false;
	}

	public boolean showCreationMenu() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return PortalPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), ActionKeys.ADD_USER);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewAccountsManagementToolbarDisplayContext.class);

	private final PortletURL _currentURL;
	private final HttpServletRequest _httpServletRequest;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private UserSearch _userSearch;

}