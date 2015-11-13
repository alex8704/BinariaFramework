package co.com.binariasystems.webtestapp.sec;

import javax.servlet.http.HttpSession;

import co.com.binariasystems.fmw.security.auditory.ShiroBasedAuditoryDataProvider;
import co.com.binariasystems.webtestapp.dto.UsuarioDTO;

public class BinariaAuditoryDataProvider extends ShiroBasedAuditoryDataProvider<UsuarioDTO> {

	@Override
	public UsuarioDTO getCurrenAuditoryUserByHttpSession(HttpSession httpSession) {
		return null;
	}

}
