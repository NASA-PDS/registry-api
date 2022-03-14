package gov.nasa.pds.api.registry.search;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.registry.business.ProductQueryBuilderUtil;

/**
 * Builds Elasticsearch queries
 * @author karpenko
 */
public class KVPQueryBuilder
{
    private static final Logger log = LoggerFactory.getLogger(KVPQueryBuilder.class);

    private String esIndex;
    
    private List<String> fields;
    private Map<String,List<String>> kvps;
    
    private boolean filterByArchiveStatus = false;

    
    /**
     * Constructor
     * @param esIndex Elasticsearch index
     */
    public KVPQueryBuilder(String esIndex)
    {
        this.esIndex = esIndex;
    }

    
    public void setFilterByArchiveStatus(Boolean b)
    {
        filterByArchiveStatus = b;
    }
    
    
    /**
     * Return these field names
     * @param field field name (path)
     */
    public void setFields(String field)
    {
        fields = Arrays.asList(field);
    }

    
    /**
     * Return these field names
     * @param fields field names (paths)
     */
    public void setFields(List<String> fields)
    {
        this.fields = fields;
    }


    /**
     * Set query criteria
     * @param key field name
     * @param value field value
     */
    public void setKVP(String key, String value)
    {
        kvps = new HashMap<>();
        kvps.put(key, Arrays.asList(value));
    }

    
    /**
     * Set query criteria
     * @param key field name
     * @param values field values
     */
    public void setKVP(String key, List<String> values)
    {
        kvps = new HashMap<>();
        kvps.put(key, values);
    }


    public SearchRequest buildTermQuery()
    {
        return build(true);
    }

    
    public SearchRequest buildMatchQuery()
    {
        return build(false);
    }

    
    private SearchRequest build(boolean term)
    {
        String[] exclude = ElasticSearchRegistrySearchRequestBuilder.excludes(fields);
        String[] include = fields.toArray(new String[0]);
        
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        // Archive status filter
        if(filterByArchiveStatus) ProductQueryBuilderUtil.addArchiveStatusFilter(boolQuery);
        
        // KVP
        kvps.forEach((key, values) -> 
        {
            if(term) 
            {
                boolQuery.must(QueryBuilders.termsQuery(key, values));
            }
            else
            {
                values.forEach((value) -> 
                {
                    boolQuery.must(QueryBuilders.matchQuery(key, value));
                });
            }
        });
        
        // Create request
        SearchRequest request = new SearchRequest(esIndex);                
        request.source(new SearchSourceBuilder().query(boolQuery).fetchSource(include, exclude));
        
        log.debug(request.toString());
        
        return request;
    }

}
