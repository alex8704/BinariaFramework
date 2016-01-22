package co.com.binariasystems.webtestapp.sec;

import java.util.List;

import co.com.binariasystems.fmw.entity.manager.EntityCRUDOperationsManager;
import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.security.FMWSecurityException;
import co.com.binariasystems.fmw.security.authc.SecurityPrincipalConverter;
import co.com.binariasystems.fmw.security.util.SecConstants;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.vweb.util.LocaleMessagesUtil;
import co.com.binariasystems.webtestapp.dto.UsuarioDTO;

public class BinariaSecurityPrincipalConverter implements SecurityPrincipalConverter<String, UsuarioDTO>{
	private MessageBundleManager messageManager;
	private EntityCRUDOperationsManager<UsuarioDTO> operationsManager;
	
	public BinariaSecurityPrincipalConverter() {
		messageManager = MessageBundleManager.forPath(SecConstants.DEFAULT_AUTH_MESSAGES_PATH, SecurityPrincipalConverter.class);
	}
	
	private EntityCRUDOperationsManager<UsuarioDTO> getManager(){
		if(operationsManager == null)
			operationsManager = EntityCRUDOperationsManager.getInstance(UsuarioDTO.class);
		return operationsManager;
	}

	@Override
	public UsuarioDTO toPrincipalModel(String representation){
		List<UsuarioDTO> searchResult = null;
		UsuarioDTO usuarioDTO = new UsuarioDTO();
		usuarioDTO.setAlias(representation);
		try {
			searchResult = getManager().searchWithoutPaging(usuarioDTO);
			if(searchResult.isEmpty())
				throw new FMWSecurityException(LocaleMessagesUtil.getLocalizedMessage(messageManager, "UnknownAccountException"+".localizedMessage"));
		} catch (FMWException | ReflectiveOperationException e) {
			throw new FMWUncheckedException(e.getMessage());
		}
		return searchResult.get(0);
	}

	@Override
	public String toPrincipalRepresentation(UsuarioDTO model){
		return model.getAlias();
	}

}
