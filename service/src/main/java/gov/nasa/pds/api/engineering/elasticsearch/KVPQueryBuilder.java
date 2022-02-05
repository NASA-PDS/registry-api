package gov.nasa.pds.api.engineering.elasticsearch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.engineering.elasticsearch.business.ProductQueryBuilderUtil;

public class KVPQueryBuilder
{
    private static final Logger log = LoggerFactory.getLogger(KVPQueryBuilder.class);

    private String esIndex;
    
    private List<String> fields;
    private Map<String,List<String>> kvps;
    
    private boolean archiveStatus;

    
    public KVPQueryBuilder(String esIndex, boolean filterByArchiveStatus)
    {
        this.esIndex = esIndex;
        this.archiveStatus = filterByArchiveStatus;
    }

    
    public KVPQueryBuilder(String esIndex)
    {
        this(esIndex, false);
    }

    
    public void setFields(String field)
    {
        fields = Arrays.asList(field);
    }

    
    public void setFields(List<String> fields)
    {
        this.fields = fields;
    }

    
    public void setKVP(String key, String value)
    {
        kvps = new HashMap<>();
        kvps.put(key, Arrays.asList(value));
    }

    
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
        if(archiveStatus) ProductQueryBuilderUtil.addArchiveStatusFilter(boolQuery);
        
        // KVP
        kvps.forEach((key, values) -> 
        {
            values.forEach((value) -> 
            {
                if(term) boolQuery.must(QueryBuilders.termQuery(key, value));
                else boolQuery.must(QueryBuilders.matchQuery(key, value));
            });
        });
        
        // Create request
        SearchRequest request = new SearchRequest(esIndex);                
        request.source(new SearchSourceBuilder().query(boolQuery).fetchSource(include, exclude));
        
        log.debug(request.toString());
        
        return request;
    }

}
