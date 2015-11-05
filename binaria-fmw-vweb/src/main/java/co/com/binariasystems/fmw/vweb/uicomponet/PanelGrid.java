package co.com.binariasystems.fmw.vweb.uicomponet;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;

public class PanelGrid extends HorizontalLayout{
	private Panel content;
	private PanelGroup panelGroup;
	private Dimension fixedWidth = Dimension.percent(100);
	private int columns;
	private String title;
	
	private Button submitButton;
	private Button resetButton;
	
	
	public PanelGrid(int columns){
		this(columns, null);
	}
	
	public PanelGrid(String title){
		this(1, title);
	}
	
	public PanelGrid(int columns, String title){
		super();
		this.columns = columns <= 0 ? 1 : columns;
		this.title = title;
		
		initContent();
	}
	
	private void initContent(){
		content = new Panel(title);
		panelGroup = new PanelGroup(columns);
		content.setCaptionAsHtml(true);
		content.setContent(panelGroup);
		
		addComponent(content);
		
		setWidth(fixedWidth.value, fixedWidth.unit);
		setComponentAlignment(content, Alignment.TOP_CENTER);
		setExpandRatio(content, 1.0f);
		setMargin(true);
	}
	
	public PanelGrid setColumnExpandRatio(int columnIndex, float ratio){
		panelGroup.setColumnExpandRatio(columnIndex, ratio);
		return this;
	}
	
	public PanelGrid add(Component component){
		panelGroup.add(component);
		return this;
	}
	
	public PanelGrid add(Component component, Dimension width){
		panelGroup.add(component, width);
		return this;
	}
	
	public PanelGrid add(Component component, Alignment align){
		panelGroup.add(component, align);
		return this;
	}
	
	public PanelGrid add(Component component, int colSpan){
		panelGroup.add(component, colSpan);
		return this;
	}
	
	public PanelGrid add(Component component, int colSpan, Alignment align){
		panelGroup.add(component, colSpan, align);
		return this;
	}
	
	public PanelGrid add(Component component, int colSpan, Alignment align, Dimension width){
		panelGroup.add(component, colSpan, align, width);
		return this;
	}
	
	public PanelGrid addCenteredOnNewRow(Component... components){
		panelGroup.addCenteredOnNewRow(components);
		return this;
	}
	
	public PanelGrid addEmptyRow(){
		panelGroup.addEmptyRow();
		return this;
	}
	
	public PanelGrid addCenteredOnNewRow(Dimension uniformWidth, Component... components){
		panelGroup.addCenteredOnNewRow(uniformWidth, components);
		return this;
	}
	
	public PanelGrid add(Component component, int colSpan, Alignment align, Dimension width, Dimension height){
		panelGroup.add(component, colSpan, align, width, height);
		return this;
	}
	
	public PanelGrid setSubmitButton(Button button){
		if(!panelGroup.containsButton(button))
			throw new IllegalArgumentException("You should add the button to form before setSubmitButton");
		if(button.equals(resetButton))
			throw new IllegalArgumentException("ResetButton and SubmitButton cannot be the same Button");
		submitButton = button;
		content.addAction(new ClickShortcut(submitButton, KeyCode.F1));
		return this;
	}

	public PanelGrid setResetButton(Button button){
		if(!panelGroup.containsButton(button))
			throw new IllegalArgumentException("You should add the button to form before setSubmitButton");
		if(button.equals(submitButton))
			throw new IllegalArgumentException("ResetButton and SubmitButton cannot be the same Button");
		resetButton = button;
		content.addAction(new ClickShortcut(resetButton, KeyCode.F4));
		return this;
	}
	
	
	public void initFocus(){
		
	}
	
	public PanelGrid setTitle(String title){
		content.setCaption("");
		return this;
	}
	
	@Override
	public void setCaption(String caption) {
		content.setCaption(caption);
	}
	
	public PanelGrid setWidth(Dimension width){
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
