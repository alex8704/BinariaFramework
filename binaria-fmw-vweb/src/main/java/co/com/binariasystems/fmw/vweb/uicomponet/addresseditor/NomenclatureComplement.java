package co.com.binariasystems.fmw.vweb.uicomponet.addresseditor;

import co.com.binariasystems.fmw.vweb.uicomponet.AddressEditorField;

public enum NomenclatureComplement {
	ADMINISTRATION("AD", "Administraci\u00f3n"),
	AGENCY("AG", "Agencia"),
	GROUP("AGP", "Agrupaci\u00f3n"),
	MARKET("ALM", "Almacen"),
	ALTILLO("AL", "Altillo"),
	PO_BOX("APTDO", "Apartado"),
	APARTMENT("AP", "Apartamento"),
	NEIGHBORHOOD("BRR", "Barrio"),
	BLOQCK("BL", "Bloque"),
	WAREHOUSE("BG", "Bodega"),
	TIGHT_ROAD("CN", "Camino"),
	HOUSE("CA", "Casa"),
	CELL("CEL", "Celula"),
	SHOPPING_CENTER("CC", "Centro Comercial"),
	CITADEL("CD", "Ciudadela"),
	SET("CONJ", "Conjunto"),
	CLOSED_SET("CON", "Conjunto Residencial"),
	DOCTOR_OFFICE("CS", "Consultorio"),
	SMALL_TOWN("CORR", "Corregimiento"),
	STATE("DPTO", "Departamento"),
	STORE("DP", "Deposito"),
	BASEMENT_STORE("D", "Deposito Sotano"),
	BUILDING("ED", "Edificio"),
	ENTRANCE("EN", "Entrada"),
	CORNER("ESQ", "Esquina"),
	STAGE("ET", "Etapa"),
	EXTERIOR("EX", "Exterior"),
	RANCH("FCA", "Finca"),
	GARAGE("GJ", "Garaje"),
	BASEMENT_GARAGE("GS", "Garaje Sotano"),
	FARM("HC", "Hacienda"),
	INTERIOR("IN", "Interior"),
	KILOMETER("KM", "Kilometro"),
	LOCAL("LC", "Local"),
	LOCAL_MEZZANINE("LM", "Local Mezzanine"),
	LOTE("LT", "Lote"),
	MANZANA("MZ", "Manzana"),
	MEZZANINE("MN", "Mezzanine"),
	MODULE("MD", "Modulo"),
	TOWN("MCP", "Municipio"),
	OFFICE("OF", "Oficina"),
	PLOT("PA", "Parcela"),
	PARK("PAR", "Parque"),
	PARKING_LOT("PQ", "Parqueadero"),
	PENTHOUSE("PH", "Penthouse"),
	FLOOR("PISO", "Piso"),
	LEVEL("PL", "Planta"),
	GUARD_POST("POR", "Porteria"),
	PROPERTY("PD", "Predio"),
	GRIDGE("PN", "Puente"),
	POST("PT", "Puesto"),
	ROOM("SA", "Sal\u00f3n"),
	COMMUNAL_ROOM("SC", "Sal\u00f3n Comunal"),
	SECTOR("SEC", "Sector"),
	SEMI_BASEMENT("SS", "Semisotano"),
	SITE("SL", "Solar"),
	BASEMENT("ST", "Sotano"),
	SUITE("SU", "Suite"),
	SUPERMANZANA("SM", "Supermanzana"),
	TERMINAL("TER", "Terminal"),
	TERRACE("TZ", "Terraza"),
	TOWER("TO", "Torre"),
	UNIT("UN", "Unidad"),
	RESIDENTIAL_UNIT("UR", "Unidad Residencial"),
	URBANIZATION("URB", "Urbanizaci\u00f3n"),
	LANE("VDA", "Vereda"),
	ZONE("ZN", "Zona"),
	FREE_ZONE("ZF", "Zona Franca"),
	VIA("VIA", "Via");
	
	private String code;
	private String description;
	private NomenclatureComplement(String code, String  description){
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
		.append(NomenclatureComplement.class.getSimpleName().toLowerCase())
		.append(".")
		.append(code).toString();
	}
}
