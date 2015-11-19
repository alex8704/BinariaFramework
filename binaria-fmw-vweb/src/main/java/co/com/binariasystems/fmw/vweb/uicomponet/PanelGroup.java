package co.com.binariasystems.fmw.vweb.uicomponet;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.vweb.util.VWebUtils;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class PanelGroup extends HorizontalLayout{
	protected List<Component> childComponents = new LinkedList<Component>();
	private Dimension fixedWidth = Dimension.percent(100);
	public GridLayout content;
	private int columns;
	private int currentRow;
	private int currentColumn;
	
	
	public PanelGroup() {
		this(1);
	}
	public PanelGroup(int columns) {
		super();
		this.columns = columns <= 0 ? 1 : columns;
		initContent();
	}
	
	private void initContent(){
		content = new GridLayout(columns, 1);
		content.setSpacing(true);
		addComponent(content);
		
		setWidth(fixedWidth.value, fixedWidth.unit);
		setComponentAlignment(content, Alignment.TOP_CENTER);
		setExpandRatio(content, 1.0f);
		setMargin(true);
	}
	
	public PanelGroup setColumnExpandRatio(int columnIndex, float ratio){
		content.setColumnExpandRatio(columnIndex, ratio);
		return this;
	}
	
	public PanelGroup add(Component component){
		add(component, 1);
		return this;
	}
	
	public PanelGroup add(Component component, Dimension width){
		add(component, 1, null, width);
		return this;
	}
	
	public PanelGroup add(Component component, Alignment align){
		add(component, 1, align, null);
		return this;
	}
	
	public PanelGroup add(Component component, Alignment align, Dimension width){
		add(component, 1, align, width, null);
		return this;
	}
	
	public PanelGroup add(Component component, int colSpan){
		add(component, colSpan, null, null);
		return this;
	}
	
	public PanelGroup add(Component component, int colSpan, Alignment align){
		add(component, colSpan, align, null);
		return this;
	}
	
	public PanelGroup add(Component component, int colSpan, Dimension width){
		add(component, colSpan, null, width);
		return this;
	}
	
	public PanelGroup add(Component component, int colSpan, Alignment align, Dimension width){
		add(component, colSpan, align, width, null);
		return this;
	}
	
	public PanelGroup addCenteredOnNewRow(Component... components){
		return addCenteredOnNewRow(null, components);
	}
	
	public PanelGroup addEmptyRow(){
		return addCenteredOnNewRow(new Label("&nbsp;", ContentMode.HTML));
	}
	
	public PanelGroup addCenteredOnNewRow(Dimension uniformWidth, Component... components){
		Component comp = null;
		if(currentColumn > 0)
			createNewRow();
		if(components.length == 1){
			comp = components[0];
			if(uniformWidth != null)
				comp.setWidth(uniformWidth.value, uniformWidth.unit);
		}else{
			HorizontalLayout layout = new HorizontalLayout();
			layout.setSpacing(true);
			if(uniformWidth == null)
				layout.addComponents(components);
			else{
				for(Component component : components){
					component.setWidth(uniformWidth.value, uniformWidth.unit);
					layout.addComponent(component);
				}
			}
			comp = layout;
		}
		
		add(comp, columns, Alignment.MIDDLE_CENTER);
		return this;
	}
	
	public PanelGroup add(Component component, int colSpan, Alignment align, Dimension width, Dimension height){
		validateComponentAlreadyAdded(component);
		int realColSpan = colSpan <= 0 ? 1 : colSpan;
		validateSpaceAndPosition(realColSpan);
		if(component instanceof PanelGroup || component instanceof FormPanel){
			((HorizontalLayout)component).setMargin(false);
		}
		content.addComponent(component, currentColumn, currentRow, currentColumn + (realColSpan - 1), currentRow);
		content.setComponentAlignment(component, align != null ? align : Alignment.TOP_LEFT);
		if(width != null)
			component.setWidth(width.value, width.unit);
		if(height != null)
			component.setHeight(height.value, height.unit);
		
		addChildComponent(component);
		currentColumn += realColSpan;
		return this;
	}
	
	
	
	private void validateComponentAlreadyAdded(Component component){
		if(ComponentContainer.class.isAssignableFrom(component.getClass())){
			Iterator<Component> compIt = ((ComponentContainer)component).iterator();
			for(;compIt.hasNext();)
				validateComponentAlreadyAdded(compIt.next());
		}
		else if(VWebUtils.isFocusableControlClass(component.getClass()) && childComponents.contains(component))
			throw new FMWUncheckedException("Component '"+component.getCaption()+"' of type "+component.getClass().getSimpleName()+"is already added on this container");
	}
	
	private void addChildComponent(Component component){
		if(ComponentContainer.class.isAssignableFrom(component.getClass())){
			Iterator<Component> compIt = ((ComponentContainer)component).iterator();
			for(;compIt.hasNext();)
				addChildComponent(compIt.next());
		}
		else if(VWebUtils.isFocusableControlClass(component.getClass())){
			childComponents.add(component);
		}
			
	}
	
	private void validateSpaceAndPosition(int colSpan){
		if(currentColumn >= columns)
			createNewRow();
		int availableCells = columns - currentColumn;
		if(colSpan > availableCells)
			throw new FMWUncheckedException("colSpan cause an overlap in container, availableColumns = "+availableCells+", requiredColumns = "+colSpan);
	}
	
	protected void setColumns(int columns){
		this.columns = columns <= 0 ? 1 : columns;
		content.setColumns(columns);
	}
	
	private void createNewRow(){
		currentRow += 1;
		content.setRows(content.getRows() + 1);
		currentColumn = 0;
	}
	
	public PanelGroup setWidth(Dimension width){
		content.setWidth(width.value, width.unit);
		return this;
	}
	
	@Override
	public void setWidth(float width, Unit unit) {
		super.setWidth(fixedWidth.value, fixedWidth.unit);
		content.setWidth(width, unit);
	}
	
	@Override
	public void setWidth(String width) {
		super.setWidth("100%");
		content.setWidth(width);
	}
}
