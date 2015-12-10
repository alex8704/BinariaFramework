package co.com.binariasystems.fmw.vweb.uicomponet.addresseditor;

import co.com.binariasystems.fmw.vweb.uicomponet.AddressEditorField;

public enum Quadrant {
	EAST("ESTE", "Este"),
	WEST("OESTE", "Oeste"),
	NORTH("NORTE", "Norte"),
	SOUTH("SUR", "Sur");
	
	private String code;
	private String description;
	private Quadrant(String code, String  description){
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
		.append(Quadrant.class.getSimpleName().toLowerCase())
		.append(".")
		.append(code).toString();
	}
}
