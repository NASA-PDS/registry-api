package gov.nasa.pds.api.engineering.elasticsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.lang.Math;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import gov.nasa.pds.api.engineering.elasticsearch.business.CollectionProductRefBusinessObject;
import gov.nasa.pds.api.engineering.elasticsearch.business.ProductQueryBuilderUtil;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntityProduct;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntitytProductWithBlob;


public class ElasticSearchRegistrySearchRequestBuilder {
    
    private static final Logger log = LoggerFactory.getLogger(ElasticSearchRegistrySearchRequestBuilder.class);
    private static final String[] DEFAULT_ALL_FIELDS = { "*" };
    
    private static final String[] DEFAULT_BLOB = { "ops:Label_File_Info/ops:blob" };
    
    private String registryIndex;
    private String registryRefIndex;
    private int timeOutSeconds;
    
    public ElasticSearchRegistrySearchRequestBuilder(
            String registryIndex, 
            String registryRefindex, 
            int timeOutSeconds) {
        
        this.registryIndex = registryIndex;
        this.registryRefIndex = registryRefindex;
        this.timeOutSeconds = timeOutSeconds;
    
    }
    
    
    
    public ElasticSearchRegistrySearchRequestBuilder() {
        
        this.registryIndex = "registry";
        this.registryRefIndex = "registry-refs";
        this.timeOutSeconds = 60;
    
    }


    public SearchRequest getSearchProductRefsFromCollectionLidVid(
            String lidvid,
            int start,
            int limit) {
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("collection_lidvid", lidvid);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        
        int productRefStart = (int)Math.floor(start/(float)CollectionProductRefBusinessObject.PRODUCT_REFERENCES_BATCH_SIZE);
        int productRefLimit = (int)Math.ceil(limit/(float)CollectionProductRefBusinessObject.PRODUCT_REFERENCES_BATCH_SIZE);
        log.debug("Request product reference documents from " + Integer.toString(productRefStart) + " for size " + Integer.toString(productRefLimit) );
        searchSourceBuilder.query(matchQueryBuilder)
            .from(productRefStart)
            .size(productRefLimit);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSourceBuilder);
        searchRequest.indices(this.registryRefIndex);

        
        log.debug("search product ref request :" + searchRequest.toString());
        
        return searchRequest;
        
    }
    
    public SearchRequest getSearchProductRequestHasLidVidPrefix(String lidvid) {
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

    
    
    public GetRequest getGetProductRequest(String lidvid, boolean withXMLBlob) {
        
        GetRequest getProductRequest = new GetRequest(this.registryIndex, 
                lidvid);
        
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, null, withXMLBlob?null:new String[] {  EntitytProductWithBlob.BLOB_PROPERTY });
        
        getProductRequest.fetchSourceContext(fetchSourceContext);
        
        return getProductRequest;
        
    }


    public GetRequest getGetProductRequest(String lidvid) {
        
        return this.getGetProductRequest(lidvid, false);
        
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
        
        String[] includedFields = createIncludedFields(fields);
        String[] excludedFields = { EntitytProductWithBlob.BLOB_PROPERTY };

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
    
    static public SearchRequest getQueryFieldFromLidvid (String lidvid, String field, String es_index)
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
        String[] aFields = new String[fields == null ? 0 : fields.size() + EntityProduct.JSON_PROPERTIES.length];
        if (fields != null)
        {
            for (int i = 0 ; i < EntityProduct.JSON_PROPERTIES.length ; i++) aFields[i] = EntityProduct.JSON_PROPERTIES[i];
            for (int i = 0 ; i < fields.size(); i++) aFields[i+EntityProduct.JSON_PROPERTIES.length] = ElasticSearchUtil.jsonPropertyToElasticProperty(fields.get(i));
        }

        BoolQueryBuilder find_kvps = QueryBuilders.boolQuery();
        SearchRequest request = new SearchRequest(es_index)
                .source(new SearchSourceBuilder().query(find_kvps)
                        .fetchSource(fields == null ? DEFAULT_ALL_FIELDS : aFields, DEFAULT_BLOB));

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
    
    
    private String[] createIncludedFields(List<String> fields)
    {
        if(fields == null || fields.isEmpty()) return null;
        
        HashSet<String> esFields = new HashSet<String>(Arrays.asList(EntityProduct.JSON_PROPERTIES));
        for (int i = 0; i < fields.size(); i++)
        {
            String includedField = ElasticSearchUtil.jsonPropertyToElasticProperty((String) fields.get(i));
            ElasticSearchRegistrySearchRequestBuilder.log.debug("add field " + includedField + " to search");
            esFields.add(includedField);
        }

        String[] includedFields = esFields.toArray(new String[esFields.size()]);
        
        return includedFields;
    }


}
