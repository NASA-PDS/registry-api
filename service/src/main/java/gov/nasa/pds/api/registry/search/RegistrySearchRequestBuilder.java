package gov.nasa.pds.api.registry.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.registry.business.BlobUtil;
import gov.nasa.pds.api.registry.business.ProductQueryBuilderUtil;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.TermQueryBuilder;
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
    
    public RegistrySearchRequestBuilder()
    {
        this.registryIndex = "registry";
        this.registryRefIndex = "registry-refs";
        this.timeOutSeconds = 60;
    }

    static public String[] excludes (List<String> fields)
    {
    	String[] exclude, ex0 = new String[0], exbp = {BlobUtil.XML_BLOB_PROPERTY},
                exjbp = {BlobUtil.JSON_BLOB_PROPERTY},
                exall = {BlobUtil.XML_BLOB_PROPERTY, BlobUtil.JSON_BLOB_PROPERTY};

    	if (fields.contains(BlobUtil.XML_BLOB_PROPERTY) && fields.contains(BlobUtil.JSON_BLOB_PROPERTY)) exclude = ex0;
    	else if (fields.contains(BlobUtil.XML_BLOB_PROPERTY)) exclude = exjbp;
    	else if (fields.contains(BlobUtil.JSON_BLOB_PROPERTY)) exclude = exbp;
    	else exclude = exall;

    	return exclude;
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
            String keyword,
            List<String> fields, 
            int start, int limit, 
            Map<String,String> presetCriteria) 
    {
        QueryBuilder query = null;
        
        // "keyword" parameter provided. Run full-text query.
        if(keyword != null && !keyword.isBlank())
        {
            query = ProductQueryBuilderUtil.createKeywordQuery(keyword, presetCriteria);
        }
        // Run PDS query language ("q" parameter) query
        else
        {
            query = ProductQueryBuilderUtil.createPqlQuery(queryString, fields, presetCriteria);
        }
        
        String[] excludedFields = excludes (fields);
        String[] includedFields = fields.toArray(new String[0]);

        SearchRequestBuilder bld = new SearchRequestBuilder(query, start, limit);
        bld.fetchSource(true, includedFields, excludedFields);
        bld.setTimeoutSeconds(this.timeOutSeconds);        
        SearchRequest searchRequest = bld.build(this.registryIndex);
        log.debug("opensearch request :" + searchRequest.toString());

        return searchRequest;
    }

    
    public SearchRequest getSearchProductRequest(String queryString, String keyword, List<String> fields, int start, int limit) {
        Map<String, String> presetCriteria = new HashMap<String, String>();
        return getSearchProductsRequest(queryString, keyword, fields, start, limit, presetCriteria);        
    }
    
    public SearchRequest getSearchCollectionRequest(String queryString, String keyword, List<String> fields, int start, int limit) {
        Map<String, String> presetCriteria = new HashMap<String, String>();
        presetCriteria.put("product_class", "Product_Collection");
        return getSearchProductsRequest(queryString, keyword, fields, start, limit, presetCriteria);
    }

}
