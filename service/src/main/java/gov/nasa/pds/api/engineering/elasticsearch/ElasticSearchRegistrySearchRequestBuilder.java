package gov.nasa.pds.api.engineering.elasticsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import gov.nasa.pds.api.engineering.elasticsearch.business.BlobUtil;
import gov.nasa.pds.api.engineering.elasticsearch.business.ProductQueryBuilderUtil;

public class ElasticSearchRegistrySearchRequestBuilder
{
    private static final Logger log = LoggerFactory.getLogger(ElasticSearchRegistrySearchRequestBuilder.class);
    
    final public String registryIndex;
    final public String registryRefIndex;
    private int timeOutSeconds;
    
    public ElasticSearchRegistrySearchRequestBuilder(
            String registryIndex, 
            String registryRefindex, 
            int timeOutSeconds)
    {
        this.registryIndex = registryIndex;
        this.registryRefIndex = registryRefindex;
        this.timeOutSeconds = timeOutSeconds;
    }
    
    public ElasticSearchRegistrySearchRequestBuilder()
    {
        this.registryIndex = "registry";
        this.registryRefIndex = "registry-refs";
        this.timeOutSeconds = 60;
    }

    static private String[] excludes (List<String> fields)
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
        log.debug("Elasticsearch request :" + searchRequest.toString());

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
    
    static public SearchRequest getqueryfieldfromlidvid (String lidvid, String field, String es_index)
    {
        List<String> fields = new ArrayList<String>(), lidvids = new ArrayList<String>();
        Map<String,List<String>> kvps = new HashMap<String,List<String>>();
        fields.add(field);
        lidvids.add(lidvid);
        kvps.put("lidvid", lidvids);
        return getQueryForKVPs (kvps, fields, es_index);
    }

    static public SearchRequest getQueryFieldFromKVP (String key, List<String>values, String field, String es_index)
    {
        List<String> fields = new ArrayList<String>();
        Map<String,List<String>> kvps = new HashMap<String,List<String>>();
        fields.add(field);
        kvps.put(key, values);
        return getQueryForKVPs (kvps, fields, es_index);        
    }

    static public SearchRequest getQueryFieldFromKVP (String key, String value, String field, String es_index)
    {
        List<String> fields = new ArrayList<String>(), values = new ArrayList<String>();
        Map<String,List<String>> kvps = new HashMap<String,List<String>>();
        fields.add(field);
        values.add(value);
        kvps.put(key, values);
        return getQueryForKVPs (kvps, fields, es_index);
    }

    static public SearchRequest getQueryFieldsFromKVP (String key, String value, List<String> fields, String es_index, boolean term)
    {
        List<String> values = new ArrayList<String>();
        Map<String,List<String>> kvps = new HashMap<String,List<String>>();
        values.add(value);
        kvps.put(key, values);
        return getQueryForKVPs (kvps, fields, es_index, term);
    }

    static public SearchRequest getQueryFieldsFromKVP (String key, List<String> values, List<String> fields, String es_index)
    {
        Map<String,List<String>> kvps = new HashMap<String,List<String>>();
        kvps.put(key, values);
        return getQueryForKVPs (kvps, fields, es_index);        
    }

    static public SearchRequest getQueryFieldsFromKVP (String key, List<String> values, List<String> fields, String es_index, boolean term)
    {
        Map<String,List<String>> kvps = new HashMap<String,List<String>>();
        kvps.put(key, values);
        return getQueryForKVPs (kvps, fields, es_index, term);      
    }

    static public SearchRequest getQueryForKVPs (Map<String,List<String>> kvps, List<String> fields, String es_index)
    {
        return getQueryForKVPs (kvps, fields, es_index, true);
    }

    static public SearchRequest getQueryForKVPs (Map<String,List<String>> kvps, List<String> fields, String es_index, boolean term)
    {
    	String[] exclude = excludes (fields);
        String[] include = fields.toArray(new String[0]);
        
        BoolQueryBuilder find_kvps = QueryBuilders.boolQuery();
        SearchRequest request = new SearchRequest(es_index)
                .source(new SearchSourceBuilder().query(find_kvps)
                        .fetchSource(include, exclude));

        log.info("****************************************");
        log.info("****************        exclude");
        for (String e : exclude) log.info("****************           " + e);
        log.info("****************************************");
        log.info("****************        include");
        for (String i : include) log.info("****************           " + i);
        log.info("****************************************");
        for (Entry<String,List<String>> key : kvps.entrySet())
        {
            for (String value : key.getValue())
            {
                if (term) find_kvps.should (QueryBuilders.termQuery (key.getKey(), value));
                else find_kvps.should (QueryBuilders.matchQuery (key.getKey(), value));
            }
        }
        return request;
    }
}
