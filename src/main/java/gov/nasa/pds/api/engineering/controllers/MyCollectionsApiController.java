package gov.nasa.pds.api.engineering.controllers;


import gov.nasa.pds.api.base.CollectionsApi;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchHitIterator;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistrySearchRequestBuilder;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchUtil;
import gov.nasa.pds.model.Product;
import gov.nasa.pds.model.Products;
import gov.nasa.pds.model.Summary;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;

import org.elasticsearch.action.search.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

import gov.nasa.pds.api.engineering.elasticsearch.business.LidVidNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;



@Controller
public class MyCollectionsApiController extends MyProductsApiBareController implements CollectionsApi {

    private static final Logger log = LoggerFactory.getLogger(MyCollectionsApiController.class);
    
 
    public MyCollectionsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
    	super(objectMapper, request);
    	
		this.presetCriteria.put("product_class", "Product_Collection");
	
    }
    
    
    public ResponseEntity<Product> collectionsByLidvid(@ApiParam(value = "lidvid (urn)",required=true) @PathVariable("lidvid") String lidvid) {
    	return this.getProductResponseEntity(lidvid);
    }


    public ResponseEntity<Products> getCollection(
            @ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
            @ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue = "100") Integer limit,
            @ApiParam(value = "search query, complex query uses eq,ne,gt,ge,lt,le,(,),not,and,or. Properties are named as in 'properties' attributes, literals are strings between \" or numbers. Detailed query specification is available at https://bit.ly/393i1af") @Valid @RequestParam(value = "q", required = false) String q,
            @ApiParam(value = "keyword search query") @Valid @RequestParam(value = "keyword", required = false) String keyword,
            @ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
            @ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
            @ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue = "false") Boolean onlySummary)
    {
        return this.getProductsResponseEntity(q, keyword, start, limit, fields, sort, onlySummary);
    }    

    
    public ResponseEntity<Products> productsOfACollection(@ApiParam(value = "lidvid (urn)",required=true) @PathVariable("lidvid") String lidvid
    		,@ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start
    		,@ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit
    		,@ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields
    		,@ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort
    		,@ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue="false") Boolean onlySummary
    		) {
    	
    	 MyCollectionsApiController.log.info("Get productsOfACollection");
    	
    	 String accept = this.request.getHeader("Accept");
    	 MyCollectionsApiController.log.info("accept value is " + accept);
		 if ((accept != null 
		 		&& (accept.contains("application/json") 
		 				|| accept.contains("text/html")
		 				|| accept.contains("application/xml")
		 				|| accept.contains("application/pds4+xml")
		 				|| accept.contains("*/*")))
		 	|| (accept == null)) {
		 	
		 	try {
		    	
		 	
		 		Products products = this.getProductChildren(lidvid, start, limit, fields, sort, onlySummary);
		    	
		 		/* REMOVED since it breaks the result when only-smmary argument is set to true
		 		if (products.getData() == null || products.getData().size() == 0)
		 			return new ResponseEntity<Products>(products, HttpStatus.NOT_FOUND);
		 		else
		 		*/
		 		
		 		return new ResponseEntity<Products>(products, HttpStatus.OK);
		  } catch (LidVidNotFoundException e) {
			  log.error("Couldn't find the lidvid " + e.getMessage());
			  return new ResponseEntity<Products>(HttpStatus.NOT_FOUND);
			  
		  } catch (IOException e) {
		       log.error("Couldn't serialize response for content type " + accept, e);
		       return new ResponseEntity<Products>(HttpStatus.INTERNAL_SERVER_ERROR);
		  }
		     
		 }
		 else return new ResponseEntity<Products>(HttpStatus.NOT_IMPLEMENTED);
    }
    		
	
    
	private Products getProductChildren(String lidvid, int start, int limit, List<String> fields, List<String> sort, boolean onlySummary) throws IOException, LidVidNotFoundException
	{
    	lidvid = this.productBO.getLatestLidVidFromLid(lidvid);
    	MyCollectionsApiController.log.info("request collection lidvid, collections children: " + lidvid);

    	int iteration=0,wsize=0;
    	HashSet<String> uniqueProperties = new HashSet<String>();
    	List<String> productLidvids = new ArrayList<String>();
    	List<String> pageOfLidvids = new ArrayList<String>();
    	Products products = new Products();
      	Summary summary = new Summary();

    	if (sort == null) { sort = Arrays.asList(); }	

    	summary.setStart(start);
    	summary.setLimit(limit);
      	summary.setSort(sort);	
    	products.setSummary(summary);

    	for (final Map<String,Object> kvp : new ElasticSearchHitIterator(this.esRegistryConnection.getRestHighLevelClient(),
				ElasticSearchRegistrySearchRequestBuilder.getQueryFieldFromKVP("collection_lidvid", lidvid, "product_lidvid",
						this.esRegistryConnection.getRegistryRefIndex())))
		{
    		pageOfLidvids.clear();
    		wsize = 0;

    		if (kvp.get("product_lidvid") instanceof String)
			{ pageOfLidvids.add(this.productBO.getLatestLidVidFromLid(kvp.get("product_lidvid").toString())); }
			else
			{
				@SuppressWarnings("unchecked")
				List<String> clids = (List<String>)kvp.get("product_lidvid");

				// if we are working with data that we care about (between start and start + limit) then record them
				if (start <= iteration || start < iteration+clids.size()) {pageOfLidvids.addAll(clids); }
				// else just modify the counter to skip them without wasting CPU cycles processing them
				else { wsize = clids.size(); }
			}

    		// if any data from the pages then add them to the complete roster
			if (start <= iteration || start < iteration+pageOfLidvids.size())
			{ productLidvids.addAll(pageOfLidvids.subList(start <= iteration ? 0 : start-iteration, pageOfLidvids.size())); }

			// if the limit of data has been found then break out of the loop
			if (limit <= productLidvids.size()) { break; }
			// otherwise update all of hte indices for the next iteration
			else { iteration = iteration + pageOfLidvids.size() + wsize; }
		}

    	if (0 < productLidvids.size())
    	{
    		this.fillProductsFromLidvids(products, uniqueProperties,
    				productLidvids.subList(0, productLidvids.size() < limit ? productLidvids.size() : limit), fields, onlySummary);
    	}
    	else MyCollectionsApiController.log.warn("Did not find any products for collection lidvid: " + lidvid);

    	summary.setProperties(new ArrayList<String>(uniqueProperties));
    	return products;    	
    }


	@Override
	public ResponseEntity<Products> bundlesContainingCollection(String lidvid, @Valid Integer start, @Valid Integer limit,
			@Valid List<String> fields, @Valid List<String> sort, @Valid Boolean summaryOnly)
	{
		String accept = this.request.getHeader("Accept");
		MyCollectionsApiController.log.info("accept value is " + accept);

		if ((accept != null 
				&& (accept.contains("application/json") 
						|| accept.contains("text/html")
		 				|| accept.contains("application/xml")
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
			catch (LidVidNotFoundException e)
			{
				log.warn("Could not find lid(vid) in database: " + lidvid);
				return new ResponseEntity<Products>(HttpStatus.NOT_FOUND);
			}
		 }
		 else return new ResponseEntity<Products>(HttpStatus.NOT_IMPLEMENTED);
	}
    
	private Products getContainingBundle(String lidvid, int start, int limit, List<String> fields, List<String> sort, boolean summaryOnly) throws IOException,LidVidNotFoundException
    {
    		
    	lidvid = this.productBO.getLatestLidVidFromLid(lidvid);
    	MyCollectionsApiController.log.info("find all bundles containing the collection lidvid: " + lidvid);
    	MyCollectionsApiController.log.info("find all bundles containing the collection lid: " + lidvid.substring(0, lidvid.indexOf("::")));
    	HashSet<String> uniqueProperties = new HashSet<String>();
    	Products products = new Products();
    	SearchRequest request = ElasticSearchRegistrySearchRequestBuilder.getQueryFieldsFromKVP("ref_lid_collection",
    			lidvid.substring(0, lidvid.indexOf("::")), fields, this.esRegistryConnection.getRegistryIndex(), false);
      	Summary summary = new Summary();

    	if (sort == null) { sort = Arrays.asList(); }

    	summary.setStart(start);
    	summary.setLimit(limit);
    	summary.setSort(sort);
    	products.setSummary(summary);
    	request.source().size(limit);
    	request.source().from(start);
    	this.fillProductsFromParents(products, uniqueProperties, ElasticSearchUtil.collate(this.esRegistryConnection.getRestHighLevelClient(), request), summaryOnly);
	    summary.setProperties(new ArrayList<String>(uniqueProperties));
    	return products;
    }
}