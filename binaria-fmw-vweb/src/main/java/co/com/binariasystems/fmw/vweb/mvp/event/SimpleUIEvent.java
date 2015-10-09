package co.com.binariasystems.fmw.vweb.mvp.event;

public class SimpleUIEvent<ID_TYPE> extends AbstractUIEvent<ID_TYPE> {
	private ID_TYPE id;
	public SimpleUIEvent(ID_TYPE id){
		this.id = id;
	}
	@Override
	public ID_TYPE getId() {
		return id;
	}

}
