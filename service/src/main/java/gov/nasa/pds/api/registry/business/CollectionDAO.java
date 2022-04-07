package gov.nasa.pds.api.registry.business;

import java.util.HashMap;
import java.util.Map;

public class CollectionDAO
{
    static public Map<String,String> searchConstraints()
    {
    	Map<String,String> preset = new HashMap<String,String>();
    	preset.put("product_class", "Product_Collection");
    	return preset;
    }
}
