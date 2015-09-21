package co.com.binariasystems.fmw.vweb.mvp.event;

import co.com.binariasystems.fmw.event.FMWEvent;

public interface UIEvent extends FMWEvent {
	public UIEvent set(String key, Object data);
    public <T extends Object> T get(String key, Class<T> clazz);
}
