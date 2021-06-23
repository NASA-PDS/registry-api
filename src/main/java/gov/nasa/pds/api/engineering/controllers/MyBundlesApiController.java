package gov.nasa.pds.api.engineering.controllers;


import gov.nasa.pds.api.base.BundlesApi;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistrySearchRequestBuilder;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchUtil;
import gov.nasa.pds.api.engineering.elasticsearch.business.ProductBusinessObject;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntityProduct;
import gov.nasa.pds.api.model.xml.ProductWithXmlLabel;
import gov.nasa.pds.api.model.xml.XMLMashallableProperyValue;
import gov.nasa.pds.model.Product;
import gov.nasa.pds.model.PropertyArrayValues;
import gov.nasa.pds.model.Products;
import gov.nasa.pds.model.Summary;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-02-16T16:35:42.434-08:00[America/Los_Angeles]")
@Controller
public class MyBundlesApiController extends MyProductsApiBareController implements BundlesApi {

    private static final Logger log = LoggerFactory.getLogger(MyBundlesApiController.class);


    @org.springframework.beans.factory.annotation.Autowired
    public MyBundlesApiController(ObjectMapper objectMapper, HttpServletRequest request) {
    	super(objectMapper, request);
    	
		this.presetCriteria.put("product_class", "Product_Bundle");
	
    }

    public ResponseEntity<Product> bundleByLidvid(@ApiParam(value = "lidvid (urn)",required=true) @PathVariable("lidvid") String lidvid
) {
    	return this.getProductResponseEntity(lidvid);
    }

    public ResponseEntity<Products> getBundles(@ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start
,@ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit
,@ApiParam(value = "search query, complex query uses eq,ne,gt,ge,lt,le,(,),not,and,or. Properties are named as in 'properties' attributes, literals are strings between \" or numbers. Detailed query specification is available at https://bit.ly/393i1af") @Valid @RequestParam(value = "q", required = false) String q
,@ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields
,@ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort
,@ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue="false") Boolean onlySummary
) {
    	return this.getProductsResponseEntity(q, start, limit, fields, sort, onlySummary);
    }
    
    
    public ResponseEntity<Products> collectionsOfABundle(@ApiParam(value = "lidvid (urn)",required=true) @PathVariable("lidvid") String lidvid
    		,@ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start
    		,@ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit
    		,@ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields
    		,@ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort
    		,@ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue="false") Boolean onlySummary

    		
    		) {
    		return this.getBundlesCollections(lidvid, start, limit, fields, sort, onlySummary);
    		           		    }

    @SuppressWarnings("unchecked")
	private Products getCollectionChildren(String lidvid, int start, int limit, List<String> fields, List<String> sort, boolean onlySummary) throws IOException
    {
		if (!lidvid.contains("::") && !lidvid.endsWith(":")) lidvid = this.productBO.getLatestLidVidFromLid(lidvid);
    	MyBundlesApiController.log.info("request bundle lidvid, collections children: " + lidvid);
       	
    	GetRequest getBundleRequest = new ElasticSearchRegistrySearchRequestBuilder().getGetProductRequest(lidvid);
        GetResponse getBundleResponse = null;
        
        RestHighLevelClient restHighLevelClient = this.esRegistryConnection.getRestHighLevelClient();
         
    	try {
			getBundleResponse = restHighLevelClient.get(getBundleRequest, 
					RequestOptions.DEFAULT);
			
	    	Products products = new Products();
	    	
	    	HashSet<String> uniqueProperties = new HashSet<String>();
	    	
	      	Summary summary = new Summary();
	    	
	    	summary.setStart(start);
	    	summary.setLimit(limit);
	    	
	    	if (sort == null) {
	    		sort = Arrays.asList();
	    	}	
	    	summary.setSort(sort);
	    	
	    	products.setSummary(summary);
	    	
	    	if (getBundleResponse.isExists()) {
	    		MyBundlesApiController.log.info("get response " + getBundleResponse.toString());
        		@SuppressWarnings("unchecked")
				List<String> collections = (List<String>) getBundleResponse.getSourceAsMap().get("ref_lid_collection");
        		String collectionLidVid;
        		int i=0;
        		for (String collectionLid : collections ) {
        			if ((i>=start) && (i<start+limit)) {
	        			collectionLidVid = this.productBO.getLatestLidVidFromLid(collectionLid);
	        			
	        			//this.productBO.getProduct(collectionLidVid);
	        			GetRequest getCollectionRequest = new ElasticSearchRegistrySearchRequestBuilder().getGetProductRequest(collectionLidVid);
	        			
	        			//GetRequest getCollectionRequest = new GetRequest(this.esRegistryConnection.getRegistryIndex(), 
	        			//		collectionLidVid);
	        			
	        			
	        	        GetResponse getCollectionResponse = null;
	        	        
	        	        getCollectionResponse = restHighLevelClient.get(getCollectionRequest, 
	                			RequestOptions.DEFAULT);
	                	
	    	        	if (getCollectionResponse.isExists()) {
	    	        		
	    	        		MyBundlesApiController.log.info("get response " + getCollectionResponse.toString());
	    	        		Map<String, Object> sourceAsMap = getCollectionResponse.getSourceAsMap();
	    	        		Map<String, XMLMashallableProperyValue> filteredMapJsonProperties = ProductBusinessObject.getFilteredProperties(sourceAsMap, fields, null);
	
	    	        		uniqueProperties.addAll(filteredMapJsonProperties.keySet());
	
	    	        		    	        	    
	    	     	        
	    	     	        if (!onlySummary) {
	    	     	        	EntityProduct entityCollection = objectMapper.convertValue(sourceAsMap, EntityProduct.class);
	    	     	        	Product collection = ElasticSearchUtil.ESentityProductToAPIProduct(entityCollection);
	    	     	        	collection.setProperties((Map<String, PropertyArrayValues>)(Map<String, ?>)filteredMapJsonProperties);
	    	         	        products.addDataItem(collection);
	    	     	        }
	    	        		
	    	        	
	    	        	}
	    	        	else {
	    	        		MyBundlesApiController.log.warn("Couldn't get collection child " + collectionLidVid + " of bundle " + lidvid + " in elasticSearch");
	    	        	}
        			}
        			i+=1; 
        			
        		}
        	
	    	}
	    	
	    	
	    	summary.setProperties(new ArrayList<String>(uniqueProperties));
	    	
	    	return products;	
			
			
		} catch (IOException e) {
			  MyBundlesApiController.log.error("Couldn't get bundle " + lidvid + " from elasticSearch", e);
              throw(e);
		}
    	
    }

    private ResponseEntity<Products> getBundlesCollections(String lidvid, int start, int limit, List<String> fields, List<String> sort, boolean onlySummary) {
		 String accept = this.request.getHeader("Accept");
		 MyBundlesApiController.log.info("accept value is " + accept);
		 if ((accept != null 
		 		&& (accept.contains("application/json") 
		 				|| accept.contains("text/html")
		 				|| accept.contains("application/xml")
		 				|| accept.contains("*/*")))
		 	|| (accept == null)) {
		 	
		 	try {
		    	
		 	
		 		Products products = this.getCollectionChildren(lidvid, start, limit, fields, sort, onlySummary);
		    	
		    	return new ResponseEntity<Products>(products, HttpStatus.OK);
		    	
		  } catch (IOException e) {
		       log.error("Couldn't serialize response for content type " + accept, e);
		       return new ResponseEntity<Products>(HttpStatus.INTERNAL_SERVER_ERROR);
		  }
		     
		 }
		 else return new ResponseEntity<Products>(HttpStatus.NOT_IMPLEMENTED);
    }

	@Override
	public ResponseEntity<Products> productsOfABundle(String lidvid, @Valid Integer start, @Valid Integer limit,
			@Valid List<String> fields, @Valid List<String> sort, @Valid Boolean onlySummary) {
		 String accept = this.request.getHeader("Accept");
		 MyBundlesApiController.log.info("accept value is " + accept);
		 if ((accept != null 
		 		&& (accept.contains("application/json") 
		 				|| accept.contains("text/html")
		 				|| accept.contains("application/xml")
		 				|| accept.contains("*/*")))
		 	|| (accept == null)) {
		 	
		 	try {
		    	
		 	
		 		Products products = this.getProductChildren(lidvid, start, limit, fields, sort, onlySummary);
		    	
		    	return new ResponseEntity<Products>(products, HttpStatus.OK);
		    	
		  } catch (IOException e) {
		       log.error("Couldn't serialize response for content type " + accept, e);
		       return new ResponseEntity<Products>(HttpStatus.INTERNAL_SERVER_ERROR);
		  }
		     
		 }
		 else return new ResponseEntity<Products>(HttpStatus.NOT_IMPLEMENTED);
	}

    @SuppressWarnings("unchecked")
	private Products getProductChildren(String lidvid, int start, int limit, List<String> fields, List<String> sort, boolean onlySummary) throws IOException
    {
    	
    	if (!lidvid.contains("::")) lidvid = this.productBO.getLatestLidVidFromLid(lidvid);
    	
    	MyBundlesApiController.log.info("request bundle lidvid, children of products: " + lidvid);
    	GetRequest getBundleRequest = new ElasticSearchRegistrySearchRequestBuilder().getGetProductRequest(lidvid);
    	HashSet<String> uniqueProperties = new HashSet<String>();
    	List<String> productLidvids = new ArrayList<String>();
    	Products products = new Products();
        RestHighLevelClient restHighLevelClient = this.esRegistryConnection.getRestHighLevelClient();
      	Summary summary = new Summary();

    	if (sort == null) { sort = Arrays.asList(); }

    	summary.setStart(start);
    	summary.setLimit(limit);
    	summary.setSort(sort);
    	products.setSummary(summary);

    	try
    	{
    		GetResponse getBundleResponse = restHighLevelClient.get(getBundleRequest, RequestOptions.DEFAULT);
    		
    		
	    	if (getBundleResponse.isExists())
	    	{
	    		MyBundlesApiController.log.info("get response " + getBundleResponse.toString());
				List<String> collections = (List<String>) getBundleResponse.getSourceAsMap().get("ref_lid_collection");
        		String collectionLidVid;

        		for (String collectionLid : collections )
        		{
        			
        			collectionLidVid = productBO.getLatestLidVidFromLid(lidvid) + "::P1";

        			// TODO change the request to get collections from their lidvid and not from the ID which is not right, we are missing packets 
        			MyBundlesApiController.log.info("get collection with lidvid " + collectionLidVid);
        			GetRequest getCollectionRequest = new GetRequest(this.esRegistryConnection.getRegistryRefIndex(), collectionLidVid);
        			GetResponse getCollectionResponse = restHighLevelClient.get(getCollectionRequest, RequestOptions.DEFAULT);

        			if (getCollectionResponse.isExists())
        			{
        				MyBundlesApiController.log.info("get ref response " + getCollectionResponse.toString());
        				Object temp = getCollectionResponse.getSourceAsMap().get("product_lidvid");
        				if (temp instanceof String) { productLidvids.add((String)temp); }
        				else { productLidvids.addAll((List<String>) temp); }
        			}
        			else
        			{
        				MyBundlesApiController.log.warn("Couldn't get collection child " + collectionLidVid + " of bundle " + lidvid + " in elasticSearch");
        			}
        		}
        		MyBundlesApiController.log.info("total number of product lidvids " + Integer.toString(productLidvids.size()));
        		for (int i=start ;  i < start+limit && i < productLidvids.size() ;  i++)
        		{
        			MyBundlesApiController.log.info("fetch product from lidvid " + productLidvids.get(i));
        			GetRequest getProductRequest = new GetRequest(this.esRegistryConnection.getRegistryIndex(), productLidvids.get(i));
        			GetResponse getProductResponse = restHighLevelClient.get(getProductRequest, RequestOptions.DEFAULT);
        			
        			if (getProductResponse.isExists())
        			{
        				Map<String, Object> sourceAsMap = getProductResponse.getSourceAsMap();
        				Map<String, XMLMashallableProperyValue> filteredMapJsonProperties = ProductBusinessObject.getFilteredProperties(sourceAsMap, fields, null);

        				uniqueProperties.addAll(filteredMapJsonProperties.keySet());

        				if (!onlySummary)
        				{
        					EntityProduct entityProduct = objectMapper.convertValue(sourceAsMap, EntityProduct.class);
        					Product product = ElasticSearchUtil.ESentityProductToAPIProduct(entityProduct);
        					product.setProperties((Map<String, PropertyArrayValues>)(Map<String, ?>)filteredMapJsonProperties);
        					products.addDataItem(product);
        				}
        			}
        			else
        			{
        				MyBundlesApiController.log.warn("Couldn't get product child " + productLidvids.get(i) + " of bundle " + lidvid + " in elasticSearch");
        			}
        		}
	    	}
		}
    	catch (IOException e)
    	{
    		MyBundlesApiController.log.error("Couldn't get bundle " + lidvid + " from elasticSearch", e);
    		throw(e);
		}

    	summary.setProperties(new ArrayList<String>(uniqueProperties));
    	return products;	
    }
}
