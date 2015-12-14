package co.com.binariasystems.fmw.vweb.uicomponet.treemenu;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public abstract class MenuElement implements Serializable{
	protected String caption;
	protected String description;
	private List<MenuElement> childs;
	
	public MenuElement(){}
	
	public MenuElement(String caption){
		this(caption, null);
	}
	
	public MenuElement(String caption, String description){
		this.caption = caption;
		this.description = StringUtils.defaultIfBlank(description, caption);
	}
	
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
	
	public MenuElement addChild(MenuElement child){
		if(child != null)
			getChilds().add(child);
		return this;
	}
	
	public MenuElement addChilds(MenuElement... child){
		if(child != null)
			for(MenuElement ch : child)
				getChilds().add(ch);
		return this;
	}
	
	public MenuElement addChilds(Collection<MenuElement> child){
		getChilds().addAll(child);
		return this;
	}
	
	public List<MenuElement> getChilds() {
		if(childs == null)
			childs = new LinkedList<MenuElement>();
		return childs;
	}
}
