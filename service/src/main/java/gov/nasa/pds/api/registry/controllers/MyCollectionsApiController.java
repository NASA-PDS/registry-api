package gov.nasa.pds.api.registry.controllers;


import gov.nasa.pds.api.base.CollectionsApi;
import gov.nasa.pds.api.registry.business.ErrorFactory;
import gov.nasa.pds.api.registry.business.LidVidNotFoundException;
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



@Controller
public class MyCollectionsApiController extends MyProductsApiBareController implements CollectionsApi {

    private static final Logger log = LoggerFactory.getLogger(MyCollectionsApiController.class);
    
 
    public MyCollectionsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        super(objectMapper, request);
        
        this.presetCriteria.put("product_class", "Product_Collection");
    
    }
    
    
    public ResponseEntity<Object> collectionsByLidvid(
            @ApiParam(value = "lidvid or lid", required = true) @PathVariable("identifier") String lidvid)
    {
        return this.getLatestProductResponseEntity(lidvid);
    }

    @Override
    public ResponseEntity<Object> collectionsByLidvidLatest(
            @ApiParam(value = "lidvid or lid", required = true) @PathVariable("identifier") String lidvid)
    {
        return this.getLatestProductResponseEntity(lidvid);
    }
    
    
    public ResponseEntity<Object> collectionsByLidvidAll(
            @ApiParam(value = "lidvid or lid", required = true) @PathVariable("identifier") String lidvid,
            @ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
            @ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "10") @Valid @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit)
    {
        return getAllProductsResponseEntity(lidvid, start, limit);                
    }

    
    public ResponseEntity<Object> getCollection(
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

    
    public ResponseEntity<Object> productsOfACollection(
            @ApiParam(value = "lidvid or lid", required = true) @PathVariable("identifier") String identifier,
            @ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
            @ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue = "100") Integer limit,
            @ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
            @ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
            @ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue = "false") Boolean onlySummary)
    {
        return getProductsOfACollectionResponseEntity(identifier, start, limit, fields, sort, onlySummary);
    }

    
    public ResponseEntity<Object> productsOfACollectionLatest(
            @ApiParam(value = "lidvid or lid", required = true) @PathVariable("identifier") String identifier,
            @ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
            @ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue = "100") Integer limit,
            @ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
            @ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
            @ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue = "false") Boolean onlySummary)
    {
        return getProductsOfACollectionResponseEntity(identifier, start, limit, fields, sort, onlySummary);
    }

    
    public ResponseEntity<Object> productsOfACollectionAll(
            @ApiParam(value = "lidvid or lid", required = true) @PathVariable("identifier") String identifier,
            @ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
            @ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue = "100") Integer limit,
            @ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
            @ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
            @ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue = "false") Boolean onlySummary)
    {
        return getProductsOfACollectionResponseEntity(identifier, start, limit, fields, sort, onlySummary);
    }    
    
    
    protected ResponseEntity<Object> getProductsOfACollectionResponseEntity(String lidvid, int start, int limit, 
            List<String> fields, List<String> sort, boolean onlySummary)
    {
        String accept = this.request.getHeader("Accept");
        MyCollectionsApiController.log.info("Get productsOfACollection");

        try
        {
        	RequestAndResponseContext context = RequestAndResponseContext.buildRequestAndResponseContext(this.objectMapper, this.getBaseURL(), lidvid, start, limit, fields, sort, onlySummary, this.presetCriteria, accept);
        	this.getProductChildren(context);
       	 	return new ResponseEntity<Object>(context.getResponse(), HttpStatus.OK);
        }
        catch (ApplicationTypeException e)
        {
        	log.error("Application type not implemented", e);
       	 	return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_ACCEPTABLE);
        }
        catch (LidVidNotFoundException e)
        {
            log.error("Couldn't find the lidvid " + e.getMessage());
            return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);

        }
        catch (IOException e)
        {
            log.error("Couldn't serialize response for content type " + accept, e);
            return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (NothingFoundException e)
        {
        	log.warn("Could not find any matching reference(s) in database.");
        	return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
        }
    }
    
    
    private void getProductChildren(RequestAndResponseContext context) throws IOException, LidVidNotFoundException
    {
        String lidvid = this.productBO.getLatestLidVidFromLid(context.getLIDVID());
    
        MyCollectionsApiController.log.info("request collection lidvid, collections children: " + lidvid);

        int iteration=0,wsize=0;
        List<String> productLidvids = new ArrayList<String>();
        List<String> pageOfLidvids = new ArrayList<String>();

        KVPQueryBuilder bld = new KVPQueryBuilder(esRegistryConnection.getRegistryRefIndex());
        bld.setKVP("collection_lidvid", lidvid);
        bld.setFields("product_lidvid");
        SearchRequest request = bld.buildTermQuery();

        HitIterator itr = new HitIterator(esRegistryConnection.getRestHighLevelClient(), request);
        
        for (final Map<String,Object> kvp : itr)
        {
            pageOfLidvids.clear();
            wsize = 0;

            if (kvp.get("product_lidvid") instanceof String)
            { 
                pageOfLidvids.add(this.productBO.getLatestLidVidFromLid(kvp.get("product_lidvid").toString())); 
            }
            else
            {
                @SuppressWarnings("unchecked")
                List<String> clids = (List<String>)kvp.get("product_lidvid");

                // if we are working with data that we care about (between start and start + limit) then record them
                if (context.getStart() <= iteration || context.getStart() < iteration+clids.size()) {pageOfLidvids.addAll(clids); }
                // else just modify the counter to skip them without wasting CPU cycles processing them
                else { wsize = clids.size(); }
            }

            // if any data from the pages then add them to the complete roster
            if (context.getStart() <= iteration || context.getStart() < iteration+pageOfLidvids.size())
            {
                int fromIndex = context.getStart() <= iteration ? 0 : context.getStart() - iteration;
                List<String> subList = pageOfLidvids.subList(fromIndex, pageOfLidvids.size());
                productLidvids.addAll(subList); 
            }

            // if the limit of data has been found then break out of the loop
            //if (limit <= productLidvids.size()) { break; }
            // otherwise update all of hte indices for the next iteration
            //else { iteration = iteration + pageOfLidvids.size() + wsize; }
            iteration = iteration + pageOfLidvids.size() + wsize;
        }

        if (productLidvids.size() > 0 && context.getLimit() > 0)
        {
            int toIndex = productLidvids.size() < context.getLimit() ? productLidvids.size() : context.getLimit();
            List<String> subList = productLidvids.subList(0, toIndex);
            this.fillProductsFromLidvids(context, subList, iteration);
        }
        else 
        {
            MyCollectionsApiController.log.warn("Did not find any products for collection lidvid: " + lidvid);
        }
    }


    @Override
    public ResponseEntity<Object> bundlesContainingCollection(String lidvid, @Valid Integer start, @Valid Integer limit,
            @Valid List<String> fields, @Valid List<String> sort, @Valid Boolean summaryOnly)
    {
        String accept = this.request.getHeader("Accept");
        MyCollectionsApiController.log.info("accept value is " + accept);

        try
        {
        	RequestAndResponseContext context = RequestAndResponseContext.buildRequestAndResponseContext(this.objectMapper, this.getBaseURL(), lidvid, start, limit, fields, sort, summaryOnly, this.presetCriteria, accept);
       	 	this.getContainingBundle(context);
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
    
    private void getContainingBundle(RequestAndResponseContext context) throws IOException,LidVidNotFoundException
    {
        String id = context.getLIDVID();
        if(id == null) return;

        log.info("Find all bundles containing collection ID: " + id);

        int idx = id.indexOf("::");
        String lid = idx > 0 ? id.substring(0, idx) : id;
        
        KVPQueryBuilder bld = new KVPQueryBuilder(esRegistryConnection.getRegistryIndex());
        bld.setFilterByArchiveStatus(true);
        
        bld.setKVP("ref_lid_collection", lid);
        bld.setFields(context.getFields());
        SearchRequest request = bld.buildTermQuery();

        context.setResponse(this.esRegistryConnection.getRestHighLevelClient(), request);
    }
}
