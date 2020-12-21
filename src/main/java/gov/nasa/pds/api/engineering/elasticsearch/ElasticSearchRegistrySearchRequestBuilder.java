package gov.nasa.pds.api.engineering.elasticsearch;

import java.util.concurrent.TimeUnit;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.common.unit.TimeValue;

import gov.nasa.pds.api.engineering.lexer.SearchLexer;
import gov.nasa.pds.api.engineering.lexer.SearchParser;
import gov.nasa.pds.api.engineering.elasticsearch.Antlr4SearchListener;


public class ElasticSearchRegistrySearchRequestBuilder {
	
	private static final Logger log = LoggerFactory.getLogger(ElasticSearchRegistrySearchRequestBuilder.class);
	
	private String registryIndex;
	private int timeOutSeconds;
	
	public ElasticSearchRegistrySearchRequestBuilder(String registryIndex, int timeOutSeconds) {
		
		this.registryIndex = registryIndex;
		this.timeOutSeconds = timeOutSeconds;
	
	}
	
	public ElasticSearchRegistrySearchRequestBuilder() {
		
		this.registryIndex = "registry";
		this.timeOutSeconds = 60;
	
	}

	public SearchRequest getSearchCollectionRequest(String queryString, int start, int limit) {
		
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		
		if (queryString != null) {
			CodePointCharStream input = CharStreams.fromString(queryString);
	        SearchLexer lex = new SearchLexer(input);
	        CommonTokenStream tokens = new CommonTokenStream(lex);
	
	        SearchParser par = new SearchParser(tokens);
	        ParseTree tree = par.query();
	        
	        ElasticSearchRegistrySearchRequestBuilder.log.info(tree.toStringTree(par));
	                
	        // Walk it and attach our listener
	        ParseTreeWalker walker = new ParseTreeWalker();
	        Antlr4SearchListener listener = new Antlr4SearchListener(boolQuery);
	        walker.walk(listener, tree);
	        	        
	        boolQuery = listener.getBoolQuery();
		}
        
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        boolQuery.must(QueryBuilders.termQuery( "product_class", "Product_Collection"));
    	searchSourceBuilder.query(boolQuery);
    	searchSourceBuilder.from(start); 
    	searchSourceBuilder.size(limit); 
    	searchSourceBuilder.timeout(new TimeValue(this.timeOutSeconds, 
    			TimeUnit.SECONDS)); 
    	
    	SearchRequest searchRequest = new SearchRequest();
    	searchRequest.source(searchSourceBuilder);
    	searchRequest.indices(this.registryIndex);
    	
        this.log.info("q value: " + queryString);
    	this.log.info("request elasticSearch :" + searchRequest.toString());
    	
    	return searchRequest;
	}
	
}
