package co.com.binariasystems.fmw.vweb.uicomponet;

import com.vaadin.server.Sizeable.Unit;

public class Dimension {
	public Unit unit;
	public float value;
	public Dimension(Unit unit, float value){
		this.unit = unit;
		this.value = value;
	}
	public static Dimension pixels(float value){
		return new Dimension(Unit.PIXELS, value);
	}
	public static Dimension points(float value){
		return new Dimension(Unit.POINTS, value);
	}
	public static Dimension picas(float value){
		return new Dimension(Unit.PICAS, value);
	}
	public static Dimension em(float value){
		return new Dimension(Unit.EM, value);
	}
	public static Dimension rem(float value){
		return new Dimension(Unit.REM, value);
	}
	public static Dimension ex(float value){
		return new Dimension(Unit.EX, value);
	}
	public static Dimension mm(float value){
		return new Dimension(Unit.MM, value);
	}
	public static Dimension cm(float value){
		return new Dimension(Unit.CM, value);
	}
	public static Dimension inch(float value){
		return new Dimension(Unit.INCH, value);
	}
	public static Dimension percent(float value){
		return new Dimension(Unit.PERCENTAGE, value);
	}
}
