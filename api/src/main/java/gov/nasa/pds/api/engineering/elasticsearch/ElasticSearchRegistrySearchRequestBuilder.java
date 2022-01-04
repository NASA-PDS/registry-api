package gov.nasa.pds.api.engineering.elasticsearch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.lang.Math;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.common.unit.TimeValue;


import gov.nasa.pds.api.engineering.lexer.SearchLexer;
import gov.nasa.pds.api.engineering.lexer.SearchParser;
import gov.nasa.pds.api.model.ProductWithXmlLabel;
import gov.nasa.pds.model.Products;
import gov.nasa.pds.model.Summary;
import gov.nasa.pds.api.engineering.elasticsearch.business.CollectionProductRefBusinessObject;

public class ElasticSearchRegistrySearchRequestBuilder {
	
	private static final Logger log = LoggerFactory.getLogger(ElasticSearchRegistrySearchRequestBuilder.class);
	
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
	
	
	private BoolQueryBuilder parseQueryString(String queryString) {
		CodePointCharStream input = CharStreams.fromString(queryString);
        SearchLexer lex = new SearchLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lex);

        SearchParser par = new SearchParser(tokens);
        ParseTree tree = par.query();
        
        ElasticSearchRegistrySearchRequestBuilder.log.info(tree.toStringTree(par));
                
        // Walk it and attach our listener
        ParseTreeWalker walker = new ParseTreeWalker();
        Antlr4SearchListener listener = new Antlr4SearchListener();
        walker.walk(listener, tree);
        	        
        return listener.getBoolQuery();	
		
	}
	
	private BoolQueryBuilder parseFields(List<String> fields) {
		BoolQueryBuilder fieldsBoolQuery = QueryBuilders.boolQuery();
		String esField;
		ExistsQueryBuilder existsQueryBuilder;
		for (String field : fields) {
			esField = ElasticSearchUtil.jsonPropertyToElasticProperty(field);
			existsQueryBuilder = QueryBuilders.existsQuery(esField);
			fieldsBoolQuery.should(existsQueryBuilder);
		}
		fieldsBoolQuery.minimumShouldMatch(1);
		
		return fieldsBoolQuery;
	}
	
	
//	public SearchRequest getSearchProductsRequest(String queryString, int start, int limit, Map<String,String> presetCriteria) {
//		return getSearchProductsRequest(queryString, null, start, limit, presetCriteria);
//	}
	
	
	public SearchRequest getSearchProductsRequest(
			String queryString, 
			List<String> fields, 
			int start, int limit, 
			Map<String,String> presetCriteria) {

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		
		if (queryString != null) {
			boolQuery.must(this.parseQueryString(queryString));
		} 
	
        
       
        for (Map.Entry<String, String> e : presetCriteria.entrySet()) {
        	//example "product_class", "Product_Collection"
        	boolQuery.must(QueryBuilders.termQuery(e.getKey(), e.getValue() ));
        }
        
        
		if (fields != null) {
			boolQuery.must(this.parseFields(fields));
		}
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    	searchSourceBuilder.query(boolQuery);
    	searchSourceBuilder.from(start); 
    	searchSourceBuilder.size(limit); 
    	
    	/* TODO add the fetchsource to the request to reduce the payload being moved from elasticsearch
    	List<String> fetched_fields = fields.addAll(c);
    	searchSourceBuilder.fetchSource(fields., null);
    	*/
    	searchSourceBuilder.timeout(new TimeValue(this.timeOutSeconds, 
    			TimeUnit.SECONDS)); 
    	
    	SearchRequest searchRequest = new SearchRequest();
    	searchRequest.source(searchSourceBuilder);
    	searchRequest.indices(this.registryIndex);
    	
        ElasticSearchRegistrySearchRequestBuilder.log.info("q value: " + queryString);
    	ElasticSearchRegistrySearchRequestBuilder.log.info("request elasticSearch :" + searchRequest.toString());
    	
    	return searchRequest;

		
	}
	
	public SearchRequest getSearchProductRequest(String queryString, List<String> fields, int start, int limit) {
		Map<String, String> presetCriteria = new HashMap<String, String>();
		return getSearchProductsRequest(queryString, fields, start, limit, presetCriteria);		
	}
	
	public SearchRequest getSearchCollectionRequest(String queryString, List<String> fields, int start, int limit) {
		
		Map<String, String> presetCriteria = new HashMap<String, String>();
		presetCriteria.put("product_class", "Product_Collection");
		return getSearchProductsRequest(queryString, fields, start, limit, presetCriteria);
		
	}
	
}
