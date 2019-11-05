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

import {DefaultEventHandler, ItemSelectorDialog} from 'frontend-js-web';

class UsersManagementToolbarDefaultEventHandler extends DefaultEventHandler {
	selectAccount(itemData) {
		this._openAccountSelector(
			Liferay.Language.get('select'),
			this.ns('selectAccounts'),
			Liferay.Language.get(itemData.portletTitle),
			itemData.accountSelectorURL,
			selectedItems => {
				var portletURL = Liferay.Util.PortletURL.createPortletURL(
					itemData.redirectURL,
					{
						accountEntryIds: selectedItems.value,
						accountNavigation: 'accounts'
					}
				);

				window.location.href = portletURL;
			}
		);
	}

	selectAnAccount(itemData) {
		Liferay.Util.selectEntity(
			{
				dialog: {
					constrain: true,
					modal: true
				},
				eventName: this.ns('selectAnAccount'),
				id: this.ns('selectAnAccount'),
				title: Liferay.Language.get(itemData.portletTitle),
				uri: itemData.accountSelectorURL
			},
			event => {
				var addNewAccountUserURL =
					Liferay.Util.PortletURL.createPortletURL;

				addNewAccountUserURL.setParamter(
					'mvcRenderCommandName',
					'/account_admin/add_account_user'
				);
				addNewAccountUserURL.setParamter(
					'backURL',
					itemData.redirectURL
				);

				addNewAccountUserURL,
					{
						accountEntryIds: event.accountEntryId
					};

				window.location.href = addNewAccountUserURL;
			}
		);
	}

	_openAccountSelector(
		dialogButtonLabel,
		eventName,
		dialogTitle,
		accountSelectorURL,
		callback
	) {
		const itemSelectorDialog = new ItemSelectorDialog({
			buttonAddLabel: dialogButtonLabel,
			eventName: this.ns('selectAccounts'),
			title: dialogTitle,
			url: accountSelectorURL
		});

		itemSelectorDialog.on('selectedItemChange', event => {
			if (event.selectedItem) {
				callback(event.selectedItem);
			}
		});

		itemSelectorDialog.open();
	}
}

export default UsersManagementToolbarDefaultEventHandler;
