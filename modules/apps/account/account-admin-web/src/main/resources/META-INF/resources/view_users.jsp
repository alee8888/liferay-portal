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
SearchContainer usersDisplaySearchContainer = new UserSearch(liferayPortletRequest, renderResponse.createRenderURL());

List<User> results = new ArrayList<>();

usersDisplaySearchContainer.setResults(results);
usersDisplaySearchContainer.setEmptyResultsMessage("no-users-were-found");

ViewUsersManagementToolbarDisplayContext viewUsersManagementToolbarDisplayContext = new ViewUsersManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, usersDisplaySearchContainer);
%>

<clay:management-toolbar
	displayContext="<%= viewUsersManagementToolbarDisplayContext %>"
/>

<aui:container cssClass="container-fluid container-fluid-max-xl">
	<aui:form method="post" name="fm">
		<aui:input name="userId" type="hidden" />

		<liferay-ui:search-container
			searchContainer="<%= usersDisplaySearchContainer %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.portal.kernel.model.User"
				escapedModel="<%= true %>"
				keyProperty="userId"
				modelVar="accountUser"
			>
				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					name="name"
					value="<%= accountUser.getFullName() %>"
				/>

				<%
				List<EmailAddress> emailAddresses = accountUser.getEmailAddresses();

				String emailAddress = null;

				if (!emailAddresses.isEmpty()) {
					emailAddress = String.valueOf(emailAddresses.get(0));
				}
				%>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					name="email"
					value="<%= emailAddress %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					name="job-title"
					value="<%= accountUser.getJobTitle() %>"
				/>

				<liferay-ui:search-container-column-jsp
					path="/account_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</aui:container>

<liferay-frontend:component
	componentId="<%= viewUsersManagementToolbarDisplayContext.getDefaultEventHandler() %>"
	module="js/UsersManagementToolbarDefaultEventHandler.es"
/>