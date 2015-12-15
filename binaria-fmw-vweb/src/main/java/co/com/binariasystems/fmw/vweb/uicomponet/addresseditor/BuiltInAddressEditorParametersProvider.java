package co.com.binariasystems.fmw.vweb.uicomponet.addresseditor;


import static co.com.binariasystems.fmw.vweb.resources.resources.getMessageFilePath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import co.com.binariasystems.fmw.dto.ComparableListableDTO;
import co.com.binariasystems.fmw.dto.Listable;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;
import co.com.binariasystems.fmw.vweb.resources.resources;

public class BuiltInAddressEditorParametersProvider implements AddressEditorParametersProvider {
	private MessageBundleManager messages = MessageBundleManager.forPath(getMessageFilePath("address_editor"), resources.class);

	@Override
	public List<? extends Listable> getViaTypes(Locale userLocale) {
		List<ComparableListableDTO> viaTypes = new ArrayList<ComparableListableDTO>();
		for(ViaType viaType : ViaType.values()){
			ComparableListableDTO item = new ComparableListableDTO(viaType.getCode(), messages.getString(viaType.getLocalizedDescKey(), userLocale));
			viaTypes.add(item);
		}
		
		Collections.sort(viaTypes);
		return viaTypes;
	}

	@Override
	public List<? extends Listable> getNomenclatureComplements(Locale userLocale) {
		List<ComparableListableDTO> nomenclatureComplements = new ArrayList<ComparableListableDTO>();
		for(NomenclatureComplement nomenclatureComplement : NomenclatureComplement.values()){
			ComparableListableDTO item = new ComparableListableDTO(nomenclatureComplement.getCode(), messages.getString(nomenclatureComplement.getLocalizedDescKey(), userLocale));
			nomenclatureComplements.add(item);
		}
		
		Collections.sort(nomenclatureComplements);
		return nomenclatureComplements;
	}

	@Override
	public List<? extends Listable> getQuadrants(Locale userLocale) {
		List<ComparableListableDTO> quadrants = new ArrayList<ComparableListableDTO>();
		for(Quadrant quadrant : Quadrant.values()){
			ComparableListableDTO item = new ComparableListableDTO(quadrant.getCode(), messages.getString(quadrant.getLocalizedDescKey(), userLocale));
			quadrants.add(item);
		}
		
		Collections.sort(quadrants);
		return quadrants;
	}

	@Override
	public List<? extends Listable> getBis(Locale userLocale) {
		List<ComparableListableDTO> bises = new ArrayList<ComparableListableDTO>();
		for(Bis bis : Bis.values()){
			ComparableListableDTO item = new ComparableListableDTO(bis.getCode(), messages.getString(bis.getLocalizedDescKey(), userLocale));
			bises.add(item);
		}
		
		Collections.sort(bises);
		return bises;
	}

}
