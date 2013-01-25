/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.security.pacl.checker;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.net.SocketPermission;

import java.security.Permission;
import java.security.Permissions;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public class SocketChecker extends BaseChecker {

	public void afterPropertiesSet() {
		initAcceptHostsAndPorts();
		initConnectHostsAndPorts();
		initListenPorts();
	}

	public void checkPermission(Permission permission) {
		String actions = permission.getActions();
		String name = permission.getName();

		if (!_permissions.implies(permission)) {
			throwSecurityException(
				_log, "Attempted " + actions + " for address " + name);
		}
	}

	protected void initAcceptHostsAndPorts() {
		String[] networkParts = getPropertyArray(
			"security-manager-sockets-accept");

		for (String networkPart : networkParts) {
			initHostsAndPorts(networkPart, SOCKET_PERMISSION_ACCEPT);
		}
	}

	protected void initConnectHostsAndPorts() {
		String[] networkParts = getPropertyArray(
			"security-manager-sockets-connect");

		for (String networkPart : networkParts) {
			initHostsAndPorts(networkPart, SOCKET_PERMISSION_CONNECT);
		}
	}

	protected void initHostsAndPorts(String networkPart, String action) {
		SocketPermission socketPermission = new SocketPermission(
			networkPart, action);

		_permissions.add(socketPermission);
	}

	protected void initListenPorts() {
		String[] listenParts = getPropertyArray(
			"security-manager-sockets-listen");

		for (String listenPart : listenParts) {
			initListenPorts(listenPart);
		}
	}

	protected void initListenPorts(String listenPart) {
		initHostsAndPorts("*:" + listenPart, SOCKET_PERMISSION_LISTEN);
	}

	private static Log _log = LogFactoryUtil.getLog(SocketChecker.class);

	private Permissions _permissions = new Permissions();

}