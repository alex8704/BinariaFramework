package co.com.binariasystems.webtestapp.dto;

import java.util.Collection;
import java.util.List;

import co.com.binariasystems.fmw.vweb.uicomponet.treemenu.MenuElement;

public class MenuOptionDTO extends MenuElement{
	
	public MenuOptionDTO() {}
	
	public MenuOptionDTO(String caption) {
		super(caption);
	}
	
	public MenuOptionDTO(String caption, String description) {
		super(caption, description);
	}

	public MenuOptionDTO(String caption, String description, String path) {
		super(caption, description);
		this.path = path;
	}



	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public static void main(String[] args) {
		Class clazz = Gateway.class;
	}
	
	@Override
	public MenuElement addChild(MenuElement child){
		throw new UnsupportedOperationException(getClass().getName()+" not allow childs");
	}
	
	@Override
	public MenuElement addChilds(MenuElement... child){
		throw new UnsupportedOperationException(getClass().getName()+" not allow childs");
	}
	@Override
	public MenuElement addChilds(Collection<MenuElement> child){
		throw new UnsupportedOperationException(getClass().getName()+" not allow childs");
	}
	
	@Override
	public List<MenuElement> getChilds() {
		return null;
	}
}
