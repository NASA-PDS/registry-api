package gov.nasa.pds.api.engineering.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EntityCollection extends EntityProduct {
	@JsonProperty("pds/Modification_Detail/pds/modification_date")
    private List<String> modification_date;
	
	public String getModificationDate() {
		return modification_date.get(0);
	}
	

}
