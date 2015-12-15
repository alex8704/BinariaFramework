package co.com.binariasystems.webtestapp.dto;

import java.io.Serializable;

import co.com.binariasystems.fmw.entity.annot.CRUDViewConfig;
import co.com.binariasystems.fmw.entity.annot.Column;
import co.com.binariasystems.fmw.entity.annot.Entity;
import co.com.binariasystems.fmw.entity.annot.Key;
import co.com.binariasystems.fmw.entity.annot.SearcherConfig;
import co.com.binariasystems.fmw.entity.annot.ViewFieldConfig;

@Entity(table="usuarios")
@SearcherConfig()
@CRUDViewConfig(searcherConfig=@SearcherConfig(descriptionFields={"alias"}, searchField="alias"))
public class UsuarioDTO implements Serializable{
	@Key(column="id_usuario")
	private Long id;
	@ViewFieldConfig(ommitUpperTransform=true)
	@Column(name="nombre_usuario")
	private String alias;
	@ViewFieldConfig(ommitUpperTransform=true)
	@Column(name="password")
	private String contrasenia;
	@Column(name="password_salt")
	private String saltContrasenia;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getContrasenia() {
		return contrasenia;
	}
	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
	}
	public String getSaltContrasenia() {
		return saltContrasenia;
	}
	public void setSaltContrasenia(String saltContrasenia) {
		this.saltContrasenia = saltContrasenia;
	}
	
}
