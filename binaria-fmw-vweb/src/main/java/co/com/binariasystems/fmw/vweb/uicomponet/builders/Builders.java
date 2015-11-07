package co.com.binariasystems.fmw.vweb.uicomponet.builders;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import co.com.binariasystems.fmw.vweb.uicomponet.FormPanel;
import co.com.binariasystems.fmw.vweb.uicomponet.SearcherField;
import co.com.binariasystems.fmw.vweb.uicomponet.TreeMenu;
import co.com.binariasystems.fmw.vweb.uicomponet.treemenu.MenuElement;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button.ClickListener;

public class Builders {
	
	public static MenuButtonBuilder menuButton(Resource icon){
		return new MenuButtonBuilder(icon);
	}
	
	public static MenuButtonBuilder menuButton(String caption){
		return new MenuButtonBuilder(caption);
	}
	
	public static MenuButtonBuilder menuButton(String caption, Resource icon){
		return new MenuButtonBuilder(caption,icon);
	}
	
	public static ButtonBuilder button(){
		return new ButtonBuilder();
	}
	
	public static ButtonBuilder button(Resource icon){
		return new ButtonBuilder(icon);
	}
	
	public static ButtonBuilder button(String caption){
		return new ButtonBuilder(caption);
	}
	
	public static ButtonBuilder button(String caption, ClickListener clickListener){
		return new ButtonBuilder(caption, clickListener);
	}
	
	public static ButtonBuilder button(String caption, Resource icon){
		return new ButtonBuilder(caption, icon);
	}
	
	public static ComboBoxBuilder comboBox(){
		return new ComboBoxBuilder();
	}
	
	public static ComboBoxBuilder comboBox(String caption){
		return new ComboBoxBuilder(caption);
	}
	
	public static ComboBoxBuilder comboBox(String caption, Collection<?> options){
		return new ComboBoxBuilder(caption, options);
	}
	
	public static ComboBoxBuilder comboBox(String caption, Container dataSource){
		return new ComboBoxBuilder(caption, dataSource);
	}
	
	public static DateFieldBuilder dateField(){
		return new DateFieldBuilder();
	}
	
	public static DateFieldBuilder dateField(String caption){
		return new DateFieldBuilder(caption);
	}
	
	public static DateFieldBuilder dateField(Property dataSource){
		return new DateFieldBuilder(dataSource);
	}
	
	public static DateFieldBuilder dateField(String caption, Date value){
		return new DateFieldBuilder(caption, value);
	}
	
	public static DateFieldBuilder dateField(String caption, Property dataSource){
		return new DateFieldBuilder(caption, dataSource);
	}
	
	public static DatePickerBuilder datePicker(){
		return new DatePickerBuilder();
	}
	
	public static DatePickerBuilder datePicker(String caption){
		return new DatePickerBuilder(caption);
	}
	
	public static DatePickerBuilder datePicker(Property dataSource){
		return new DatePickerBuilder(dataSource);
	}
	
	public static DatePickerBuilder datePicker(String caption, Date value){
		return new DatePickerBuilder(caption, value);
	}
	
	public static DatePickerBuilder datePicker(String caption, Property dataSource){
		return new DatePickerBuilder(caption, dataSource);
	}
	
	public static LabelBuilder label(){
		return new LabelBuilder();
	}
	
	public static LabelBuilder label(String caption){
		return new LabelBuilder(caption);
	}
	
	public static LabelBuilder label(Property dataSource){
		return new LabelBuilder(dataSource);
	}
	
	public static LabelBuilder label(String caption, ContentMode contentMode){
		return new LabelBuilder(caption, contentMode);
	}
	
	public static LabelBuilder label(Property dataSource, ContentMode contentMode){
		return new LabelBuilder(dataSource, contentMode);
	}
	
	public static ListBuilder list(){
		return new ListBuilder();
	}
	
	public static ListBuilder list(String caption){
		return new ListBuilder(caption);
	}
	
	public static ListBuilder list(String caption, Collection<?> options){
		return new ListBuilder(caption, options);
	}
	
	public static ListBuilder list(String caption, Container dataSource){
		return new ListBuilder(caption, dataSource);
	}
	
	public static OptionGroupBuilder optionGroup(){
		return new OptionGroupBuilder();
	}
	
	public static OptionGroupBuilder optionGroup(String caption){
		return new OptionGroupBuilder(caption);
	}
	
	public static OptionGroupBuilder optionGroup(String caption, Collection<?> options){
		return new OptionGroupBuilder(caption, options);
	}
	
	public static OptionGroupBuilder optionGroup(String caption, Container dataSource){
		return new OptionGroupBuilder(caption, dataSource);
	}
	
	public static PasswordFieldBuilder passwordField(){
		return new PasswordFieldBuilder();
	}
	
	public static PasswordFieldBuilder passwordField(String caption){
		return new PasswordFieldBuilder(caption);
	}
	
	public static PasswordFieldBuilder passwordField(Property dataSource){
		return new PasswordFieldBuilder(dataSource);
	}
	
	public static PasswordFieldBuilder passwordField(String caption, Property dataSource){
		return new PasswordFieldBuilder(caption, dataSource);
	}
	
	public static PasswordFieldBuilder passwordField(String caption, String value){
		return new PasswordFieldBuilder(caption, value);
	}
	
	public static TextFieldBuilder textField(){
		return new TextFieldBuilder();
	}
	
	public static TextFieldBuilder textField(String caption){
		return new TextFieldBuilder(caption);
	}
	
	public static TextFieldBuilder textField(Property dataSource){
		return new TextFieldBuilder(dataSource);
	}
	
	public static TextFieldBuilder textField(String caption, Property dataSource){
		return new TextFieldBuilder(caption, dataSource);
	}
	
	public static TextFieldBuilder textField(String caption, String value){
		return new TextFieldBuilder(caption, value);
	}
	
	public static SelectBuilder select(){
		return new SelectBuilder();
	}
	
	public static SelectBuilder select(String caption){
		return new SelectBuilder(caption);
	}
	
	public static SelectBuilder select(String caption, Collection<?> options){
		return new SelectBuilder(caption, options);
	}
	
	public static SelectBuilder select(String caption, Container dataSource){
		return new SelectBuilder(caption, dataSource);
	}
	
	public static TwinColSelectBuilder twinColSelect(){
		return new TwinColSelectBuilder();
	}
	
	public static TwinColSelectBuilder twinColSelect(String caption){
		return new TwinColSelectBuilder(caption);
	}
	
	public static TwinColSelectBuilder twinColSelect(String caption, Collection<?> options){
		return new TwinColSelectBuilder(caption, options);
	}
	
	public static TwinColSelectBuilder twinColSelect(String caption, Container dataSource){
		return new TwinColSelectBuilder(caption, dataSource);
	}
	
	public static TreeMenu treeMenu(){
		return new TreeMenu();
	}
	
	public static TreeMenu treeMenu(String title, List<MenuElement> items){
		return new TreeMenu(title, items);
	}
	
	public static TreeMenu treeMenu(String title, List<MenuElement> items, boolean showSearcher){
		return new TreeMenu(title, items, showSearcher);
	}
	
	public static <T> SearcherField<T> searcherField(Class<T> entityClass, String caption){
		return new SearcherField<T>(entityClass, caption);
	}
	
	public static FormPanel form(){
		return new FormPanel(1);
	}
	
	public static FormPanel form(int columns){
		return new FormPanel(columns);
	}
	
	public static FormPanel form(String title){
		return new FormPanel(title);
	}
	
	public static FormPanel form(int columns, String title){
		return new FormPanel(columns, title);
	}
}
