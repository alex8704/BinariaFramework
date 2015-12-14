package co.com.binariasystems.fmw.entity.cfg;

/*** 
 * @author Alexander Castro O.
 */

public enum EntityConfigUIControl {
	TEXTFIELD(1), PASSWORDFIELD(1), DATEFIELD(1), SEARCHBOX(1), COMBOBOX(1), RADIO(2), CHECKBOX(2), TEXTAREA(3), DEFAULT(0);
	
	private int priority;
	
	private EntityConfigUIControl(int priority){
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}
	
	
}
