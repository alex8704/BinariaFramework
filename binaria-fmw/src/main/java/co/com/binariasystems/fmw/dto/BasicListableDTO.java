package co.com.binariasystems.fmw.dto;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import co.com.binariasystems.fmw.constants.FMWConstants;

public class BasicListableDTO implements Serializable, Listable{
	private String PK;
	private String description;
	
	public String getPK() {
		return PK;
	}

	public String getDescription() {
		return description;
	}

	public void setPK(String pK) {
		PK = pK;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		StringBuilder toString = new StringBuilder();
		toString.append(StringUtils.defaultString(PK))
		.append(FMWConstants.PIPE)
		.append(StringUtils.defaultString(description));
		return toString.toString() ;
	}

}
