package gov.nasa.pds.api.engineering.controllers;


import gov.nasa.pds.api.base.BundlesApi;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchHitIterator;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistrySearchRequestBuilder;
import gov.nasa.pds.api.engineering.elasticsearch.business.LidVidNotFoundException;
import gov.nasa.pds.model.Product;
import gov.nasa.pds.model.Products;
import gov.nasa.pds.model.Summary;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;

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

    @Override
    public ResponseEntity<Product> bundleByLidvid(@ApiParam(value = "lidvid (urn)",required=true) @PathVariable("lidvid") String lidvid)
    {
    	return this.getProductResponseEntity(lidvid);
    }

    public ResponseEntity<Products> getBundles(
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
    
    
    public ResponseEntity<Products> collectionsOfABundle(@ApiParam(value = "lidvid (urn)",required=true) @PathVariable("lidvid") String lidvid
    		,@ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start
    		,@ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit
    		,@ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields
    		,@ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort
    		,@ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue="false") Boolean onlySummary
    		)
    {
    		return this.getBundlesCollections(lidvid, start, limit, fields, sort, onlySummary);
    }

 	private List<String> getRefLidCollection (String lidvid) throws IOException,LidVidNotFoundException
    {
 		List<String> reflids = new ArrayList<String>();

 		for (final Map<String, Object> bundle : new ElasticSearchHitIterator(this.esRegistryConnection.getRestHighLevelClient(),
				ElasticSearchRegistrySearchRequestBuilder.getQueryFieldFromLidvid(lidvid, "ref_lid_collection",
						this.esRegistryConnection.getRegistryIndex())))
 		{
 			if (bundle.get("ref_lid_collection") instanceof String)
 			{ reflids.add(this.productBO.getLatestLidVidFromLid(bundle.get("ref_lid_collection").toString())); }
 			else
 			{
 				@SuppressWarnings("unchecked")
 				List<String> clids = (List<String>)bundle.get("ref_lid_collection");
 				for (String clid : clids)
 				{ reflids.add(this.productBO.getLatestLidVidFromLid(clid)); }
 			}
 		}
 		return reflids;
    }
    
    private Products getCollectionChildren(String lidvid, int start, int limit, List<String> fields, List<String> sort, boolean onlySummary) throws IOException,LidVidNotFoundException
    {
    	long begin = System.currentTimeMillis();
	lidvid = this.productBO.getLatestLidVidFromLid(lidvid);
    	MyBundlesApiController.log.info("request bundle lidvid, collections children: " + lidvid);

    	HashSet<String> uniqueProperties = new HashSet<String>();
    	List<String> clidvids = this.getRefLidCollection(lidvid);
    	Products products = new Products();
      	Summary summary = new Summary();

    	if (sort == null) { sort = Arrays.asList(); }

    	summary.setHits(clidvids.size());
    	summary.setLimit(limit);
    	summary.setSort(sort);
    	summary.setStart(start);
    	summary.setTook(-1);
    	products.setSummary(summary);

    	if (0 < clidvids.size())
    	{
    		this.fillProductsFromLidvids(products, uniqueProperties,
    				clidvids.subList(start, clidvids.size() < start+limit ? clidvids.size(): start+limit),
    				fields, onlySummary);
    	}
    	else MyBundlesApiController.log.warn ("Did not find any collections for bundle lidvid: " + lidvid);

    	summary.setProperties(new ArrayList<String>(uniqueProperties));
    	summary.setTook((int)(System.currentTimeMillis() - begin));
    	return products;	
    }

    private ResponseEntity<Products> getBundlesCollections(String lidvid, int start, int limit, List<String> fields, List<String> sort, boolean onlySummary)
    {
		 String accept = this.request.getHeader("Accept");
		 MyBundlesApiController.log.info("accept value is " + accept);
		 if ((accept != null 
		 		&& (accept.contains("application/json") 
		 				|| accept.contains("text/html")
		 				|| accept.contains("application/xml")
		 				|| accept.contains("*/*")))
		 	|| (accept == null))
		 {
			 try
			 {
				 Products products = this.getCollectionChildren(lidvid, start, limit, fields, sort, onlySummary);
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

	@Override
	public ResponseEntity<Products> productsOfABundle(String lidvid, @Valid Integer start, @Valid Integer limit,
			@Valid List<String> fields, @Valid List<String> sort, @Valid Boolean onlySummary)
	{
		 String accept = this.request.getHeader("Accept");
		 MyBundlesApiController.log.info("accept value is " + accept);
		 if ((accept != null 
		 		&& (accept.contains("application/json") 
		 				|| accept.contains("text/html")
		 				|| accept.contains("application/xml")
		 				|| accept.contains("*/*")))
		 	|| (accept == null))
		 {
			 try
			 {
				 Products products = this.getProductChildren(lidvid, start, limit, fields, sort, onlySummary);
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

	private Products getProductChildren(String lidvid, int start, int limit, List<String> fields, List<String> sort, boolean onlySummary) throws IOException,LidVidNotFoundException
    {
  	long begin = System.currentTimeMillis();
    	lidvid = this.productBO.getLatestLidVidFromLid(lidvid);
    	MyBundlesApiController.log.info("request bundle lidvid, children of products: " + lidvid);

    	int iteration=0,wsize=0;
    	HashSet<String> uniqueProperties = new HashSet<String>();
    	List<String> clidvids = this.getRefLidCollection(lidvid);
    	List<String> plidvids = new ArrayList<String>();   
    	List<String> wlidvids = new ArrayList<String>();
    	Products products = new Products();
      	Summary summary = new Summary();

    	if (sort == null) { sort = Arrays.asList(); }

    	summary.setHits(-1);
    	summary.setLimit(limit);
    	summary.setSort(sort);
    	summary.setStart(start);
    	summary.setTook(-1);
    	products.setSummary(summary);

    	if (0 < clidvids.size())
    	{
    		for (final Map<String,Object> hit : new ElasticSearchHitIterator(this.esRegistryConnection.getRestHighLevelClient(),
    				ElasticSearchRegistrySearchRequestBuilder.getQueryFieldFromKVP("collection_lidvid", clidvids, "product_lidvid",
    						this.esRegistryConnection.getRegistryRefIndex())))
    		{
    			wlidvids.clear();
    			wsize = 0;

    			if (hit.get("product_lidvid") instanceof String)
    			{ wlidvids.add(this.productBO.getLatestLidVidFromLid(hit.get("product_lidvid").toString())); }
    			else
    			{
    				@SuppressWarnings("unchecked")
    				List<String> plids = (List<String>)hit.get("product_lidvid");

    				if (start <= iteration || start < iteration+plids.size()) { wlidvids.addAll(plids); }
    				else { wsize = plids.size(); } 
    			}

    			if (start <= iteration || start < iteration+wlidvids.size())
    			{ plidvids.addAll(wlidvids.subList(start <= iteration ? 0 : start-iteration, wlidvids.size())); }

    			//if (limit <= plidvids.size()) { break; }
    			//else { iteration = iteration + wlidvids.size() + wsize; }
    			iteration = iteration + wlidvids.size() + wsize;
    		}
    	}
    	else MyBundlesApiController.log.warn ("Did not find any collections for bundle lidvid: " + lidvid);
    	
    	MyBundlesApiController.log.info("found " + Integer.toString(plidvids.size()) + " products in this bundle");

    	if (0 < plidvids.size())
    	{
    		this.fillProductsFromLidvids(products, uniqueProperties,
    				plidvids.subList(0, plidvids.size() < limit ? plidvids.size() : limit), fields, onlySummary);
    	}
    	else MyBundlesApiController.log.warn ("Did not find any products for bundle lidvid: " + lidvid);

    	summary.setHits(iteration);
    	summary.setProperties(new ArrayList<String>(uniqueProperties));
    	summary.setTook((int)(System.currentTimeMillis() - begin));
    	return products;	
    }
}
