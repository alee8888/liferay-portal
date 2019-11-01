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
SearchContainer accountDisplaySearchContainer = AccountDisplaySearchContainerFactory.create(liferayPortletRequest, liferayPortletResponse);

accountDisplaySearchContainer.setRowChecker(null);

SelectAnAccountManagementToolbarDisplayContext selectAnAccountManagementToolbarDisplayContext = new SelectAnAccountManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, accountDisplaySearchContainer);
%>

<clay:management-toolbar
	displayContext="<%= selectAnAccountManagementToolbarDisplayContext %>"
	showCreationMenu="<%= false %>"
/>

<aui:container cssClass="container-fluid container-fluid-max-xl">
	<aui:form method="post" name="fm">
		<aui:input name="accountEntryIds" type="hidden" />

		<liferay-ui:search-container
			searchContainer="<%= accountDisplaySearchContainer %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.account.admin.web.internal.display.AccountDisplay"
				keyProperty="accountId"
				modelVar="accountDisplay"
			>
				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand table-title"
					name="name"
					property="name"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					name="parent-account"
					property="parentAccountName"
				/>

				<liferay-ui:search-container-column-text>

					<%
					Map<String, Object> data = new HashMap<String, Object>();

					data.put("accountentryid", accountDisplay.getAccountId());
					%>

					<aui:button cssClass="choose-account selector-button" data="<%= data %>" value="choose" />
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</aui:container>