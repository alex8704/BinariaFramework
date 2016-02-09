package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import co.com.binariasystems.fmw.entity.annot.Entity;
import co.com.binariasystems.fmw.vweb.constants.VWebCommonConstants;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.RequestData;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewAndController;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewInfo;
import co.com.binariasystems.fmw.vweb.uicomponet.EntityCRUDPanel;

import com.vaadin.ui.Component;

public class EntityCRUDViewInstanceCreator implements ViewInstanceCreator {
	private Map<String, Class<?>> entityClassesContext = new HashMap<String, Class<?>>();
	private Pattern urlPattern = Pattern.compile(getUrlPattern());
	
	
	@Override
	public void init(Reflections reflections) {
		Set<Class<?>> entityClasses = reflections.getTypesAnnotatedWith(Entity.class);
		String entityClassKey = null;
		for(Class<?> entityClass : entityClasses){
			entityClassKey = entityClass.getSimpleName();
			if(entityClassKey.toLowerCase().endsWith("dto"))
				entityClassKey = entityClassKey.substring(0, entityClassKey.length() - "dto".length());
			entityClassesContext.put(entityClassKey, entityClass);
		}
			
	}

	@Override
	public ViewAndController createAndCongigureView(ViewInfo viewInfo, RequestData request) throws ViewInstantiationException {
		Matcher matcher = urlPattern.matcher(request.getUrl());
		matcher.find();
		String entityClassKey = matcher.group(5);
		if(entityClassKey.toLowerCase().endsWith("dto"))
			entityClassKey = entityClassKey.substring(0, entityClassKey.length() - "dto".length());
		if(StringUtils.isEmpty(entityClassKey)) return null;
		
		Class<?> entityClass = entityClassesContext.get(entityClassKey);
		if(entityClass == null) return null;
		
		Component view = new EntityCRUDPanel(entityClass);
		return new ViewAndController(view, view);
	}

	@Override
	public String getUrlPattern() {
		return VWebCommonConstants.ENTITYCRUD_VIEWCREATOR_URLPATTERN;
	}

	@Override
	public boolean matches(String url) {
		if(StringUtils.isEmpty(url))
			return false;
		Matcher matcher = urlPattern.matcher(url);
		return matcher.matches();
	}

}
