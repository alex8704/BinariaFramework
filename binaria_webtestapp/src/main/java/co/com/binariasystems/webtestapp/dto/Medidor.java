package co.com.binariasystems.webtestapp.dto;

import java.sql.Timestamp;

import co.com.binariasystems.fmw.dto.AbstractDTO;
import co.com.binariasystems.fmw.entity.Column;
import co.com.binariasystems.fmw.entity.Entity;
import co.com.binariasystems.fmw.entity.ForeignKey;
import co.com.binariasystems.fmw.entity.Key;
import co.com.binariasystems.fmw.entity.Relation;
import co.com.binariasystems.fmw.entity.SearcherConfig;

@Entity(table="medidores")
@SearcherConfig(descriptionFields={"id", "serial"})
public class Medidor extends AbstractDTO{
	@Key(column="id_medidor")
	private Long id;
	private String serial;
	@Relation(column="id_gateway")
	private Gateway gateway;
	//@Relation(column="id_suscriptor")
	@Column(name="id_suscriptor")
	@ForeignKey(entityClazz=Suscriptor.class)
	private Long suscriptor;
	@Column(name="fecha_instalacion")
	private Timestamp fechaInstalacion;
	@Column(name="lectura_inicial")
	private Double lecturaInicial;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public Gateway getGateway() {
		return gateway;
	}
	public void setGateway(Gateway gateway) {
		this.gateway = gateway;
	}
	public Long getSuscriptor() {
		return suscriptor;
	}
	public void setSuscriptor(Long suscriptor) {
		this.suscriptor = suscriptor;
	}
	public Timestamp getFechaInstalacion() {
		return fechaInstalacion;
	}
	public void setFechaInstalacion(Timestamp fechaInstalacion) {
		this.fechaInstalacion = fechaInstalacion;
	}
	public Double getLecturaInicial() {
		return lecturaInicial;
	}
	public void setLecturaInicial(Double lecturaInicial) {
		this.lecturaInicial = lecturaInicial;
	}
	
}
