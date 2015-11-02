package co.com.binariasystems.fmw.vweb.constants;

import co.com.binariasystems.fmw.vweb.resources.images.images;

public interface UIConstants {
	final String FMW_IMAGE_RESOURCES_DIRECTORY = new StringBuilder("/")
	.append(images.class.getPackage().getName().replace(".", "/"))
	.append("/").toString();
	
	final String UIFORM_PANEL_CSSCLASS = "v-grform-panel";
	final String THEME_IMAGES_DIRECTORY = "img/";
	final String PAGER_STYLENAME = "gr-pager";
	final String PAGER_LINKS_STYLENAME = "gr-pager-link";
	final String REQUIRED_INDICATOR_STYLENAME = "v-required-field-indicator";
	
	final String CONVENTION_PROPERTY_CAPTION = "caption";
	final String CONVENTION_PROPERTY_DESCRIPTION = "description";
	final String CONVENTION_PROPERTY_TITLE = "title";
	final String UI_CONVENTION_STRINGS_TEMPLATE = "{0}.{1}.{2}";
	
	final String CENTER_ALIGN_STYLE = "vweb-center-align";
	final String RIGHT_ALIGN_STYLE = "vweb-right-align";
}
