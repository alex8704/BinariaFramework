package co.com.binariasystems.webtestapp.dto;

import java.util.Date;

import co.com.binariasystems.fmw.dto.AbstractDTO;
import co.com.binariasystems.fmw.entity.annot.CRUDViewConfig;
import co.com.binariasystems.fmw.entity.annot.Column;
import co.com.binariasystems.fmw.entity.annot.Entity;
import co.com.binariasystems.fmw.entity.annot.Key;
import co.com.binariasystems.fmw.entity.annot.Relation;
import co.com.binariasystems.fmw.entity.annot.SearcherConfig;

@Entity(table="gateways")
@CRUDViewConfig(
		searcherConfig=@SearcherConfig(
				descriptionFields = {"ip","descripcion"},
				gridColumnFields = {"ip","descripcion", "creationUser", "modificationDate"}
		),
		isAuditable=true
)
public class Gateway extends AbstractDTO {
	@Key(column="id_gateway")
	private long id;
	@Column(name="ip_gateway")
	private String ip;
	private String descripcion;
	@Column(name="feha_comunicacion")
	private Date fechaComunicacion;
	
	//Auditory Data with defaultNames
	@Relation(column="id_creation_user")
	private UsuarioDTO creationUser;
	@Column(name="modification_date")
	private Date modificationDate;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Date getFechaComunicacion() {
		return fechaComunicacion;
	}
	public void setFechaComunicacion(Date fechaComunicacion) {
		this.fechaComunicacion = fechaComunicacion;
	}
	public UsuarioDTO getCreationUser() {
		return creationUser;
	}
	public void setCreationUser(UsuarioDTO creationUser) {
		this.creationUser = creationUser;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}
	
	
}
