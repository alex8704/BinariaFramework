package co.com.binariasystems.webtestapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="usuarios")
public class Usuario {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_usuario")
	private Long id;
	@Column(name="nombre_usuario")
	private String alias;
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
