package gov.nasa.pds.api.registry.controllers;

import java.io.IOException;
import java.util.ArrayList;
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
import gov.nasa.pds.api.registry.elasticsearch.ElasticSearchHitIterator;
import gov.nasa.pds.api.registry.elasticsearch.KVPQueryBuilder;
import gov.nasa.pds.api.registry.elasticsearch.business.ErrorFactory;
import gov.nasa.pds.api.registry.elasticsearch.business.LidVidNotFoundException;
import gov.nasa.pds.api.registry.elasticsearch.business.RequestAndResponseContext;
import gov.nasa.pds.api.registry.exceptions.ApplicationTypeException;
import gov.nasa.pds.api.registry.exceptions.NothingFoundException;
import io.swagger.annotations.ApiParam;


@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-10-29T11:01:11.991-07:00[America/Los_Angeles]")
@Controller
public class MyProductsApiController extends MyProductsApiBareController implements ProductsApi {

    private static final Logger log = LoggerFactory.getLogger(MyProductsApiController.class);
    
    @org.springframework.beans.factory.annotation.Autowired
    public MyProductsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        
        super(objectMapper, request);
    }
    
   
    public ResponseEntity<Object> products(
            @ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
            @ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue = "100") Integer limit,
            @ApiParam(value = "search query") @Valid @RequestParam(value = "q", required = false) String q,
            @ApiParam(value = "keyword search query") @Valid @RequestParam(value = "keyword", required = false) String keyword,
            @ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
            @ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
            @ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue = "false") Boolean onlySummary)
    {
        return this.getProductsResponseEntity(q, keyword, start, limit, fields, sort, onlySummary);
    }
    
     
    public ResponseEntity<Object> productsByLidvid(
            @ApiParam(value = "lidvid or lid", required = true) @PathVariable("identifier") String lidvid)
    {
        return this.getLatestProductResponseEntity(lidvid);
    }

    
    @Override
    public ResponseEntity<Object> productsByLidvidLatest(
            @ApiParam(value = "lidvid or lid", required = true) @PathVariable("identifier") String lidvid)
    {
        return this.getLatestProductResponseEntity(lidvid);
    }    
    
    
    @Override
    public ResponseEntity<Object> productsByLidvidAll(
            @ApiParam(value = "lidvid or lid", required = true) @PathVariable("identifier") String lidvid,
            @ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
            @ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "10") @Valid @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit)
    {
        return getAllProductsResponseEntity(lidvid, start, limit);
    }    
    
    
    @Override
    public ResponseEntity<Object> bundlesContainingProduct(String lidvid, @Valid Integer start, @Valid Integer limit,
            @Valid List<String> fields, @Valid List<String> sort, @Valid Boolean summaryOnly) {
        String accept = this.request.getHeader("Accept");
        MyProductsApiController.log.info("accept value is " + accept);

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
        String lidvid = productBO.getLatestLidVidFromLid(context.getLIDVID());
        MyProductsApiController.log.info("find all bundles containing the product lidvid: " + lidvid);

        List<String> collectionLIDs = this.getCollectionLidvids(lidvid, true);

        if (0 < collectionLIDs.size())
        {
            KVPQueryBuilder bld = new KVPQueryBuilder(esRegistryConnection.getRegistryIndex());
            bld.setKVP("ref_lid_collection", collectionLIDs);
            bld.setFields(context.getFields());
            SearchRequest request = bld.buildMatchQuery();
            
            context.setResponse(this.esRegistryConnection.getRestHighLevelClient(), request);
        }
        else 
        {
            MyProductsApiController.log.warn ("No parent collection for product LIDVID: " + lidvid);
        }
    }


    @Override
    public ResponseEntity<Object> collectionsContainingProduct(String lidvid, @Valid Integer start, @Valid Integer limit,
            @Valid List<String> fields, @Valid List<String> sort, @Valid Boolean summaryOnly) {
        String accept = this.request.getHeader("Accept");
        MyProductsApiController.log.info("accept value is " + accept);

        try
        {
        	RequestAndResponseContext context = RequestAndResponseContext.buildRequestAndResponseContext(this.objectMapper, this.getBaseURL(), lidvid, start, limit, fields, sort, summaryOnly, this.presetCriteria, accept);
            this.getContainingCollection(context);              
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

    
    private List<String> getCollectionLidvids (String lidvid, boolean noVer) throws IOException
    {
        List<String> fields = new ArrayList<String>(), lidvids = new ArrayList<String>();
        String field = noVer ? "collection_lid" : "collection_lidvid";
        fields.add(field);
        
        KVPQueryBuilder bld = new KVPQueryBuilder(esRegistryConnection.getRegistryRefIndex());
        bld.setKVP("product_lidvid", lidvid);
        bld.setFields(fields);            
        SearchRequest request = bld.buildMatchQuery();
        
        ElasticSearchHitIterator itr = new ElasticSearchHitIterator(esRegistryConnection.getRestHighLevelClient(), request);
        
        for (final Map<String,Object> kvp : itr)
        {
            if (kvp.get(field) instanceof String)
            { 
                lidvids.add(kvp.get(field).toString()); 
            }
            else
            {
                @SuppressWarnings("unchecked")
                List<String> clids = (List<String>)kvp.get(field);
                for (String clid : clids) { lidvids.add(clid); }
            }
        }
        
        return lidvids;
    }

    
    private void getContainingCollection(RequestAndResponseContext context) throws IOException,LidVidNotFoundException
    {
        String lidvid = this.productBO.getLatestLidVidFromLid(context.getLIDVID());
    
        MyProductsApiController.log.info("find all bundles containing the product lidvid: " + lidvid);

        List<String> collectionLidvids = this.getCollectionLidvids(lidvid, false);
        
        int size = collectionLidvids.size();
        if (size > 0 && context.getLimit() > 0 && context.getStart() < size)
        {
            int end = context.getStart() + context.getLimit();
            if(end > size) end = size; 
            List<String> ids = collectionLidvids.subList(context.getStart(), end);
            
            this.fillProductsFromLidvids(context, ids, collectionLidvids.size()); 
        }
        else 
        {
            MyProductsApiController.log.warn("Did not find a product with lidvid: " + lidvid);
        }
    }
}
