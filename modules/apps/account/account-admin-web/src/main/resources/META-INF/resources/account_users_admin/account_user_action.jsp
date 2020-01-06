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
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

AccountUserDisplay accountUserDisplay = (AccountUserDisplay)row.getObject();

long userId = accountUserDisplay.getUserId();
%>

<liferay-ui:icon-menu
	direction="left-side"
	icon="<%= StringPool.BLANK %>"
	markupView="lexicon"
	message="<%= StringPool.BLANK %>"
	showWhenSingleIcon="<%= true %>"
>

	<%
	boolean hasUpdatePermission = UserPermissionUtil.contains(permissionChecker, userId, ActionKeys.UPDATE);
	%>

	<c:if test="<%= hasUpdatePermission %>">
		<portlet:renderURL var="editUserURL">
			<portlet:param name="mvcRenderCommandName" value="/account_admin/edit_account_user" />
			<portlet:param name="backURL" value="<%= currentURL %>" />
			<portlet:param name="p_u_i_d" value="<%= String.valueOf(userId) %>" />
			<portlet:param name="userId" value="<%= String.valueOf(userId) %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			message="edit"
			url="<%= editUserURL %>"
		/>
	</c:if>

	<c:if test="<%= !PropsValues.PORTAL_JAAS_ENABLE && PropsValues.PORTAL_IMPERSONATION_ENABLE && (userId != user.getUserId()) && !themeDisplay.isImpersonated() && UserPermissionUtil.contains(permissionChecker, userId, ActionKeys.IMPERSONATE) %>">
		<liferay-security:doAsURL
			doAsUserId="<%= userId %>"
			var="impersonateUserURL"
		/>

		<liferay-ui:icon
			message="impersonate-user"
			target="_blank"
			url="<%= impersonateUserURL %>"
		/>
	</c:if>

	<c:if test="<%= UserPermissionUtil.contains(permissionChecker, userId, ActionKeys.DELETE) %>">
		<c:if test="<%= accountUserDisplay.getStatus() == WorkflowConstants.STATUS_INACTIVE %>">
			<portlet:actionURL name="/account_admin/edit_account_users" var="restoreUserURL">
				<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.RESTORE %>" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="accountEntriesNavigation" value='<%= ParamUtil.getString(request, "accountEntriesNavigation") %>' />
				<portlet:param name="accountUserIds" value="<%= String.valueOf(userId) %>" />
			</portlet:actionURL>

			<liferay-ui:icon
				message="activate"
				url="<%= restoreUserURL %>"
			/>
		</c:if>

		<portlet:actionURL name="/account_admin/edit_account_users" var="deleteUserURL">
			<portlet:param name="<%= Constants.CMD %>" value="<%= (accountUserDisplay.getStatus() == WorkflowConstants.STATUS_APPROVED) ? Constants.DEACTIVATE : Constants.DELETE %>" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="accountEntriesNavigation" value='<%= ParamUtil.getString(request, "accountEntriesNavigation") %>' />
			<portlet:param name="accountUserIds" value="<%= String.valueOf(userId) %>" />
		</portlet:actionURL>

		<c:if test="<%= userId != user.getUserId() %>">
			<c:choose>
				<c:when test="<%= accountUserDisplay.getStatus() == WorkflowConstants.STATUS_APPROVED %>">
					<liferay-ui:icon-delete
						confirmation="are-you-sure-you-want-to-deactivate-this-user"
						message="deactivate"
						url="<%= deleteUserURL %>"
					/>
				</c:when>
				<c:when test="<%= (accountUserDisplay.getStatus() == WorkflowConstants.STATUS_INACTIVE) && PropsValues.USERS_DELETE %>">
					<liferay-ui:icon-delete
						confirmation="are-you-sure-you-want-to-delete-this-user"
						url="<%= deleteUserURL %>"
					/>
				</c:when>
			</c:choose>
		</c:if>
	</c:if>
</liferay-ui:icon-menu>