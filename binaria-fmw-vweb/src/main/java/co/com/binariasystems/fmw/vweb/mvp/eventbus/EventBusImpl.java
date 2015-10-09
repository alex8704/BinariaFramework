package co.com.binariasystems.fmw.vweb.mvp.eventbus;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.binariasystems.fmw.vweb.mvp.annotation.UIEventHandler;
import co.com.binariasystems.fmw.vweb.mvp.event.UIEvent;

class EventMethodCache implements Serializable{
	
	private static final long serialVersionUID = -3835595439788993624L;
	
	private final Map<Class<?>, Set<Method>> methodMap;
	
	public EventMethodCache(){
		methodMap = new ConcurrentHashMap<Class<?>, Set<Method>>();
	}
	
	public void addMethodToCache(Class<?> c, Method m){
		Set<Method> methods = methodMap.get(c);
		
		if (methods == null){
			methods = new LinkedHashSet<Method>();
			methodMap.put(c, methods);
		}
		
		methods.add(m);
	}
	
	
	public void clear(){
		methodMap.clear();
	}
	
	
	public void removeCachedOf(Class<?> c){
		methodMap.remove(c);
	}
	
	
	public Set<Method> getMethods(Class<?> c)
	{
		return methodMap.get(c);
	}
	
	
	public boolean isClassCached(Class<?> c){
		return methodMap.containsKey(c);
	}
	
}

class EventDispatcher implements Serializable{
	
	private static final long serialVersionUID = -7359501691640084178L;
	
	private final Object target;
	private final Method method;
	
	
	public EventDispatcher(Object target, Method method){
		this.target = target;
		this.method = method;
		this.method.setAccessible(true);
	}
	
	public Object getTarget(){
		return target;
	}
	
	public void dispatchEvent(Object event){
	    
		try {
	      method.invoke(target, new Object[] { event });
	    } catch (IllegalArgumentException e) {
	      throw new Error("Method rejected target/argument: " + event, e);
	    } catch (IllegalAccessException e) {
	      throw new Error("Method became inaccessible: " + event, e);
	    } catch (InvocationTargetException e) {
	      if (e.getCause() instanceof Error) {
	        throw (Error) e.getCause();
	      }
	      else
	    	  throw new Error (e);
	    }
	}
	
	@Override 
	public int hashCode() {
	    final int PRIME = 31;
	    return (PRIME + method.hashCode()) * PRIME
	        + System.identityHashCode(target);
	  }

	  @Override 
	  public boolean equals(Object o) {
	    if (o instanceof EventDispatcher) {
	    	EventDispatcher other = (EventDispatcher) o;
	    	return target == other.target && method.equals(other.method);
	    }
	    
	    return false;
	  }
}


public class EventBusImpl implements EventBus, Serializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventBusImpl.class);
	private static final long serialVersionUID = 5500479291713928578L;
	
	private static final EventMethodCache eventMethodChache = new EventMethodCache();
	private final Map<Class<? extends UIEvent>, Set<EventDispatcher>> handlerMap;
	
	private static boolean caching = true;
	
	
	public EventBusImpl(){
		 handlerMap = new ConcurrentHashMap<Class<? extends UIEvent>, Set<EventDispatcher>>();
	}
	
	/**
	 * Enable or disable caching 
	 * @param caching
	 */
	public void setUseCache(boolean caching)
	{
		EventBusImpl.caching = caching;
	}
	
	
	@SuppressWarnings("unchecked")
	private Class<? extends UIEvent> getEventClass(Method m){
		return (Class<? extends UIEvent>) m.getParameterTypes()[0];
	}
	

    
	@Override
	public void addHandler(Object handler){
		boolean added = false;
		if (caching){
			if (!eventMethodChache.isClassCached(handler.getClass())) 
				added = scanHandlerAndCreateEventDispatcher(handler);
			else
				added = createEventDispatchersFromCache(handler);
		}
		else
			added = scanHandlerAndCreateEventDispatcher(handler);
		LOGGER.info(getClass().getSimpleName()+".addHandler(<"+handler.getClass().getSimpleName()+">) --> "+(added ? "added" : "not added"));
	}
	
	
	private boolean scanHandlerAndCreateEventDispatcher(Object handler){
		boolean added = false;
		for (Method m : handler.getClass().getMethods())
		{
			if (!m.isAnnotationPresent(UIEventHandler.class))
				continue;
			
			Class<?> params [] = m.getParameterTypes();
			if (params.length == 1 && isEventClass(params[0])){
				// This Method is a Valid AppEventHandler
				EventDispatcher disp = new EventDispatcher(handler, m);
				addEventDispatcher(getEventClass(m), disp);
				added = true;
				
				if (caching)
					eventMethodChache.addMethodToCache(handler.getClass(), m);
			}
			else
				throw new Error("You have annotated the Method "+m.getName()+" with @"+UIEventHandler.class.getSimpleName()+", " +
						"but this method did not match the required one Parameter (exactly one) of the type Event");
		}
		
		return added;
	}
	
	
	private boolean isEventClass(Class<?> clazz){
		return UIEvent.class.isAssignableFrom(clazz);
		
	}
	
	private boolean createEventDispatchersFromCache(Object handler){
	
		Set<Method> methods = eventMethodChache.getMethods(handler.getClass());
		
		if (methods == null)
			throw new Error("The class "+handler.getClass().getName()+" has not been cached until now. However the EventBus tries to create a EventDispatcher from the cache.");
		
		for (Method m : methods){
			EventDispatcher disp = new EventDispatcher(handler, m);
			addEventDispatcher(getEventClass(m), disp);
		}
		
		return !methods.isEmpty();
	}
	
	private void addEventDispatcher(Class<? extends UIEvent> eventClass, 
			EventDispatcher disp){
		
		Set<EventDispatcher> dispatchers = handlerMap.get(eventClass);
		
		if(dispatchers == null){
			dispatchers = new LinkedHashSet<EventDispatcher>();
			handlerMap.put(eventClass, dispatchers);
		}
		
		dispatchers.add(disp);
	}
	
	@Override
	public void removeHandler(Object handler){
		
		Set<EventDispatcher> toRemove = new LinkedHashSet<EventDispatcher>();
		for (Set<EventDispatcher> dispatchers : handlerMap.values()){
			for (EventDispatcher d: dispatchers)
				if (d.getTarget() == handler)
					toRemove.add(d);
			
			dispatchers.removeAll(toRemove);
			toRemove.clear();
		}
	}
	
	@Override
	public boolean fireEvent(UIEvent event){
		
		Set<EventDispatcher> dispatchers = handlerMap.get(event.getClass());
		if (dispatchers == null || dispatchers.isEmpty())
			return false;
		
		for (EventDispatcher disp : dispatchers)
			disp.dispatchEvent(event);
		
		return true;
	}
	
	
	
}
