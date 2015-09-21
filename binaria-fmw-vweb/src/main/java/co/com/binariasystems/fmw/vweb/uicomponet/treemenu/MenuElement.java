package co.com.binariasystems.fmw.vweb.uicomponet.treemenu;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public abstract class MenuElement implements Serializable{
	protected String caption;
	protected String description;
	private List<MenuElement> childs;
	
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void addChild(MenuElement child){
		if(child != null)
			getChilds().add(child);
	}
	public List<MenuElement> getChilds() {
		if(childs == null)
			childs = new LinkedList<MenuElement>();
		return childs;
	}
}
