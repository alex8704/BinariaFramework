package co.com.binariasystems.fmw.vweb.mvp.dispatcher;

import org.reflections.Reflections;

import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.RequestData;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewAndController;
import co.com.binariasystems.fmw.vweb.mvp.dispatcher.data.ViewInfo;

public interface ViewInstanceCreator {
	public void init(Reflections reflections);
	public ViewAndController createAndCongigureView(ViewInfo viewInfo, RequestData request) throws ViewInstantiationException;
	public String getUrlPattern();
	
	public boolean matches(String url);
}
