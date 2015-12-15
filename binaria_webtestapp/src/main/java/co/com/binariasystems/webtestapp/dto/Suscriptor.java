package co.com.binariasystems.webtestapp.dto;

import java.util.Date;

import co.com.binariasystems.fmw.dto.AbstractDTO;
import co.com.binariasystems.fmw.dto.Listable;
import co.com.binariasystems.fmw.entity.annot.CRUDViewConfig;
import co.com.binariasystems.fmw.entity.annot.Column;
import co.com.binariasystems.fmw.entity.annot.Entity;
import co.com.binariasystems.fmw.entity.annot.FieldValue;
import co.com.binariasystems.fmw.entity.annot.Key;
import co.com.binariasystems.fmw.entity.annot.SearcherConfig;
import co.com.binariasystems.fmw.entity.annot.ViewFieldConfig;
@Entity(table="suscriptores")
@CRUDViewConfig(searcherConfig=@SearcherConfig(descriptionFields={"id", "identificacion", "nombre"}, searchField="identificacion"))
public class Suscriptor extends AbstractDTO{
	@Key(column="id_suscriptor")
	private Long id;
	private String nombre;
	private String identificacion;
	@Column(name="tipo_identificacion")
	private TipoId tipoId;
	@Column(name="fecha_nacimiento")
	private Date fechaNac;
	@ViewFieldConfig(fixedValues={@FieldValue(pk="S", description= "Si"), @FieldValue(pk="N", description= "No")})
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
