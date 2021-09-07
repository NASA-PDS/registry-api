package gov.nasa.pds.api.engineering.elasticsearch;

import java.util.List;
import java.util.Map;

public class GetProductsRequest
{
    public String queryString;
    public String keyword;
    public List<String> fields;
    public List<String> sort;
    public int start;
    public int limit;
    public Map<String, String> presetCriteria;
    public boolean onlySummary;
    
    
    public GetProductsRequest()
    {
    }

    
    public void setSearchCriteria(String q, String keyword)
    {
        this.queryString = q;
        this.keyword = keyword;
    }
    
    
    public void setPageInfo(int start, int limit)
    {
        this.start = start;
        this.limit = limit;
    }
    
    public void setFields(List<String> fields, List<String> sort)
    {
        this.fields = fields;
        this.sort = sort;
    }
    
}
