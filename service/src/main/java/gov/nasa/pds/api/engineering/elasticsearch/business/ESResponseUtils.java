package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class ESResponseUtils
{
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List<String> getFieldValues(Map<String, Object> fieldMap, String fieldName)
    {
        if(fieldMap == null || fieldName == null) return null;
        
        Object obj = fieldMap.get(fieldName);
        if(obj == null) return null;

        if(obj instanceof String)
        {
            return Arrays.asList((String)obj);
        }
        else if(obj instanceof List)
        {
            return (List)obj;
        }
        
        return null;
    }
}
