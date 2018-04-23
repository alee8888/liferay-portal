package ${packagePath}.uad.display;

import ${apiPackagePath}.model.${entity.name};
import ${packagePath}.uad.constants.${portletShortName}UADConstants;

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.user.associated.data.display.UADDisplay;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author ${author}
 * @generated
 */
@Component(
	immediate = true,
	property = {"model.class.name=" + ${portletShortName}UADConstants.CLASS_NAME_${entity.constantName}},
	service = UADDisplay.class
)
public class ${entity.name}UADDisplay implements UADDisplay<${entity.name}> {

	public String getApplicationName() {
		return ${portletShortName}UADConstants.APPLICATION_NAME;
	}

	public String[] getDisplayFieldNames() {
		return _${entity.varName}UADDisplayHelper.getDisplayFieldNames();
	}

	@Override
	public String getEditURL(${entity.name} ${entity.varName}, LiferayPortletRequest liferayPortletRequest, LiferayPortletResponse liferayPortletResponse) throws Exception {
		return _${entity.varName}UADDisplayHelper.get${entity.name}EditURL(${entity.varName}, liferayPortletRequest, liferayPortletResponse);
	}

	public String getKey() {
		return ${portletShortName}UADConstants.CLASS_NAME_${entity.constantName};
	}

	@Override
	public Map<String, Object> getNonanonymizableFieldValues(${entity.name} ${entity.varName}) {
		return _${entity.varName}UADDisplayHelper.getUADEntityNonanonymizableFieldValues(${entity.varName});
	}

	@Override
	public String getTypeName(Locale locale) {
		return "${entity.name}";
	}

	@Reference
	private ${entity.name}UADDisplayHelper _${entity.varName}UADDisplayHelper;

}