package co.com.binariasystems.fmw.vweb.uicomponet.addresseditor;

import co.com.binariasystems.fmw.vweb.uicomponet.AddressEditorField;

public enum Bis {
	BIS("BIS", "Bis");
	
	private String code;
	private String description;
	private Bis(String code, String  description){
		this.code = code;
		this.description = description;
	}
	public String getCode() {
		return code;
	}
	public String getDescription() {
		return description;
	}
	
	public String getLocalizedDescKey(){
		return new StringBuilder()
		.append(AddressEditorField.class.getSimpleName().toLowerCase())
		.append(".")
		.append(Bis.class.getSimpleName().toLowerCase())
		.append(".")
		.append(code).toString();
	}
}
