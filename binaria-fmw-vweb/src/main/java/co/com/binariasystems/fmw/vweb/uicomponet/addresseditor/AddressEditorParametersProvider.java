package co.com.binariasystems.fmw.vweb.uicomponet.addresseditor;

import java.util.List;
import java.util.Locale;

import co.com.binariasystems.fmw.dto.Listable;

public interface AddressEditorParametersProvider {
	public List<? extends Listable> getViaTypes(Locale userLocale);
	public List<? extends Listable> getNomenclatureComplements(Locale userLocale);
	public List<? extends Listable> getQuadrants(Locale userLocale);
	public List<? extends Listable> getBis(Locale userLocale);
}
