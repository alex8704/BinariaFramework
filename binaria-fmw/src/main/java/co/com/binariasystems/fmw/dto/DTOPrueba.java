package co.com.binariasystems.fmw.dto;

public class DTOPrueba {
	private String nombre;
	private long identificador;
	private DTOAnidado anidado;
	
	public DTOPrueba() {
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public long getIdentificador() {
		return identificador;
	}

	public void setIdentificador(long identificador) {
		this.identificador = identificador;
	}

	public DTOAnidado getAnidado() {
		return anidado;
	}

	public void setAnidado(DTOAnidado anidado) {
		this.anidado = anidado;
	}
	
	
}
