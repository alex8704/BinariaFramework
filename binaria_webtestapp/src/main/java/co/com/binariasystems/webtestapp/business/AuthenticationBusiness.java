package co.com.binariasystems.webtestapp.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import co.com.binariasystems.fmw.business.FMWBusiness;
import co.com.binariasystems.webtestapp.dataaccess.UsuariosDAO;
import co.com.binariasystems.webtestapp.entity.Usuario;

public interface AuthenticationBusiness extends FMWBusiness {
	
	public String dato();
	
	
	@Service
	@Transactional(
			propagation=Propagation.SUPPORTS, readOnly=true
			)
	public static class AuthenticationBusinessImpl implements AuthenticationBusiness{
		@Autowired
		private UsuariosDAO dao;
		
		
		@Override
		public String dato() {
			Iterable<Usuario> users = dao.findAll();
			System.out.println("{USUARIOS DE LA BASE DE DATOS}");
			for(Usuario u : users){
				System.out.println("Usuario: "+u.getAlias()+", Contrasenia: "+u.getContrasenia());
			}
			return "{Este es el DATO gilipollete}";
		}
	}
}
