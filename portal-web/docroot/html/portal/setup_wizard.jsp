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

<%@ include file="/html/portal/init.jsp" %>

<div class="container-fluid-1280" id="wrapper">
	<header id="banner" role="banner">
		<div id="heading">
			<span class="logo" title="<liferay-ui:message key="welcome-to-liferay" />">

				<%
				Group group = layout.getGroup();
				%>

				<img alt="<%= HtmlUtil.escapeAttribute(group.getDescriptiveName(locale)) %>" height="<%= themeDisplay.getCompanyLogoHeight() %>" src="<%= HtmlUtil.escape(themeDisplay.getCompanyLogo()) %>" width="<%= themeDisplay.getCompanyLogoWidth() %>" />

				<span class="site-name">
					<%= PropsValues.COMPANY_DEFAULT_NAME %>
				</span>
			</span>

			<span class="label label-default pull-right">
				<i class="icon-cogs icon-monospaced"></i>

				<strong class="h4"><liferay-ui:message key="basic-configuration" /></strong>
			</span>
		</div>
	</header>

	<div id="content">
		<div id="main-content">

			<%
			UnicodeProperties unicodeProperties = (UnicodeProperties)session.getAttribute(WebKeys.SETUP_WIZARD_PROPERTIES);
			%>

			<c:choose>
				<c:when test="<%= unicodeProperties == null %>">

					<%
					boolean defaultDatabase = SetupWizardUtil.isDefaultDatabase(request);
					%>

					<aui:form action='<%= themeDisplay.getPathMain() + "/portal/setup_wizard" %>' method="post" name="fm" onSubmit="event.preventDefault();">
						<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />

						<aui:fieldset-group markupView="lexicon">
							<aui:fieldset>
								<div class="col-md-6">
									<h2><liferay-ui:message key="portal" /></h2>

									<aui:input helpTextCssClass="help-inline" label="portal-name" name="companyName" value="<%= PropsValues.COMPANY_DEFAULT_NAME %>">
										<aui:validator name="required" />
									</aui:input>

									<aui:select label="default-language" name="companyLocale">

										<%
										String languageId = GetterUtil.getString((String)session.getAttribute(WebKeys.SETUP_WIZARD_DEFAULT_LOCALE), SetupWizardUtil.getDefaultLanguageId());

										for (Locale curLocale : LanguageUtil.getAvailableLocales()) {
										%>

											<aui:option label="<%= curLocale.getDisplayName(curLocale) %>" selected="<%= languageId.equals(LocaleUtil.toLanguageId(curLocale)) %>" value="<%= LocaleUtil.toLanguageId(curLocale) %>" />

										<%
										}
										%>

									</aui:select>

									<aui:input label="add-sample-data" name="addSampleData" type="toggle-switch" value="<%= true %>" />
								</div>

								<div class="col-md-6">
									<h2><liferay-ui:message key="administrator-user" /></h2>

									<aui:input label="first-name" name="adminFirstName" value="<%= PropsValues.DEFAULT_ADMIN_FIRST_NAME %>">
										<aui:validator name="required" />
									</aui:input>

									<aui:input label="last-name" name="adminLastName" value="<%= PropsValues.DEFAULT_ADMIN_LAST_NAME %>">
										<aui:validator name="required" />
									</aui:input>

									<aui:input label="email" name="adminEmailAddress" value="<%= PropsValues.ADMIN_EMAIL_FROM_ADDRESS %>">
										<aui:validator name="email" />
										<aui:validator name="required" />
									</aui:input>
								</div>
							</aui:fieldset>

							<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label='<%= defaultDatabase ? "default-database" : "configured-database" %>'>
								<aui:input name="defaultDatabase" type="hidden" value="<%= defaultDatabase %>" />

								<c:choose>
									<c:when test="<%= defaultDatabase %>">
										<strong><liferay-ui:message key="database.hypersonic" /></strong>

										<liferay-ui:message key="this-database-is-useful-for-development-and-demo'ing-purposes" />
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="<%= Validator.isNotNull(PropsValues.JDBC_DEFAULT_JNDI_NAME) %>">
												<h4 class="text-default" title="<liferay-ui:message key="jdbc-default-jndi-name" />">
													<liferay-ui:message key="jdbc-default-jndi-name" />
												</h4>

												<p class="text-default">
													<%= PropsValues.JDBC_DEFAULT_JNDI_NAME %>
												</p>
											</c:when>
											<c:otherwise>
												<h4 class="text-default" title="<liferay-ui:message key="jdbc-url" />">
													<liferay-ui:message key="jdbc-url" />
												</h4>

												<p class="text-default">
													<%= PropsValues.JDBC_DEFAULT_URL %>
												</p>

												<h4 class="text-default" title="<liferay-ui:message key="jdbc-driver-class-name" />">
													<liferay-ui:message key="jdbc-driver-class-name" />
												</h4>

												<p class="text-default">
													<%= PropsValues.JDBC_DEFAULT_DRIVER_CLASS_NAME %>
												</p>

												<h4 class="text-default" title="<liferay-ui:message key="user-name" />">
													<liferay-ui:message key="user-name" />
												</h4>

												<p class="text-default">
													<%= PropsValues.JDBC_DEFAULT_USERNAME %>
												</p>

												<h4 class="text-default" title="<liferay-ui:message key="password" />">
													<liferay-ui:message key="password" />
												</h4>

												<p class="text-default">
													<%= StringPool.EIGHT_STARS %>
												</p>
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>

								<c:if test="<%= Validator.isNull(PropsValues.JDBC_DEFAULT_JNDI_NAME) %>">
									<aui:a cssClass="btn btn-default" href="https://dev.liferay.com/discover/deployment/-/knowledge_base/7-0/installing-liferay-manually" target="_blank">
										<liferay-ui:message key="change" />
									</aui:a>
								</c:if>
							</aui:fieldset>
						</aui:fieldset-group>

						<aui:button-row>
							<aui:button cssClass="btn-lg" name="finishButton" type="submit" value="finish-configuration" />
						</aui:button-row>
					</aui:form>

					<aui:script use="aui-base,aui-io-request,aui-loading-mask-deprecated">
						var command = A.one('#<%= Constants.CMD %>');
						var setupForm = A.one('#fm');

						A.one('#companyLocale').on(
							'change',
							function(event) {
								command.val('<%= Constants.TRANSLATE %>');

								setupForm.submit();
							}
						);

						var loadingMask = new A.LoadingMask(
							{
								'strings.loading': '<%= UnicodeLanguageUtil.get(request, "liferay-is-being-installed") %>',
								target: A.getBody()
							}
						);

						var startInstall = function() {
							loadingMask.show();
						};

						A.one('#fm').on(
							'submit',
							function(event) {
								startInstall();

								command.val('<%= Constants.UPDATE %>');

								submitForm(document.fm);
							}
						);
					</aui:script>
				</c:when>
				<c:otherwise>

					<%
					boolean propertiesFileCreated = GetterUtil.getBoolean((Boolean)session.getAttribute(WebKeys.SETUP_WIZARD_PROPERTIES_FILE_CREATED));
					%>

					<c:choose>
						<c:when test="<%= propertiesFileCreated %>">
							<div class="alert alert-success">
								<liferay-ui:message key="your-configuration-was-saved-sucessfully" />
							</div>

							<p class="lfr-setup-notice">

								<%
								String taglibArguments = "<span class=\"lfr-inline-code\">" + PropsValues.LIFERAY_HOME + StringPool.SLASH + SetupWizardUtil.PROPERTIES_FILE_NAME + "</span>";
								%>

								<liferay-ui:message arguments="<%= taglibArguments %>" key="the-configuration-was-saved-in" translateArguments="<%= false %>" />
							</p>

							<%
							boolean passwordUpdated = GetterUtil.getBoolean((Boolean)session.getAttribute(WebKeys.SETUP_WIZARD_PASSWORD_UPDATED));
							%>

							<c:if test="<%= !passwordUpdated %>">
								<p class="lfr-setup-notice">
									<liferay-ui:message arguments="<%= PropsValues.DEFAULT_ADMIN_PASSWORD %>" key="your-password-is-x.-you-will-be-required-to-change-your-password-the-next-time-you-log-into-the-portal" translateArguments="<%= false %>" />
								</p>
							</c:if>

							<div class="alert alert-info">
								<liferay-ui:message key="changes-will-take-effect-once-the-portal-is-restarted-please-restart-the-portal-now" />
							</div>
						</c:when>
						<c:otherwise>
							<p>
								<div class="alert alert-warning">

									<%
									String taglibArguments = "<span class=\"lfr-inline-code\">" + PropsValues.LIFERAY_HOME + "</span>";
									%>

									<liferay-ui:message arguments="<%= taglibArguments %>" key="sorry,-we-were-not-able-to-save-the-configuration-file-in-x" translateArguments="<%= false %>" />
								</div>
							</p>

							<aui:input cssClass="properties-text" label="" name="portal-ext" type="textarea" value="<%= unicodeProperties.toString() %>" wrap="soft" />
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>