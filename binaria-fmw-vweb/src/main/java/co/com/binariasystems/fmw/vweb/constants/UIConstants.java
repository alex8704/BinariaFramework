package co.com.binariasystems.fmw.vweb.constants;

import co.com.binariasystems.fmw.vweb.resources.images.images;

public interface UIConstants {
	public static final String FMW_IMAGE_RESOURCES_DIRECTORY = new StringBuilder("/")
	.append(images.class.getPackage().getName().replace(".", "/"))
	.append("/").toString();
	
	public static final String UIFORM_PANEL_CSSCLASS = "v-grform-panel";
	public static final String THEME_IMAGES_DIRECTORY = "img/";
	public static final String PAGER_STYLENAME = "gr-pager";
	public static final String PAGER_LINKS_STYLENAME = "gr-pager-link";
	public static final String REQUIRED_INDICATOR_STYLENAME = "v-required-field-indicator";
	
	public static final String CONVENTION_PROPERTY_CAPTION = "caption";
	public static final String CONVENTION_PROPERTY_DESCRIPTION = "description";
	public static final String CONVENTION_PROPERTY_TITLE = "title";
	public static final String UI_CONVENTION_STRINGS_TEMPLATE = "{0}.{1}.{2}";
}
