package gov.nasa.pds.api.engineering.controllers;


import gov.nasa.pds.api.base.CollectionsApi;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnectionImpl;
import gov.nasa.pds.api.engineering.entities.EntityCollection;
import gov.nasa.pds.api.engineering.entities.EntityProduct;
import gov.nasa.pds.model.Products;
import gov.nasa.pds.model.Summary;
import gov.nasa.pds.model.Metadata;
import gov.nasa.pds.model.Product;
import gov.nasa.pds.model.Reference;
import gov.nasa.pds.model.ErrorMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import org.elasticsearch.client.RestHighLevelClient;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.client.RequestOptions;


import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashSet;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnection;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistrySearchRequestBuilder;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchUtil;


@Controller
public class MyCollectionsApiController implements CollectionsApi {

    private static final Logger log = LoggerFactory.getLogger(MyCollectionsApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
 
    public MyCollectionsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }
    
    @Autowired
    ElasticSearchRegistryConnection esRegistryConnection;

    public ResponseEntity<Products> getCollection(@ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start
    		,@ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit
    		,@ApiParam(value = "search query, complex query uses eq,ne,gt,ge,lt,le,(,),not,and,or. Properties are named as in 'properties' attributes, literals are strings between \\\" or numbers.") @Valid @RequestParam(value = "q", required = false) String q
    		,@ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields
    		,@ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort
    		,@ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue="false") Boolean onlySummary
    		) {
    	
        String accept = request.getHeader("Accept");
        log.info("accept value is " + accept);
        if (accept != null 
        		&& (accept.contains("application/json") 
        				|| accept.contains("text/html")
        				|| accept.contains("*/*"))) {
        	
        	try {
	        		
	        	ElasticSearchRegistrySearchRequestBuilder searchRequestBuilder = new ElasticSearchRegistrySearchRequestBuilder(this.esRegistryConnection.getRegistryIndex(),
	        			this.esRegistryConnection.getTimeOutSeconds());
	        	
	        	SearchRequest searchRequest = searchRequestBuilder.getSearchCollectionRequest(q, start, limit);
	        	
	        	SearchResponse searchResponse = this.esRegistryConnection.getRestHighLevelClient().search(searchRequest, 
	        			RequestOptions.DEFAULT);
	        	
	        	Products products = new Products();
	        	
	        	HashSet<String> uniqueProperties = new HashSet<String>();
	        	
	          	Summary summary = new Summary();
	        	

	          	summary.setQ((q != null)?q:"" );
	        	summary.setStart(start);
	        	summary.setLimit(limit);
	        	
	        	if (sort == null) {
	        		sort = Arrays.asList();
	        	}	
	        	summary.setSort(sort);
	        	
	        	products.setSummary(summary);
	        	
	        	if (searchResponse != null) {
	        		
	        		for (SearchHit searchHit : searchResponse.getHits()) {
	        	        Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
	        	        
	        	        Map<String, Object> sourceAsMapJsonProperties =	ElasticSearchUtil.elasticHashMapToJsonHashMap(sourceAsMap);
	        	        uniqueProperties.addAll(sourceAsMapJsonProperties.keySet());
	
	        	        if (!onlySummary) {
		        	        EntityCollection entityCollection = objectMapper.convertValue(sourceAsMap, EntityCollection.class);
		        	        Product product = ElasticSearchUtil.ESentityProductToAPIProduct(entityCollection);
		        	        product.setProperties(sourceAsMapJsonProperties);
		        	        
		        	        products.addDataItem(product);
	        	        }
	        	        
	        	    }
	         
	                
	        	}
	        	
	        	
	        	summary.setProperties(new ArrayList<String>(uniqueProperties));
	        	
	        	return new ResponseEntity<Products>(products, HttpStatus.OK);
	        	
    	  } catch (IOException e) {
              log.error("Couldn't serialize response for content type application/json", e);
              return new ResponseEntity<Products>(HttpStatus.INTERNAL_SERVER_ERROR);
          }
            
        }
        else return new ResponseEntity<Products>(HttpStatus.NOT_IMPLEMENTED);
    }

}