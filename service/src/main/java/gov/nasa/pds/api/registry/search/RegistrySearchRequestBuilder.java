package gov.nasa.pds.api.registry.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.registry.business.BlobUtil;
import gov.nasa.pds.api.registry.business.ProductQueryBuilderUtil;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.TermQueryBuilder;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.PrefixQueryBuilder;
import org.opensearch.index.query.QueryBuilder;

public class RegistrySearchRequestBuilder
{
    private static final Logger log = LoggerFactory.getLogger(RegistrySearchRequestBuilder.class);
    
    final public String registryIndex;
    final public String registryRefIndex;
    private int timeOutSeconds;
    
    public RegistrySearchRequestBuilder(
            String registryIndex, 
            String registryRefIndex, 
            int timeOutSeconds)
    {
        this.registryIndex = registryIndex;
        this.registryRefIndex = registryRefIndex;
        this.timeOutSeconds = timeOutSeconds;
    }
    
    public SearchRequest getSearchProductRequestHasLidVidPrefix(String lidvid)
    {
        PrefixQueryBuilder prefixQueryBuilder = QueryBuilders.prefixQuery("lidvid", lidvid);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(prefixQueryBuilder);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSourceBuilder);
        searchRequest.indices(this.registryIndex);
        return searchRequest;
    }

    public SearchRequest getSearchProductsByLid(String lid, int from, int size) 
    {
        TermQueryBuilder termQuery = QueryBuilders.termQuery("lid", lid);
        SearchSourceBuilder srcBuilder = new SearchSourceBuilder();
        srcBuilder.query(termQuery);
        srcBuilder.from(from);
        srcBuilder.size(size);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(srcBuilder);
        searchRequest.indices(this.registryIndex);
        return searchRequest;
    }
    
    public SearchRequest getSearchProductsRequest(
            String queryString,
            List<String> keywords,
            List<String> fields, 
            int start, int limit, 
            Map<String,String> presetCriteria) 
    {
        QueryBuilder query = null;
        
        // "keyword" parameter provided. Run full-text query.
        if(keywords != null)
        {
        	// FIXME: list of booleans rather than single
        	for (String keyword : keywords) query = ProductQueryBuilderUtil.createKeywordQuery(keyword, presetCriteria);
        }
        // Run PDS query language ("q" parameter) query
        else
        {
            query = ProductQueryBuilderUtil.createPqlQuery(queryString, fields, presetCriteria);
        }
        
        String[] excludedFields = {}; //excludes (fields);
        String[] includedFields = fields.toArray(new String[0]);

        SearchRequestBuilder bld = new SearchRequestBuilder(query, start, limit);
        bld.fetchSource(true, includedFields, excludedFields);
        bld.setTimeoutSeconds(this.timeOutSeconds);        
        SearchRequest searchRequest = bld.build(this.registryIndex);
        log.debug("opensearch request :" + searchRequest.toString());

        return searchRequest;
    }
    
    public SearchRequest getSearchCollectionRequest(String queryString, List<String> keywords, List<String> fields, int start, int limit) {
        Map<String, String> presetCriteria = new HashMap<String, String>();
        presetCriteria.put("product_class", "Product_Collection");
        return getSearchProductsRequest(queryString, keywords, fields, start, limit, presetCriteria);
    }
}
