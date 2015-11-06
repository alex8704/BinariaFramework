package co.com.binariasystems.fmw.vweb.uicomponet;

import com.vaadin.server.Sizeable.Unit;

public class Dimension {
	public Unit unit;
	public float value;
	public Dimension(float value, Unit unit){
		this.value = value;
		this.unit = unit;
	}
	public static Dimension pixels(float value){
		return new Dimension(value, Unit.PIXELS);
	}
	public static Dimension points(float value){
		return new Dimension(value, Unit.POINTS);
	}
	public static Dimension picas(float value){
		return new Dimension(value, Unit.PICAS);
	}
	public static Dimension em(float value){
		return new Dimension(value, Unit.EM);
	}
	public static Dimension rem(float value){
		return new Dimension(value, Unit.REM);
	}
	public static Dimension ex(float value){
		return new Dimension(value, Unit.EX);
	}
	public static Dimension mm(float value){
		return new Dimension(value, Unit.MM);
	}
	public static Dimension cm(float value){
		return new Dimension(value, Unit.CM);
	}
	public static Dimension inch(float value){
		return new Dimension(value, Unit.INCH);
	}
	public static Dimension percent(float value){
		return new Dimension(value, Unit.PERCENTAGE);
	}
	
	public static Dimension fullPercent(){
		return percent(100.0f);
	}
}
