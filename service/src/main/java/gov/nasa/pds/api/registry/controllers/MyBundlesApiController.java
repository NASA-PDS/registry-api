package gov.nasa.pds.api.registry.controllers;


import gov.nasa.pds.api.base.BundlesApi;
import gov.nasa.pds.api.registry.business.ErrorFactory;
import gov.nasa.pds.api.registry.business.LidVidNotFoundException;
import gov.nasa.pds.api.registry.business.ProductVersionSelector;
import gov.nasa.pds.api.registry.business.RequestAndResponseContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.NothingFoundException;
import gov.nasa.pds.api.registry.search.HitIterator;
import gov.nasa.pds.api.registry.search.KVPQueryBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;

import org.opensearch.action.search.SearchRequest;
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
    public ResponseEntity<Object> bundleByLidvid(
            @ApiParam(value = "lidvid or lid", required = true) @PathVariable("identifier") String lidvid)
    {
        return this.getLatestProductResponseEntity(lidvid);
    }

    
    @Override
    public ResponseEntity<Object> bundleByLidvidLatest(
            @ApiParam(value = "lidvid or lid", required = true) @PathVariable("identifier") String lidvid)
    {
        return this.getLatestProductResponseEntity(lidvid);
    }

    
    @Override    
    public ResponseEntity<Object> bundleByLidvidAll(
            @ApiParam(value = "lidvid or lid", required = true) @PathVariable("identifier") String lidvid,
            @ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
            @ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "10") @Valid @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit)
    {
        return this.getAllProductsResponseEntity(lidvid, start, limit);                
    }    
    
    
    public ResponseEntity<Object> getBundles(
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
    
    
    public ResponseEntity<Object> collectionsOfABundle(
            @ApiParam(value = "lidvid or lid", required = true) @PathVariable("identifier") String lidvid,
            @ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
            @ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue = "100") Integer limit,
            @ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
            @ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
            @ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue = "false") Boolean onlySummary)
    {
        return getBundlesCollectionsEntity(lidvid, start, limit, fields, sort, onlySummary, ProductVersionSelector.LATEST);
    }
    
    
    public ResponseEntity<Object> collectionsOfABundleAll(
            @ApiParam(value = "lidvid or lid", required = true) @PathVariable("identifier") String lidvid,
            @ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
            @ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue = "100") Integer limit,
            @ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
            @ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
            @ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue = "false") Boolean onlySummary)
    {
        return getBundlesCollectionsEntity(lidvid, start, limit, fields, sort, onlySummary, ProductVersionSelector.ALL);
    }    
    
    
    public ResponseEntity<Object> collectionsOfABundleLatest(
            @ApiParam(value = "lidvid or lid", required = true) @PathVariable("identifier") String lidvid,
            @ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
            @ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue = "100") Integer limit,
            @ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
            @ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
            @ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue = "false") Boolean onlySummary)
    {
        return getBundlesCollectionsEntity(lidvid, start, limit, fields, sort, onlySummary, ProductVersionSelector.LATEST);
    }
    
    
    private void getBundleCollections(RequestAndResponseContext context) 
                    throws IOException, LidVidNotFoundException
    {
        String lidvid = productBO.getLidVidDao().getLatestLidVidByLid(context.getLIDVID());
        MyBundlesApiController.log.info("Get bundle's collections. Bundle LIDVID = " + lidvid);
        
        List<String> clidvids = null;
        if(context.getSelector() == ProductVersionSelector.ALL)
        {
            clidvids = productBO.getBundleDao().getAllBundleCollectionLidVids(lidvid);
        }
        else
        {
            clidvids = productBO.getBundleDao().getBundleCollectionLidVids(lidvid);
        }

        int size = clidvids.size();
        if (size > 0 && context.getStart() < size && context.getLimit() > 0)
        {
            int end = context.getStart() + context.getLimit();
            if(end > size) end = size;
            List<String> ids = clidvids.subList(context.getStart(), end);
            fillProductsFromLidvids(context, ids, -1);
        }
        else 
        {
            log.warn("Did not find any collections for bundle lidvid: " + lidvid);
        }
    }

    
    private ResponseEntity<Object> getBundlesCollectionsEntity(String lidvid, int start, int limit, 
            List<String> fields, List<String> sort, boolean onlySummary, ProductVersionSelector versionSelector)
    {
         String accept = this.request.getHeader("Accept");
         MyBundlesApiController.log.info("accept value is " + accept);

         try
         {
        	 RequestAndResponseContext context = RequestAndResponseContext.buildRequestAndResponseContext(this.objectMapper, this.getBaseURL(), lidvid, start, limit, fields, sort, onlySummary, versionSelector, this.presetCriteria, accept);
        	 this.getBundleCollections(context);
        	 return new ResponseEntity<Object>(context.getResponse(), HttpStatus.OK);
         }
         catch (ApplicationTypeException e)
         {
        	 log.error("Application type not implemented", e);
        	 return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_ACCEPTABLE);
         }
         catch (IOException e)
         {
        	 log.error("Couldn't serialize response for content type " + accept, e);
        	 return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.INTERNAL_SERVER_ERROR);
         }
         catch (LidVidNotFoundException e)
         {
        	 log.warn("Could not find lid(vid) in database: " + lidvid);
        	 return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
         }
         catch (NothingFoundException e)
         {
        	 log.warn("Could not find any matching reference(s) in database.");
        	 return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
         }
    }

    
    @Override
    public ResponseEntity<Object> productsOfABundle(String lidvid, @Valid Integer start, @Valid Integer limit,
            @Valid List<String> fields, @Valid List<String> sort, @Valid Boolean onlySummary)
    {
         String accept = this.request.getHeader("Accept");
         MyBundlesApiController.log.info("accept value is " + accept);

         try
         {
        	 RequestAndResponseContext context = RequestAndResponseContext.buildRequestAndResponseContext(
        	         this.objectMapper, this.getBaseURL(), lidvid, start, limit, fields, sort, false, this.presetCriteria, accept);
             this.getProductChildren(context);
        	 return new ResponseEntity<Object>(context.getResponse(), HttpStatus.OK);
         }
         catch (ApplicationTypeException e)
         {
        	 log.error("Application type not implemented", e);
        	 return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_ACCEPTABLE);
         }
         catch (IOException e)
         {
        	 log.error("Couldn't serialize response for content type " + accept, e);
        	 return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.INTERNAL_SERVER_ERROR);
         }
         catch (LidVidNotFoundException e)
         {
        	 log.warn("Could not find lid(vid) in database: " + lidvid);
        	 return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
         }
         catch (NothingFoundException e)
         {
        	 log.warn("Could not find any matching reference(s) in database.");
        	 return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
         }
    }

    
    private void getProductChildren(RequestAndResponseContext context) throws IOException,LidVidNotFoundException
    {
        String lidvid = this.productBO.getLatestLidVidFromLid(context.getLIDVID());
        MyBundlesApiController.log.info("request bundle lidvid, children of products: " + lidvid);

        int iteration=0,wsize=0;
        List<String> clidvids = productBO.getBundleDao().getBundleCollectionLidVids(lidvid);
        
        List<String> plidvids = new ArrayList<String>();   
        List<String> wlidvids = new ArrayList<String>();

        if (0 < clidvids.size())
        {
            KVPQueryBuilder bld = new KVPQueryBuilder(esRegistryConnection.getRegistryRefIndex());
            bld.setKVP("collection_lidvid", clidvids);
            bld.setFields("product_lidvid");
            SearchRequest req = bld.buildTermQuery();
            
            HitIterator itr = new HitIterator(esRegistryConnection.getRestHighLevelClient(), req);
            
            for(final Map<String,Object> hit : itr)
            {
                wlidvids.clear();
                wsize = 0;

                if (hit.get("product_lidvid") instanceof String)
                { wlidvids.add(this.productBO.getLatestLidVidFromLid(hit.get("product_lidvid").toString())); }
                else
                {
                    @SuppressWarnings("unchecked")
                    List<String> plids = (List<String>)hit.get("product_lidvid");

                    if (context.getStart() <= iteration || context.getStart() < iteration+plids.size()) { wlidvids.addAll(plids); }
                    else { wsize = plids.size(); } 
                }

                if (context.getStart() <= iteration || context.getStart() < iteration+wlidvids.size())
                { plidvids.addAll(wlidvids.subList(context.getStart() <= iteration ? 0 : context.getStart()-iteration, wlidvids.size())); }

                //if (limit <= plidvids.size()) { break; }
                //else { iteration = iteration + wlidvids.size() + wsize; }
                iteration = iteration + wlidvids.size() + wsize;
            }
        }
        else MyBundlesApiController.log.warn ("Did not find any collections for bundle lidvid: " + lidvid);
        
        MyBundlesApiController.log.info("found " + Integer.toString(plidvids.size()) + " products in this bundle");

        if (plidvids.size() > 0 && context.getLimit() > 0)
        {
            this.fillProductsFromLidvids(context,
                    plidvids.subList(0, plidvids.size() < context.getLimit() ? plidvids.size() : context.getLimit()), iteration);
        }
        else MyBundlesApiController.log.warn ("Did not find any products for bundle lidvid: " + lidvid);
    }
}
