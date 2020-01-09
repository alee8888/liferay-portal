<%--
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
--%>

<%@ include file="/init.jsp" %>

<%
User selUser = (User)request.getAttribute(AccountWebKeys.SELECTED_USER);
%>

<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (selUser == null) ? Constants.ADD : Constants.UPDATE %>" />

<div class="form-group">
	<h3 class="sheet-subtitle"><liferay-ui:message key="user-display-data" /></h3>

	<liferay-util:include page="/account_users_admin/account_user/user_display_data.jsp" servletContext="<%= application %>" />
</div>

<div class="form-group">
	<h3 class="sheet-subtitle"><liferay-ui:message key="personal-information" /></h3>

	<liferay-util:include page="/user/personal_information.jsp" portletId="<%= UsersAdminPortletKeys.USERS_ADMIN %>" />
</div>

<div class="sheet-section">
	<h3 class="sheet-subtitle"><liferay-ui:message key="more-information" /></h3>

	<div class="form-group">
		<liferay-util:include page="/user/categorization.jsp" portletId="<%= UsersAdminPortletKeys.USERS_ADMIN %>" />
	</div>

	<div class="form-group">
		<liferay-util:include page="/user/comments.jsp" portletId="<%= UsersAdminPortletKeys.USERS_ADMIN %>" />
	</div>
</div>

<div class="sheet-section">
	<liferay-util:include page="/user/custom_fields.jsp" portletId="<%= UsersAdminPortletKeys.USERS_ADMIN %>" />
</div>