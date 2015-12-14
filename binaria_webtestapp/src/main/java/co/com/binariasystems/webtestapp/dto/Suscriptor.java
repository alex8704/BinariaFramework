package co.com.binariasystems.webtestapp.dto;

import java.util.Date;

import co.com.binariasystems.fmw.dto.AbstractDTO;
import co.com.binariasystems.fmw.dto.Listable;
import co.com.binariasystems.fmw.entity.Column;
import co.com.binariasystems.fmw.entity.Entity;
import co.com.binariasystems.fmw.entity.FieldValue;
import co.com.binariasystems.fmw.entity.FieldValues;
import co.com.binariasystems.fmw.entity.Key;
import co.com.binariasystems.fmw.entity.SearchField;
import co.com.binariasystems.fmw.entity.SearcherConfig;
@Entity(table="suscriptores")
@SearcherConfig(descriptionFields={"id", "identificacion", "nombre"})
public class Suscriptor extends AbstractDTO{
	@Key(column="id_suscriptor")
	private Long id;
	private String nombre;
	@SearchField
	private String identificacion;
	@Column(name="tipo_identificacion")
	private TipoId tipoId;
	@Column(name="fecha_nacimiento")
	private Date fechaNac;
	@FieldValues(value={@FieldValue(pk="S", description= "Si"), @FieldValue(pk="N", description= "No")})
	private Listable activo;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getIdentificacion() {
		return identificacion;
	}
	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}
	public TipoId getTipoId() {
		return tipoId;
	}
	public void setTipoId(TipoId tipoId) {
		this.tipoId = tipoId;
	}
	public Date getFechaNac() {
		return fechaNac;
	}
	public void setFechaNac(Date fechaNac) {
		this.fechaNac = fechaNac;
	}
	public Listable getActivo() {
		return activo;
	}
	public void setActivo(Listable activo) {
		this.activo = activo;
	}
	
	
}
