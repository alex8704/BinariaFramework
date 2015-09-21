package co.com.binariasystems.fmw.vweb.mvp.eventbus;

import co.com.binariasystems.fmw.vweb.mvp.event.UIEvent;


public interface EventBus {
	public void addHandler(Object handler);
	public void removeHandler(Object handler);
	public boolean fireEvent(UIEvent event);
}
