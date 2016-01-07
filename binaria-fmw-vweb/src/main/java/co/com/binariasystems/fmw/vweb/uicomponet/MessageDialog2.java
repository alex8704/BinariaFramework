package co.com.binariasystems.fmw.vweb.uicomponet;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class MessageDialog2 extends Window{
	private String caption;
	
	private void initContent(){
		GridLayout content = new GridLayout(2, 3);
		Label topLabel = new Label();
		
		setWidth(450.0f, Unit.PIXELS);
		setModal(true);
		
	}


	public static enum DialogType{
		ERROR("fmw-dialog-error", "Error.gif"), 
		INFORMATION("fmw-dialog-information", "Inform.gif"), 
		PLANE("fmw-dialog-plane", null), 
		WARNING("fmw-dialog-warning", "Warn.gif"), 
		QUESTION("fmw-dialog-question", "Question.gif");
		
		String cssClass;
		String iconPath;
		DialogType(String cssClass, String iconPath){
			this.cssClass = cssClass;
			this.iconPath = iconPath;
		}
	}
}
