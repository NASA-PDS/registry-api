package gov.nasa.pds.api.engineering.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.elasticsearch.action.search.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.base.ProductsApi;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchHitIterator;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistrySearchRequestBuilder;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchUtil;
import gov.nasa.pds.model.Product;
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


	private Products getContainingBundle(String lidvid, @Valid Integer start, @Valid Integer limit,
			@Valid List<String> fields, @Valid List<String> sort, @Valid Boolean summaryOnly) throws IOException
	{    	
    	if (!lidvid.contains("::")) lidvid = productBO.getLatestLidVidFromLid(lidvid);
       	MyProductsApiController.log.info("find all bundles containing the product lidvid: " + lidvid);

       	HashSet<String> uniqueProperties = new HashSet<String>();
       	List<String> collectionLIDs = this.getCollectionLidvids(lidvid, true);
    	Products products = new Products();
    	Summary summary = new Summary();

    	if (sort == null) { sort = Arrays.asList(); }

    	summary.setStart(start);
    	summary.setLimit(limit);
    	summary.setSort(sort);
    	products.setSummary(summary);

    	if (0 < collectionLIDs.size())
    	{
    		SearchRequest request = ElasticSearchRegistrySearchRequestBuilder.getQueryFieldsFromKVP
    				("ref_lid_collection", collectionLIDs, fields, this.esRegistryConnection.getRegistryIndex(), false);
    		
    		request.source().from(start);
    		request.source().size(limit);
    		this.fillProductsFromParents(products, uniqueProperties,
    				ElasticSearchUtil.collate(this.esRegistryConnection.getRestHighLevelClient(), request),
    				summaryOnly);
    	}
    	else MyProductsApiController.log.warn ("No parent collection for product LIDVID: " + lidvid);

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

	private List<String> getCollectionLidvids (String lidvid, boolean noVer) throws IOException
	{
		List<String> fields = new ArrayList<String>(), lidvids = new ArrayList<String>();
		String field = noVer ? "collection_lid" : "collection_lidvid";

		fields.add(field);
    	for (final Map<String,Object> kvp : new ElasticSearchHitIterator(this.esRegistryConnection.getRestHighLevelClient(),
    			ElasticSearchRegistrySearchRequestBuilder.getQueryFieldsFromKVP("product_lidvid",
    					lidvid, fields, this.esRegistryConnection.getRegistryRefIndex(), false)))
    	{
			if (kvp.get(field) instanceof String)
			{ lidvids.add(kvp.get(field).toString()); }
			else
			{
				@SuppressWarnings("unchecked")
				List<String> clids = (List<String>)kvp.get(field);
				for (String clid : clids) { lidvids.add(clid); }
			}
    	}
    	return lidvids;
	}

	private Products getContainingCollection(String lidvid, @Valid Integer start, @Valid Integer limit,
			@Valid List<String> fields, @Valid List<String> sort, @Valid Boolean summaryOnly) throws IOException
	{	
    	if (!lidvid.contains("::")) lidvid = this.productBO.getLatestLidVidFromLid(lidvid);
       	MyProductsApiController.log.info("find all bundles containing the product lidvid: " + lidvid);

       	HashSet<String> uniqueProperties = new HashSet<String>();
       	List<String> collectionLidvids = this.getCollectionLidvids(lidvid, false);
    	Products products = new Products();
      	Summary summary = new Summary();

    	if (sort == null) { sort = Arrays.asList(); }

    	summary.setStart(start);
    	summary.setLimit(limit);
    	summary.setSort(sort);
    	products.setSummary(summary);
    	
    	if (0 < collectionLidvids.size())
    	{ this.fillProductsFromLidvids(products, uniqueProperties,
    			collectionLidvids.subList(start, collectionLidvids.size() < start+limit ? collectionLidvids.size() : +limit), fields,
    			summaryOnly); }
    	else MyProductsApiController.log.warn("Did not find a product with lidvid: " + lidvid);

    	summary.setProperties(new ArrayList<String>(uniqueProperties));
    	return products;
	}
}
