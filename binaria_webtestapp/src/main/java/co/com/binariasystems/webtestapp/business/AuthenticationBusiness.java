package co.com.binariasystems.webtestapp.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import co.com.binariasystems.fmw.business.FMWBusiness;
import co.com.binariasystems.fmw.util.mail.SimpleMailMessage;
import co.com.binariasystems.fmw.util.messagebundle.PropertiesManager;
import co.com.binariasystems.fmw.util.velocity.VelocityMailSender;
import co.com.binariasystems.webtestapp.dataaccess.UsuariosDAO;
import co.com.binariasystems.webtestapp.entity.Usuario;

public interface AuthenticationBusiness extends FMWBusiness {
	
	public String dato();
	public void sendAuthenticationMail();
	
	
	@Service
	@Transactional(
			propagation=Propagation.SUPPORTS, readOnly=true
			)
	public static class AuthenticationBusinessImpl implements AuthenticationBusiness{
		@Autowired
		private UsuariosDAO dao;
		@Autowired
		private VelocityMailSender mailSender;
		private PropertiesManager mailProperties;
		
		public AuthenticationBusinessImpl() {
			mailProperties = PropertiesManager.forPath("/javamail.properties", AuthenticationBusiness.class);
		}
		
		@Override
		public String dato() {
			Iterable<Usuario> users = dao.findAll();
			System.out.println("{USUARIOS DE LA BASE DE DATOS}");
			for(Usuario u : users){
				System.out.println("Usuario: "+u.getAlias()+", Contrasenia: "+u.getContrasenia());
			}
			return "{Este es el DATO gilipollete}";
		}
		
		@Async
		@Override
		public void sendAuthenticationMail() {
//			SimpleMailMessage msg = new SimpleMailMessage();
//			msg.setFrom(mailProperties.getString("mail.smtp.user"));
//			msg.setTo("alexander_8704@hotmail.com");
//			msg.setSubject("[Mensaje Simple]");
//			mailSender.send(msg, "/simple-tmpl.vm", null);
		}
	}
}
