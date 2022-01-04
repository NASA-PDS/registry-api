package gov.nasa.pds.api.engineering.elasticsearch.entities;

import java.util.List;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntityProduct;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EntityCollection extends EntityProduct {
	@JsonProperty("pds/Modification_Detail/pds/modification_date")
    private List<String> modification_date;
	
	public String getModificationDate() {
		if (modification_date != null) {
			return modification_date.get(0);
		}
		else {
			return null;
		}
	}
	

}
