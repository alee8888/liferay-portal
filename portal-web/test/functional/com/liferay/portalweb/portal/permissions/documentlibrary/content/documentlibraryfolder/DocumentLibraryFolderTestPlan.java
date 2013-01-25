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

package com.liferay.portalweb.portal.permissions.documentlibrary.content.documentlibraryfolder;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portal.permissions.documentlibrary.content.documentlibraryfolder.adddocument.AddFolderDocumentTests;
import com.liferay.portalweb.portal.permissions.documentlibrary.content.documentlibraryfolder.addshortcut.AddFolderShortcutTests;
import com.liferay.portalweb.portal.permissions.documentlibrary.content.documentlibraryfolder.addsubfolder.AddSubfolderTests;
import com.liferay.portalweb.portal.permissions.documentlibrary.content.documentlibraryfolder.delete.DeleteFolderTests;
import com.liferay.portalweb.portal.permissions.documentlibrary.content.documentlibraryfolder.permissions.FolderPermissionsTests;
import com.liferay.portalweb.portal.permissions.documentlibrary.content.documentlibraryfolder.update.UpdateFolderTests;
import com.liferay.portalweb.portal.permissions.documentlibrary.content.documentlibraryfolder.view.ViewFolderTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class DocumentLibraryFolderTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(AddFolderDocumentTests.suite());
		testSuite.addTest(AddFolderShortcutTests.suite());
		testSuite.addTest(AddSubfolderTests.suite());
		testSuite.addTest(DeleteFolderTests.suite());
		testSuite.addTest(FolderPermissionsTests.suite());
		testSuite.addTest(UpdateFolderTests.suite());
		testSuite.addTest(ViewFolderTests.suite());

		return testSuite;
	}

}