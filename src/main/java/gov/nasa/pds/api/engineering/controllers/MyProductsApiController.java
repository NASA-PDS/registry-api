package gov.nasa.pds.api.engineering.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.base.ProductsApi;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchUtil;
import gov.nasa.pds.api.engineering.elasticsearch.business.ProductBusinessObject;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntityProduct;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntitytProductWithBlob;
import gov.nasa.pds.api.engineering.exceptions.UnsupportedElasticSearchProperty;
import gov.nasa.pds.api.model.xml.ProductWithXmlLabel;
import gov.nasa.pds.api.model.xml.XMLMashallableProperyValue;
import gov.nasa.pds.model.Product;
import gov.nasa.pds.model.PropertyArrayValues;
import gov.nasa.pds.model.Products;
import gov.nasa.pds.model.Summary;
import io.swagger.annotations.ApiParam;



@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-10-29T11:01:11.991-07:00[America/Los_Angeles]")
@Controller
public class MyProductsApiController extends MyProductsApiBareController implements ProductsApi {

    private static final Logger log = LoggerFactory.getLogger(MyProductsApiController.class);
    
    @org.springframework.beans.factory.annotation.Autowired
    public MyProductsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
    	
    	super(objectMapper, request);
    }
    
   
    public ResponseEntity<Products> products(@ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start
,@ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit
,@ApiParam(value = "search query") @Valid @RequestParam(value = "q", required = false) String q
,@ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields
,@ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort
,@ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue="false") Boolean onlySummary
)  {
    	return this.getProductsResponseEntity(q, start, limit, fields, sort, onlySummary);

    }

    
     
    public ResponseEntity<Product> productsByLidvid(@ApiParam(value = "lidvid (urn)",required=true) @PathVariable("lidvid") String lidvid) {
    	return this.getProductResponseEntity(lidvid);
    }


	@Override
	public ResponseEntity<Products> bundlesContainingProduct(String lidvid, @Valid Integer start, @Valid Integer limit,
			@Valid List<String> fields, @Valid List<String> sort, @Valid Boolean summaryOnly) {
		String accept = this.request.getHeader("Accept");
		MyProductsApiController.log.info("accept value is " + accept);

		if ((accept != null 
				&& (accept.contains("application/json") 
						|| accept.contains("text/html")
		 				|| accept.contains("application/xml")
		 				|| accept.contains("application/pds4+xml")
		 				|| accept.contains("*/*")))
		 	|| (accept == null))
		{
			try
			{
		 		Products products = this.getContainingBundle(lidvid, start, limit, fields, sort, summaryOnly);		 		
		 		return new ResponseEntity<Products>(products, HttpStatus.OK);
			}
			catch (IOException e)
			{
				log.error("Couldn't serialize response for content type " + accept, e);
				return new ResponseEntity<Products>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		 }
		 else return new ResponseEntity<Products>(HttpStatus.NOT_IMPLEMENTED);
	}


	@SuppressWarnings("unchecked")
	private Products getContainingBundle(String lidvid, @Valid Integer start, @Valid Integer limit,
			@Valid List<String> fields, @Valid List<String> sort, @Valid Boolean summaryOnly) throws IOException
	{
		ProductBusinessObject productBO = new ProductBusinessObject(this.esRegistryConnection);
    	
    	if (!lidvid.contains("::")) lidvid = productBO.getLatestLidVidFromLid(lidvid);
    	
		boolean haveMatches = false;
    	MyProductsApiController.log.info("find all bundles containing the collection lidvid: " + lidvid);
    	HashSet<String> uniqueProperties = new HashSet<String>();
    	Products products = new Products();
    	BoolQueryBuilder query = QueryBuilders.boolQuery();
    	SearchRequest request = new SearchRequest(this.esRegistryConnection.getRegistryIndex());
    	SearchResponse response;
    	SearchSourceBuilder builder = new SearchSourceBuilder();
    	Summary summary = new Summary();

    	if (sort == null) { sort = Arrays.asList(); }

    	summary.setStart(start);
    	summary.setLimit(limit);
    	summary.setSort(sort);
    	products.setSummary(summary);
    	for (SearchHit hit : this.getCollections(lidvid))
    	{
    		haveMatches = true;
    		query.should (QueryBuilders.matchQuery("ref_lid_collection", hit.getSourceAsMap().get("collection_lid")));
    	}
    	
    	if (haveMatches)
    	{
    		builder.query(query);
    		request.source(builder);
    		response = this.esRegistryConnection.getRestHighLevelClient().search(request,RequestOptions.DEFAULT);
    		
    		try {
	    		
	    		for (int i = start ; start < limit && i < response.getHits().getHits().length ; i++)
	    		{
	    			Map<String, Object> sourceAsMap = response.getHits().getAt(i).getSourceAsMap();
	    			Map<String, XMLMashallableProperyValue> filteredMapJsonProperties = ProductBusinessObject.getFilteredProperties(
	    					sourceAsMap, 
	    					fields, 
	    					new ArrayList<String>(Arrays.asList(ElasticSearchUtil.elasticPropertyToJsonProperty(EntitytProductWithBlob.BLOB_PROPERTY)))
	    					);
	
	    			uniqueProperties.addAll(filteredMapJsonProperties.keySet());
	
	    			if (!summaryOnly)
	    			{
	    				EntityProduct entityProduct = objectMapper.convertValue(sourceAsMap, EntityProduct.class);
	    				Product product = ElasticSearchUtil.ESentityProductToAPIProduct(entityProduct);
	    				product.setProperties((Map<String, PropertyArrayValues>)(Map<String, ?>)filteredMapJsonProperties);
	    				products.addDataItem(product);
	    			}
	    		}
    		} catch (UnsupportedElasticSearchProperty e) {
	    		log.error("this should never happen " + e.getMessage());
	    	}
    	}
    	summary.setProperties(new ArrayList<String>(uniqueProperties));
    	return products;
	}


	@Override
	public ResponseEntity<Products> collectionsContainingProduct(String lidvid, @Valid Integer start, @Valid Integer limit,
			@Valid List<String> fields, @Valid List<String> sort, @Valid Boolean summaryOnly) {
		String accept = this.request.getHeader("Accept");
		MyProductsApiController.log.info("accept value is " + accept);

		if ((accept != null 
				&& (accept.contains("application/json") 
						|| accept.contains("text/html")
		 				|| accept.contains("application/xml")
		 				|| accept.contains("application/pds4+xml")
		 				|| accept.contains("*/*")))
		 	|| (accept == null))
		{
			try
			{
		 		Products products = this.getContainingCollection(lidvid, start, limit, fields, sort, summaryOnly);		 		
		 		return new ResponseEntity<Products>(products, HttpStatus.OK);
			}
			catch (IOException e)
			{
				log.error("Couldn't serialize response for content type " + accept, e);
				return new ResponseEntity<Products>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		 }
		 else return new ResponseEntity<Products>(HttpStatus.NOT_IMPLEMENTED);
	}

	private SearchHits getCollections (String lidvid) throws IOException
	{
    	SearchRequest request = new SearchRequest(this.esRegistryConnection.getRegistryRefIndex());
    	SearchResponse response;
    	SearchSourceBuilder builder = new SearchSourceBuilder();

    	builder.query(QueryBuilders.matchQuery("product_lidvid", lidvid));
    	request.source(builder);
    	response = this.esRegistryConnection.getRestHighLevelClient().search(request,RequestOptions.DEFAULT);
    	log.info("number of hits: " + Integer.toString(response.getHits().getHits().length));
    	return response.getHits();
	}

	@SuppressWarnings("unchecked")
	private Products getContainingCollection(String lidvid, @Valid Integer start, @Valid Integer limit,
			@Valid List<String> fields, @Valid List<String> sort, @Valid Boolean summaryOnly) throws IOException
	{	
    	if (!lidvid.contains("::")) lidvid = this.productBO.getLatestLidVidFromLid(lidvid);
    	
    	MyProductsApiController.log.info("find all bundles containing the collection lidvid: " + lidvid);
    	HashSet<String> uniqueProperties = new HashSet<String>();
    	Products products = new Products();
    	SearchHits hits = this.getCollections(lidvid);
      	Summary summary = new Summary();

    	if (sort == null) { sort = Arrays.asList(); }

    	summary.setStart(start);
    	summary.setLimit(limit);
    	summary.setSort(sort);
    	products.setSummary(summary);
    	
    	try {
	    	
	    	for (int i = start ; start < limit && i < hits.getHits().length ; i++)
	    	{
	    		GetRequest request = new GetRequest(this.esRegistryConnection.getRegistryIndex(), (String)hits.getAt(i).getSourceAsMap().get("collection_lidvid"));
		        Map<String, Object> sourceAsMap = this.esRegistryConnection.getRestHighLevelClient().get(request, RequestOptions.DEFAULT).getSourceAsMap();
		        Map<String, XMLMashallableProperyValue> filteredMapJsonProperties = ProductBusinessObject.getFilteredProperties(
		        		sourceAsMap, 
		        		fields,
		        		new ArrayList<String>(Arrays.asList(ElasticSearchUtil.elasticPropertyToJsonProperty(EntitytProductWithBlob.BLOB_PROPERTY)))
		        		);
		        
		        uniqueProperties.addAll(filteredMapJsonProperties.keySet());
	
		        if (!summaryOnly) {
	    	        EntityProduct entityProduct = objectMapper.convertValue(sourceAsMap, EntityProduct.class);
	    	        Product product = ElasticSearchUtil.ESentityProductToAPIProduct(entityProduct);
	    	        product.setProperties((Map<String, PropertyArrayValues>)(Map<String, ?>)filteredMapJsonProperties);
	    	        products.addDataItem(product);
		        }
	    	}
    	} catch (UnsupportedElasticSearchProperty e) {
    		log.error("this should never happen " + e.getMessage());
    	}
    	summary.setProperties(new ArrayList<String>(uniqueProperties));
    	return products;
	}
}
