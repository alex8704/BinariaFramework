package co.com.binariasystems.webtestapp.dto;

import java.util.Collection;

import co.com.binariasystems.fmw.vweb.uicomponet.treemenu.MenuElement;

public class MenuModuleDTO extends MenuElement{
	
	public MenuModuleDTO() {}
	
	public MenuModuleDTO(String caption) {
		super(caption);
	}

	public MenuModuleDTO(String caption, String description) {
		super(caption, description);
	}
	
	public MenuModuleDTO(String caption, String description, MenuElement... childs) {
		super(caption, description);
		addChilds(childs);
	}
	
	public MenuModuleDTO(String caption, String description, Collection<MenuElement> childs) {
		super(caption, description);
		addChilds(childs);
	}
	
}
