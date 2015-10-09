package co.com.binariasystems.fmw.vweb.mvp.event;

import co.com.binariasystems.fmw.event.FMWEvent;

public interface UIEvent<ID_TYPE> extends FMWEvent {
	public ID_TYPE getId();
	public UIEvent<ID_TYPE> set(String key, Object data);
    public <T extends Object> T get(String key, Class<T> clazz);
}