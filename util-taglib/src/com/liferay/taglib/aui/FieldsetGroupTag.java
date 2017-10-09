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

package com.liferay.taglib.aui;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.aui.base.BaseFieldsetGroupTag;

import javax.servlet.jsp.JspWriter;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 */
public class FieldsetGroupTag extends BaseFieldsetGroupTag {

	@Override
	protected String getEndPage() {
		String markupView = getMarkupView();

		if (Validator.isNotNull(markupView) && !markupView.equals("lexicon")) {
			return "/html/taglib/aui/fieldset_group/" + markupView + "/end.jsp";
		}

		return "/html/taglib/aui/fieldset_group/end.jsp";
	}

	@Override
	protected String getStartPage() {
		String markupView = getMarkupView();

		if (Validator.isNotNull(markupView) && !markupView.equals("lexicon")) {
			return "/html/taglib/aui/fieldset_group/" + markupView +
				"/start.jsp";
		}

		return "/html/taglib/aui/fieldset_group/start.jsp";
	}

	@Override
	protected int processEndTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("</div></div>");

		return EVAL_PAGE;
	}

}