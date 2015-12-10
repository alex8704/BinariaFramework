package co.com.binariasystems.fmw.dto;

import org.apache.commons.lang3.StringUtils;

public class ComparableListableDTO implements Listable, Comparable<ComparableListableDTO> {
	
	private String pk;
	private String description;
	
	public ComparableListableDTO(String pk, String description){
		this.pk = pk;
		this.description = description;
	}
	

	@Override
	public String getPK() {
		return StringUtils.defaultString(pk);
	}

	@Override
	public String getDescription() {
		return StringUtils.defaultString(description);
	}

	@Override
	public int compareTo(ComparableListableDTO o) {
		getDescription().compareTo(o.getDescription());
		return 0;
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
}
