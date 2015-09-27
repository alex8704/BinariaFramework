package co.com.binariasystems.fmw.vweb.uicomponet;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.uicomponet.treemenu.MenuElement;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ItemStyleGenerator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class TreeMenu extends VerticalLayout {
	private TextField searchTxT;
	private Tree tree;
	private String title;
	private HierarchicalContainer treeDs;
	private boolean showSearcher;
	private List<MenuElement> items;
	private ItemDescriptionGenerator descriptionsGenerator;
	private ItemStyleGenerator styleGenerator;
	private String rootItemsStyleName;
	private String childItemsStyleName;
	
	public TreeMenu(){
		this(null, null);
	}
	
	public TreeMenu(String title, List<MenuElement> items){
		this(title, items, false);
	}
	
	public TreeMenu(String title, List<MenuElement> items, boolean showSearcher){
		buildComponent();
		setTitle(title);
		if(showSearcher)
			setShowSearcher();
		setItems(items);
		
	}
	
	private void buildComponent(){
		searchTxT = new TextField();
		searchTxT.setInputPrompt(VWebUtils.getCommonString(VWebCommonConstants.COMMON_COMPONENTS_SEARCH_LABEL));
		searchTxT.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		searchTxT.setIcon(FontAwesome.SEARCH);
		searchTxT.setWidth(100, Unit.PERCENTAGE);
		
		treeDs = new HierarchicalContainer();
		descriptionsGenerator = new TreeMenuItemDescriptionGenerator();
		styleGenerator = new TreeMenuItemStyleGenerator();
		
		tree = new Tree(title, treeDs);
		tree.setImmediate(true);
		tree.setItemDescriptionGenerator(descriptionsGenerator);
		tree.setItemStyleGenerator(styleGenerator);
		tree.setItemStyleGenerator(null);
		tree.setWidth(100, Unit.PERCENTAGE);
		
		addComponent(searchTxT);
		addComponent(tree);
		
		setHeight(100, Unit.PERCENTAGE);
		setExpandRatio(tree, 1.0f);
		
		searchTxT.setVisible(false);
	}

	public TreeMenu setTitle(String title) {
		tree.setCaption(title);
		return this;
	}

	public TreeMenu setShowSearcher() {
		searchTxT.setVisible(true);
		return this;
	}

	public List<MenuElement> getItems() {
		return items;
	}

	public TreeMenu setItems(List<MenuElement> items) {
		this.items = items;
		treeDs.removeAllItems();
		if(items != null){
			for(MenuElement item : items)
				addItemRecursively(item, null);
		}
		return this;
	}
	
	
	private void addItemRecursively(MenuElement item, MenuElement parent){
		treeDs.addItem(item);
		tree.setItemCaption(item, item.getCaption());
		
		if(parent != null)
			treeDs.setParent(item, parent);
		
		if(item.getChilds() == null || item.getChilds().size() == 0)
			treeDs.setChildrenAllowed(item, false);
		else
			for(MenuElement subItem : item.getChilds())
				addItemRecursively(subItem, item);
	}
	
	public TreeMenu addValueChangeListener(ValueChangeListener valChangeListener){
		if(valChangeListener != null)
			tree.addValueChangeListener(valChangeListener);
		return this;
	}
	
	public TreeMenu removeValueChangeListener(ValueChangeListener valChangeListener){
		if(valChangeListener != null)
			tree.removeValueChangeListener(valChangeListener);
		return this;
	}

	public TreeMenu setRootItemsStyleName(String rootItemsStyleName) {
		this.rootItemsStyleName = rootItemsStyleName;
		setItems(items);
		return this;
	}

	public TreeMenu setChildItemsStyleName(String childItemsStyleName) {
		this.childItemsStyleName = childItemsStyleName;
		setItems(items);
		return this;
	}




	private class TreeMenuItemDescriptionGenerator implements ItemDescriptionGenerator{

		@Override
		public String generateDescription(Component source, Object itemId, Object propertyId) {
			if(itemId != null){
				MenuElement element = (MenuElement)itemId;
				return StringUtils.defaultIfEmpty(element.getDescription(), element.getCaption());
			}
			return null;
		}
		
	}
	
	private class TreeMenuItemStyleGenerator implements ItemStyleGenerator{

		@Override
		public String getStyle(Tree source, Object itemId) {
			String resp = childItemsStyleName;
			if(source.getParent(itemId) == null);
				resp = rootItemsStyleName;
			return resp;
		}
		
	}
}