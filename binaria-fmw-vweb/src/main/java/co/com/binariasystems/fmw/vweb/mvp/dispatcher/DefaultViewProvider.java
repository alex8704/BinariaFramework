package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.util.messagebundle.PropertiesManager;
import co.com.binariasystems.fmw.vweb.mvp.annotation.AuthenticationForm;
import co.com.binariasystems.fmw.vweb.mvp.annotation.DashBoard;
import co.com.binariasystems.fmw.vweb.mvp.annotation.Forbidden;
import co.com.binariasystems.fmw.vweb.mvp.annotation.Init;
import co.com.binariasystems.fmw.vweb.mvp.annotation.Namespace;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ResourceNotFound;
import co.com.binariasystems.fmw.vweb.mvp.annotation.View;
import co.com.binariasystems.fmw.vweb.mvp.annotation.View.NullController;
import co.com.binariasystems.fmw.vweb.mvp.annotation.View.Root;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewBuild;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewController;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewController.OnLoad;
import co.com.binariasystems.fmw.vweb.mvp.annotation.ViewController.OnUnLoad;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ControllerInfo;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.NamespaceInfo;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.RequestData;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewAndController;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewInfo;
import co.com.binariasystems.fmw.vweb.mvp.views.DefaultForbiddenView;
import co.com.binariasystems.fmw.vweb.mvp.views.DefaultResourceNotFoundView;
import co.com.binariasystems.fmw.vweb.resources.resources;

import com.vaadin.ui.Component;

public class DefaultViewProvider implements ViewProvider, ServletContextAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultViewProvider.class);
	private Map<String, String> packagesNamespaces = new HashMap<String, String>();
	private Map<String, NamespaceInfo> namespacesContext = new HashMap<String, NamespaceInfo>();
	private Map<String, ViewInfo> viewsContext = new HashMap<String, ViewInfo>();
	private Map<String, ControllerInfo> controllersContext = new HashMap<String, ControllerInfo>();
	private ViewInstanceCreator defaultViewInstanceCreator;
	private List<ViewInstanceCreator> viewInstanceCreators = new LinkedList<ViewInstanceCreator>();
	private ViewInfo forbiddenView;
	private ViewInfo resourceNotFoundView;
	private ViewInfo authenticationFormView;
	private ViewInfo dashboardView;
	private List<String> viewsPackages;
	private Reflections reflections;
	private String scanningExcludedPackages;
	private boolean showConfigurationDebugInfo;
	private ServletContext servletContext;
	
	public void configure()  throws ViewConfigurationException{
		FilterBuilder packagesFilter = new FilterBuilder();
		if(viewsPackages != null && viewsPackages.size() > 0){
			for(String packageName : viewsPackages)
				packagesFilter.includePackage(packageName);
			
		}else
			packagesFilter.includePackage("");
		
		String[] excludedPackages = getScanningExcludedArray();
		if(excludedPackages != null){
			for(String excludedPack : excludedPackages)
				packagesFilter.excludePackage(excludedPack);
		}
		
		ConfigurationBuilder configBuilder = new ConfigurationBuilder();
		configBuilder.filterInputsBy(packagesFilter);
		if(servletContext != null)
			configBuilder.addUrls(ClasspathHelper.forWebInfClasses(servletContext))
			.addUrls(ClasspathHelper.forWebInfLib(servletContext));
		configBuilder.setScanners(new SubTypesScanner(), new TypeAnnotationsScanner());
		
		reflections = new Reflections(configBuilder);
		
		
		for(ViewInstanceCreator viewInstanCreator : viewInstanceCreators){
			defaultViewInstanceCreator = (viewInstanCreator instanceof DefaultViewInstanceCreator) ? viewInstanCreator : defaultViewInstanceCreator;
			viewInstanCreator.init(reflections);
		}
		if(defaultViewInstanceCreator == null){
			defaultViewInstanceCreator = new DefaultViewInstanceCreator();
			defaultViewInstanceCreator.init(reflections);
		}
		//Para Garantizar que el defaultViewInstanceCreator, no haga parte del
		//Grupo de InstanceCretors personalizados
		viewInstanceCreators.remove(defaultViewInstanceCreator);
		
		configureControllersContext();
		configureNamespacesContext();
		configureViewsContext();
		ensureDefaultPublicViews();
		
		if(showConfigurationDebugInfo)
			printConfigurationDebugInfo();
	}
	
	private void configureControllersContext() throws ViewConfigurationException{
		Set<Class<?>> annotatedControllers = reflections.getTypesAnnotatedWith(ViewController.class);
		ControllerInfo controllerInfo = null;
		for(Class<?> annotated : annotatedControllers){
			controllerInfo = new ControllerInfo();
			controllerInfo.setControllerClass(annotated);
			
			for(Method method : annotated.getMethods()){
				if(method.isAnnotationPresent(OnLoad.class)){
					if(method.getParameterTypes() != null && method.getParameterTypes().length > 1)
						throw new ViewConfigurationException("Method annotated with @"+OnLoad.class.getSimpleName()+" on controller class "+annotated.getSimpleName()+" must have only 1 parameter of type "+Map.class.getName());
					controllerInfo.setBeforeLoadMethod(method.getName());
				}
				if(method.isAnnotationPresent(OnUnLoad.class)){
					if(method.getParameterTypes() != null && method.getParameterTypes().length > 0)
						throw new ViewConfigurationException("Method annotated with @"+OnUnLoad.class.getSimpleName()+" on controller class "+annotated.getSimpleName()+" must have no parameters");
					controllerInfo.setBeforeUnloadMethod(method.getName());
				}
				if(method.isAnnotationPresent(Init.class)){
					if(method.getParameterTypes() != null && method.getParameterTypes().length > 0)
						throw new ViewConfigurationException("Method annotated with @"+Init.class.getSimpleName()+" on controller class "+annotated.getSimpleName()+" must have no parameters");
					controllerInfo.setInitMethod(method.getName());
				}
			}
			
			controllersContext.put(annotated.getName(), controllerInfo);
		}
	}
	
	private void configureNamespacesContext() throws ViewConfigurationException{
		Set<Class<?>> annotatedPackages = reflections.getTypesAnnotatedWith(Namespace.class);
		for(Class<?> annotated : annotatedPackages){
			Namespace namespace = annotated.getAnnotation(Namespace.class);
			NamespaceInfo namespaceInfo = new NamespaceInfo();
			namespaceInfo.setMessages(namespace.messages());
			namespaceInfo.setPath(namespace.path());
			packagesNamespaces.put(annotated.getPackage().getName(), namespaceInfo.getPath());
			namespacesContext.put(namespaceInfo.getPath(), namespaceInfo);
		}
	}
	
	private void configureViewsContext() throws ViewConfigurationException{
		Set<Class<?>> annotatedPackages = reflections.getTypesAnnotatedWith(View.class);
		NamespaceInfo namespaceInfo = null;
		String realPackage = null;
		String urlPrefix = null;
		String shortUrl = null;
		String largeUrl = null;
		for(Class<?> annotated : annotatedPackages){
			View view = annotated.getAnnotation(View.class);
			ViewInfo viewInfo = new ViewInfo();
			viewInfo.setViewClass(annotated);
			viewInfo.setControllerInfo(view.controller() == NullController.class ? null : controllersContext.get(view.controller().getName()));
			viewInfo.setPublicView(view.isPublic());
			viewInfo.setRootView(annotated.isAnnotationPresent(Root.class));
			viewInfo.setViewStringsByConventions(view.viewStringsByConventions());
			
			if(viewInfo.isRootView()){
				viewInfo.setContentSetterMethod(annotated.getAnnotation(Root.class).contentSetterMethod());
				if(StringUtils.isEmpty(viewInfo.getContentSetterMethod()))
					throw new ViewConfigurationException("Root view "+annotated.getName()+" must declare a contentSetterMethod on @"+Root.class.getName()+" annotation");
			}
			
			
			shortUrl = view.url().startsWith("/") ? view.url() : "/" + view.url();
			
			realPackage = resolveCompleteViewPackage(viewInfo);
			urlPrefix = StringUtils.defaultIfEmpty(packagesNamespaces.get(realPackage), "");
			if(shortUrl.lastIndexOf("/") > 0){
				urlPrefix += shortUrl.substring(0, shortUrl.lastIndexOf("/"));
			}
			namespaceInfo = namespacesContext.get(urlPrefix);
			if(namespaceInfo == null){
				namespaceInfo = new NamespaceInfo();
				namespaceInfo.setPath(StringUtils.defaultString(urlPrefix));
			}
			
			largeUrl = namespaceInfo.getPath() + shortUrl.substring(shortUrl.lastIndexOf("/"));
			
			viewInfo.setUrl(largeUrl);
			viewInfo.setMessages(StringUtils.defaultIfEmpty(view.messages(), namespaceInfo.getMessages()));
			
			if(StringUtils.isEmpty(largeUrl))
				throw new ViewConfigurationException("Empty url has not valid for view "+viewInfo.getViewClass().getName());
			if(largeUrl.equals(SPECIAL_VIEWS_URL) && !isSpecialApplicationView(annotated))
				throw new ViewConfigurationException("Url '"+SPECIAL_VIEWS_URL+"', is a special reserved url and cannot be used for application views");
			
			if(!isSpecialApplicationView(annotated)){
				if(viewsContext.get(largeUrl) != null)
					throw new ViewConfigurationException("Already exist a View with url: '"+viewInfo.getUrl()+"' on namespace: '"+namespaceInfo.getPath()+"'");
				applyMandatoryRestrictions(viewInfo);
				namespacesContext.put(namespaceInfo.getPath(), namespaceInfo);
				viewsContext.put(largeUrl, viewInfo);
				namespaceInfo.getViews().add(viewInfo);
			}
			else{
				viewInfo.setPublicView(true);
				if(annotated.isAnnotationPresent(AuthenticationForm.class))
					authenticationFormView = viewInfo;
				if(annotated.isAnnotationPresent(DashBoard.class))
					dashboardView = viewInfo;
				if(annotated.isAnnotationPresent(ResourceNotFound.class) && !DefaultResourceNotFoundView.class.equals(annotated))
					resourceNotFoundView = viewInfo;
				if(annotated.isAnnotationPresent(Forbidden.class) && !DefaultForbiddenView.class.equals(annotated))
					forbiddenView = viewInfo;
			}
		}
	}
	
	public boolean isSpecialApplicationView(Class<?> viewClass){
		return (viewClass.isAnnotationPresent(AuthenticationForm.class) || 
				viewClass.isAnnotationPresent(Forbidden.class) || 
				viewClass.isAnnotationPresent(ResourceNotFound.class) ||
				viewClass.isAnnotationPresent(DashBoard.class));
	}
	
	/*
	 * Se puede presentar que una Clase anotada con @View, no se encuentre en un paquete
	 * anotado con @Namespace, por tanto se empieza a buscar en los paquetes padres
	 * a ver cual es el mas cercano con un @Namespace definido y en ese namespace
	 * se ubica el View
	 */
	private String resolveCompleteViewPackage(ViewInfo viewInfo){
		String realPackageName = null;
		String parentPackage = viewInfo.getViewClass().getPackage().getName();
		int lastDot = parentPackage.length();
		do{
			parentPackage = parentPackage.substring(0, lastDot);
			if(packagesNamespaces.get(parentPackage) != null){
				realPackageName = parentPackage;
			}
		}while((lastDot = parentPackage.lastIndexOf(".")) != -1 && realPackageName == null);
		
		return realPackageName;
	}
	
	private void ensureDefaultPublicViews() throws ViewConfigurationException{
		if(forbiddenView == null){
			forbiddenView = new ViewInfo();
			forbiddenView.setPublicView(true);
			forbiddenView.setMessages(DefaultForbiddenView.class.getAnnotation(View.class).messages());
			forbiddenView.setRootView(DefaultForbiddenView.class.isAnnotationPresent(Root.class));
			forbiddenView.setUrl(SPECIAL_VIEWS_URL+"?"+FORBIDDEN_VIEW_PARAM_IDENTIFIER);
			forbiddenView.setViewClass(DefaultForbiddenView.class);
			//forbiddenView.setViewBuildMethod(DefaultForbiddenView.VIEW_BUILD_METHOD);
			
		}
		
		if(resourceNotFoundView == null){
			resourceNotFoundView = new ViewInfo();
			resourceNotFoundView.setPublicView(true);
			resourceNotFoundView.setMessages(DefaultResourceNotFoundView.class.getAnnotation(View.class).messages());
			resourceNotFoundView.setRootView(DefaultResourceNotFoundView.class.isAnnotationPresent(Root.class));
			resourceNotFoundView.setUrl(SPECIAL_VIEWS_URL+"?"+RESNOTFOUND_VIEW_PARAM_IDENTIFIER);
			resourceNotFoundView.setViewClass(DefaultResourceNotFoundView.class);
			//resourceNotFoundView.setViewBuildMethod(DefaultResourceNotFoundView.VIEW_BUILD_METHOD);
		}
		
		if(dashboardView != null){
			dashboardView.setUrl(SPECIAL_VIEWS_URL+"?"+DASHBOARD_VIEW_PARAM_IDENTIFIER);
			applyMandatoryRestrictions(dashboardView);
			viewsContext.put(dashboardView.getUrl(), dashboardView);
		}
		if(authenticationFormView != null){
			authenticationFormView.setUrl(SPECIAL_VIEWS_URL+"?"+AUTHENTICATION_VIEW_PARAM_IDENTIFIER);
			authenticationFormView.setContentSetterMethod(null);
			applyMandatoryRestrictions(authenticationFormView);
			viewsContext.put(authenticationFormView.getUrl(), authenticationFormView);
			viewsContext.put(SPECIAL_VIEWS_URL, authenticationFormView);
		}
			
		applyMandatoryRestrictions(forbiddenView);
		applyMandatoryRestrictions(resourceNotFoundView);
		viewsContext.put(forbiddenView.getUrl(), forbiddenView);
		viewsContext.put(resourceNotFoundView.getUrl(), resourceNotFoundView);
	}
	
	private void applyMandatoryRestrictions(ViewInfo viewInfo) throws ViewConfigurationException{
		Method viewBuildMethod = null;
		Method initMethod = null;
		for(Method method : viewInfo.getViewClass().getMethods()){
			if(method.isAnnotationPresent(ViewBuild.class))
				viewBuildMethod = method;
			if(method.isAnnotationPresent(Init.class))
				initMethod = method;
		}
		if(viewBuildMethod != null){
			if(viewBuildMethod.getParameterTypes() != null && viewBuildMethod.getParameterTypes().length > 0)
				throw new ViewConfigurationException("Method annotated with @"+ViewBuild.class.getSimpleName()+" on '"+viewInfo.getViewClass().getSimpleName()+"'"
						+ "must not have parameters");
			if(!Component.class.isAssignableFrom(viewBuildMethod.getReturnType()))
				throw new ViewConfigurationException("Method annotated with @"+ViewBuild.class.getSimpleName()+" on '"+viewInfo.getViewClass().getSimpleName()+"'"
						+ "must return a "+Component.class.getName()+" compatible object");
			viewInfo.setViewBuildMethod(viewBuildMethod.getName());
		}
		
		if(initMethod != null){
			if(initMethod.getParameterTypes() != null && initMethod.getParameterTypes().length > 0)
				throw new ViewConfigurationException("Method annotated with @"+Init.class.getSimpleName()+" on view class "+viewInfo.getViewClass().getSimpleName()+" must have no parameters");
			viewInfo.setInitMethod(initMethod.getName());
		}
		
		if(viewInfo.isRootView() && !viewInfo.equals(authenticationFormView)){
//			String methodBuild = new StringBuilder()
//			.append("<ACCESS MODIFIER> ")
//			.append("<RETURN_TYPE> ")
//			.append("<METHOD_NAME>")
//					.append("(").append(Component.class.getName())
//					.append("), example: protected void myMethod(Component component)").toString();
			Method setContentmethod = MethodUtils.getAccessibleMethod(viewInfo.getViewClass(), viewInfo.getContentSetterMethod(), Component.class);
			if(setContentmethod == null)
				viewInfo.setContentSetterMethod(null);
//				throw new ViewConfigurationException("Method "+viewInfo.getContentSetterMethod()+" of view class "+viewInfo.getViewClass().getName()+" must the next signature "+methodBuild);
		}
	}
	

	@Override
	public ViewAndController getView(RequestData request) throws FMWException{
		ViewInstanceCreator viewCreator = null;
		ViewAndController resp = null;
		for(ViewInstanceCreator candidate : viewInstanceCreators){
			if(candidate.matches(request.getUrl())){
				viewCreator = candidate;
				break;
			}
		}
		if(viewCreator != null){
			resp = viewCreator.createAndCongigureView(viewsContext.get(request.getUrl()), request);
			if(resp != null)
				return resp;
		}
		
		ViewInfo viewInfo = null;
		String verifierUrl = StringUtils.defaultIfEmpty(request.getUrl(), SPECIAL_VIEWS_URL);
		
		verifierUrl = !(verifierUrl.equals(SPECIAL_VIEWS_URL)) ? request.getUrl() :  request.getUrl() + (StringUtils.isEmpty(request.getPathInfo()) ? "" : "?" + request.getPathInfo());
		
		viewInfo = viewsContext.get(verifierUrl);
		if(viewInfo == null)
			viewInfo = resourceNotFoundView;
		
		resp = defaultViewInstanceCreator.createAndCongigureView(viewInfo, request);
		
		return resp;
	}
	
	
	private String[] getScanningExcludedArray(){
		PropertiesManager messages = PropertiesManager.forPath(resources.getPropertyFilePath("annotation_scanning_excluded.xml"), resources.class);
		String defaultExcludes = messages.getString("excluded");
		if(StringUtils.isNotEmpty(scanningExcludedPackages))
			defaultExcludes += ","+scanningExcludedPackages;
		return StringUtils.split(defaultExcludes, ",");
	}
	

	@Override
	public boolean isPublicView(String url) {
		ViewInfo viewInfo = getViewInfoByURL(url, null);
		return viewInfo.isPublicView();
	}
	
	@Override
	public String getDashboardViewUrl() {
		return dashboardView != null ? dashboardView.getUrl() : SPECIAL_VIEWS_URL;
	}

	@Override
	public String getForbiddenViewUrl() {
		return forbiddenView.getUrl();
	}
	
	@Override
	public ViewInfo getViewInfo(RequestData request) {
		return getViewInfoByURL(request.getUrl(), request.getPathInfo());
	}
	
	private ViewInfo getViewInfoByURL(String viewURL, String pathInfo) {
		ViewInstanceCreator viewCreator = null;
		for(ViewInstanceCreator candidate : viewInstanceCreators){
			if(candidate.matches(viewURL)){
				viewCreator = candidate;
				break;
			}
		}
		
		if(viewCreator != null)
			return new ViewInfo(viewURL); 
		
		ViewInfo viewInfo = null;
		String verifierUrl = StringUtils.defaultIfEmpty(viewURL, SPECIAL_VIEWS_URL);
		
		verifierUrl = !(verifierUrl.equals(SPECIAL_VIEWS_URL)) ? viewURL :  viewURL + (StringUtils.isEmpty(pathInfo) ? "" : "?" + pathInfo);
		
		viewInfo = viewsContext.get(verifierUrl);
		if(viewInfo == null)
			viewInfo = resourceNotFoundView;
		
		
		return viewInfo;
	}
	
	@Override
	public String getViewUrlByClass(Class<?> viewClass) {
		for(ViewInfo viewInfo : viewsContext.values()){
			if(viewInfo.getViewClass().equals(viewClass))
				return viewInfo.getUrl();
		}
		return null;
	}
	
	private void printConfigurationDebugInfo(){
		LOGGER.info("Discovered and Configured Views on Application Classpath");
		for(String viewUrl : viewsContext.keySet())
			LOGGER.info("{} : {}", viewUrl, viewsContext.get(viewUrl).getViewClass());
	}

	public List<ViewInstanceCreator> getViewInstanceCreators() {
		return viewInstanceCreators;
	}

	public void setViewInstanceCreators(List<ViewInstanceCreator> viewInstanceCreators) {
		this.viewInstanceCreators = viewInstanceCreators;
	}

	public List<String> getViewsPackages() {
		return viewsPackages;
	}

	public void setViewsPackages(List<String> viewsPackages) {
		this.viewsPackages = viewsPackages;
	}

	public boolean isShowConfigurationDebugInfo() {
		return showConfigurationDebugInfo;
	}

	public void setShowConfigurationDebugInfo(boolean showConfigurationDebugInfo) {
		this.showConfigurationDebugInfo = showConfigurationDebugInfo;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
}
