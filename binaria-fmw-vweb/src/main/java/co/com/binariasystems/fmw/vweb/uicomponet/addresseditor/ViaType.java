package co.com.binariasystems.fmw.vweb.uicomponet.addresseditor;

import co.com.binariasystems.fmw.vweb.uicomponet.AddressEditorField;

public enum ViaType {
	EXPRESSWAY("AUT", "Autopista"),
	AVENUE("AV", "Avenida"),
	ROAD_AVENUE("AK", "Avenida Carrera"),
	BOULEVARD("BLV", "Boulevard"),
	STREET("CL", "Calle"),
	ROAD("CR", "Carrera"),
	HIGHWAY("CARR", "Carretera"),
	CIRCULAR("CIR", "Circular"),
	RING_ROAD("CRV", "Circunvalar"),
	DIAGONAL("DG", "Diagonal"),
	PASSAGEWAY("PJ", "Pasaje"),
	SHORT_WALK("PS", "Paseo"),
	TRANSVERSE("TV", "Transaversal"),
	DETOUR("VTE", "Variante");
	
	private String code;
	private String description;
	private ViaType(String code, String  description){
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
		.append(ViaType.class.getSimpleName().toLowerCase())
		.append(".")
		.append(code).toString();
	}
	
}
