package gov.nasa.pds.api.registry.controllers;


import gov.nasa.pds.api.base.CollectionsApi;
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



@Controller
public class MyCollectionsApiController extends MyProductsApiBareController implements CollectionsApi {

    private static final Logger log = LoggerFactory.getLogger(MyCollectionsApiController.class);
    
 
    public MyCollectionsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        super(objectMapper, request);
        
        this.presetCriteria.put("product_class", "Product_Collection");
    
    }
    
    
    @Override
    public ResponseEntity<Object> collectionsByLidvid(
    		@ApiParam(value = "syntax: lidvid or lid  behavior (lid): returns one or more items whose lid matches this lid exactly. If the endpoint ends with the identifier or /latest then a signle result is returned and it is the highest version. If the endpoint ends with /all then all versions of the lid are returned.  behavior (lidvid): returns one and only one item whose lidvid matches this lidvid exactly.  note: the current lid/lidvid resolution will match all the lids that start with lid. In other words, it acts like a glob of foobar*. It behaves this way from first character to the last  note: simple sorting of the lidvid is being done to select the latest from the end of the list. However, the versions 1.0, 2.0, and 13.0 will sort to 1.0, 13.0, and 2.0 so the end of the list may not be the latest. ",required=true) @PathVariable("identifier") String identifier,
    		@ApiParam(value = "syntax: fields=field1,field2,...  behavior: this parameter and the headder Accept: type determine what content is packaged for the result. While the types application/csv, application/kvp+json, and text/csv return only the fields requesteted, all of the other types have a minimal set of fields that must be returned. Duplicating a minimally required field in this parameter has not effect. The types vnd.nasa.pds.pds4+json and vnd.nasa.pds.pds4+xml have a complete set of fields that must be returned; meaning this parameter does not impact their content. When fields is not used, then the minimal set of fields, or all when minimal is an empty set, is returned.  notes: the blob fields are blocked unless specifically requrested and only for the *_/csv and application/kvp+csv types. ") @Valid @RequestParam(value = "fields", required = false) List<String> fields
    		)
    {
        return this.getLatestProductResponseEntity(new URIParameters().setFields(fields).setIdentifier(identifier));
    }

    @Override
    public ResponseEntity<Object> collectionsByLidvidLatest(
    		@ApiParam(value = "syntax: lidvid or lid  behavior (lid): returns one or more items whose lid matches this lid exactly. If the endpoint ends with the identifier or /latest then a signle result is returned and it is the highest version. If the endpoint ends with /all then all versions of the lid are returned.  behavior (lidvid): returns one and only one item whose lidvid matches this lidvid exactly.  note: the current lid/lidvid resolution will match all the lids that start with lid. In other words, it acts like a glob of foobar*. It behaves this way from first character to the last  note: simple sorting of the lidvid is being done to select the latest from the end of the list. However, the versions 1.0, 2.0, and 13.0 will sort to 1.0, 13.0, and 2.0 so the end of the list may not be the latest. ",required=true) @PathVariable("identifier") String identifier,
    		@ApiParam(value = "syntax: fields=field1,field2,...  behavior: this parameter and the headder Accept: type determine what content is packaged for the result. While the types application/csv, application/kvp+json, and text/csv return only the fields requesteted, all of the other types have a minimal set of fields that must be returned. Duplicating a minimally required field in this parameter has not effect. The types vnd.nasa.pds.pds4+json and vnd.nasa.pds.pds4+xml have a complete set of fields that must be returned; meaning this parameter does not impact their content. When fields is not used, then the minimal set of fields, or all when minimal is an empty set, is returned.  notes: the blob fields are blocked unless specifically requrested and only for the *_/csv and application/kvp+csv types. ") @Valid @RequestParam(value = "fields", required = false) List<String> fields
    		)
    {
        return this.getLatestProductResponseEntity(new URIParameters().setFields(fields).setIdentifier(identifier));
    }
    
    
    @Override
    public ResponseEntity<Object> collectionsByLidvidAll(
    		@ApiParam(value = "syntax: lidvid or lid  behavior (lid): returns one or more items whose lid matches this lid exactly. If the endpoint ends with the identifier or /latest then a signle result is returned and it is the highest version. If the endpoint ends with /all then all versions of the lid are returned.  behavior (lidvid): returns one and only one item whose lidvid matches this lidvid exactly.  note: the current lid/lidvid resolution will match all the lids that start with lid. In other words, it acts like a glob of foobar*. It behaves this way from first character to the last  note: simple sorting of the lidvid is being done to select the latest from the end of the list. However, the versions 1.0, 2.0, and 13.0 will sort to 1.0, 13.0, and 2.0 so the end of the list may not be the latest. ",required=true) @PathVariable("identifier") String identifier,
    		@ApiParam(value = "syntax: fields=field1,field2,...  behavior: this parameter and the headder Accept: type determine what content is packaged for the result. While the types application/csv, application/kvp+json, and text/csv return only the fields requesteted, all of the other types have a minimal set of fields that must be returned. Duplicating a minimally required field in this parameter has not effect. The types vnd.nasa.pds.pds4+json and vnd.nasa.pds.pds4+xml have a complete set of fields that must be returned; meaning this parameter does not impact their content. When fields is not used, then the minimal set of fields, or all when minimal is an empty set, is returned.  notes: the blob fields are blocked unless specifically requrested and only for the *_/csv and application/kvp+csv types. ") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
    		@ApiParam(value = "syntax: limit=10  behavior: maximum number of matching results returned, for pagination ", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit,
    		@ApiParam(value = "syntax: sort=asc(field0),desc(field1),...  behavior: is this implemented? ") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
    		@ApiParam(value = "syntax: start=12  behaviro: offset in matching result list, for pagination ", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start,
    		@ApiParam(value = "syntax: summary-only={true,false}  behavior: only return the summary when a list is returned. Useful to get the list of available properties. ", defaultValue = "false") @Valid @RequestParam(value = "summary-only", required = false, defaultValue="false") Boolean summaryOnly
    		)
    {
        return getAllProductsResponseEntity(new URIParameters()
        		.setFields(fields)
        		.setIdentifier(identifier)
        		.setLimit(limit)
        		.setSelector(ProductVersionSelector.ALL)
        		.setSort(sort)
        		.setStart(start)
        		.setSummanryOnly(summaryOnly));                
    }

    
    @Override
    public ResponseEntity<Object> getCollection(
    		@ApiParam(value = "syntax: fields=field1,field2,...  behavior: this parameter and the headder Accept: type determine what content is packaged for the result. While the types application/csv, application/kvp+json, and text/csv return only the fields requesteted, all of the other types have a minimal set of fields that must be returned. Duplicating a minimally required field in this parameter has not effect. The types vnd.nasa.pds.pds4+json and vnd.nasa.pds.pds4+xml have a complete set of fields that must be returned; meaning this parameter does not impact their content. When fields is not used, then the minimal set of fields, or all when minimal is an empty set, is returned.  notes: the blob fields are blocked unless specifically requrested and only for the *_/csv and application/kvp+csv types. ") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
    		@ApiParam(value = "syntax: keyword=keyword1,keyword2,...  behaviro: free text search on title and description (if set q is ignored  notes: is this implemented? ") @Valid @RequestParam(value = "keyword", required = false) List<String> keywords,
    		@ApiParam(value = "syntax: limit=10  behavior: maximum number of matching results returned, for pagination ", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit,
    		@ApiParam(value = "syntax: q=\"vid eq 13.0\"  behaviro: query uses eq,ne,gt,ge,lt,le,(,),not,and,or operators. Properties are named as in 'properties' attributes, literals are strings between quotes, like \"animal\", or numbers. Detailed query specification is available at https://bit.ly/3h3D54T  note: ignored when keyword is present ") @Valid @RequestParam(value = "q", required = false) String q,
    		@ApiParam(value = "syntax: sort=asc(field0),desc(field1),...  behavior: is this implemented? ") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
    		@ApiParam(value = "syntax: start=12  behaviro: offset in matching result list, for pagination ", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start,
    		@ApiParam(value = "syntax: summary-only={true,false}  behavior: only return the summary when a list is returned. Useful to get the list of available properties. ", defaultValue = "false") @Valid @RequestParam(value = "summary-only", required = false, defaultValue="false") Boolean summaryOnly
    		)
    {
        return this.getProductsResponseEntity(new URIParameters()
        		.setFields(fields)
        		.setKeywords(keywords)
        		.setLimit(limit)
        		.setQuery(q)
        		.setSort(sort)
        		.setStart(start)
        		.setSummanryOnly(summaryOnly));
    }    

    
    @Override
    public ResponseEntity<Object> productsOfACollection(
    		@ApiParam(value = "syntax: lidvid or lid  behavior (lid): returns one or more items whose lid matches this lid exactly. If the endpoint ends with the identifier or /latest then a signle result is returned and it is the highest version. If the endpoint ends with /all then all versions of the lid are returned.  behavior (lidvid): returns one and only one item whose lidvid matches this lidvid exactly.  note: the current lid/lidvid resolution will match all the lids that start with lid. In other words, it acts like a glob of foobar*. It behaves this way from first character to the last  note: simple sorting of the lidvid is being done to select the latest from the end of the list. However, the versions 1.0, 2.0, and 13.0 will sort to 1.0, 13.0, and 2.0 so the end of the list may not be the latest. ",required=true) @PathVariable("identifier") String identifier,
    		@ApiParam(value = "syntax: fields=field1,field2,...  behavior: this parameter and the headder Accept: type determine what content is packaged for the result. While the types application/csv, application/kvp+json, and text/csv return only the fields requesteted, all of the other types have a minimal set of fields that must be returned. Duplicating a minimally required field in this parameter has not effect. The types vnd.nasa.pds.pds4+json and vnd.nasa.pds.pds4+xml have a complete set of fields that must be returned; meaning this parameter does not impact their content. When fields is not used, then the minimal set of fields, or all when minimal is an empty set, is returned.  notes: the blob fields are blocked unless specifically requrested and only for the *_/csv and application/kvp+csv types. ") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
    		@ApiParam(value = "syntax: limit=10  behavior: maximum number of matching results returned, for pagination ", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit,
    		@ApiParam(value = "syntax: sort=asc(field0),desc(field1),...  behavior: is this implemented? ") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
    		@ApiParam(value = "syntax: start=12  behaviro: offset in matching result list, for pagination ", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start,
    		@ApiParam(value = "syntax: summary-only={true,false}  behavior: only return the summary when a list is returned. Useful to get the list of available properties. ", defaultValue = "false") @Valid @RequestParam(value = "summary-only", required = false, defaultValue="false") Boolean summaryOnly
    		)
    {
        return getProductsOfACollectionResponseEntity(new URIParameters()
        		.setFields(fields)
        		.setIdentifier(identifier)
        		.setLimit(limit)
        		.setSelector(ProductVersionSelector.LATEST)
        		.setSort(sort)
        		.setStart(start)
        		.setSummanryOnly(summaryOnly));
    }

    
    @Override
    public ResponseEntity<Object> productsOfACollectionLatest(
    		@ApiParam(value = "syntax: lidvid or lid  behavior (lid): returns one or more items whose lid matches this lid exactly. If the endpoint ends with the identifier or /latest then a signle result is returned and it is the highest version. If the endpoint ends with /all then all versions of the lid are returned.  behavior (lidvid): returns one and only one item whose lidvid matches this lidvid exactly.  note: the current lid/lidvid resolution will match all the lids that start with lid. In other words, it acts like a glob of foobar*. It behaves this way from first character to the last  note: simple sorting of the lidvid is being done to select the latest from the end of the list. However, the versions 1.0, 2.0, and 13.0 will sort to 1.0, 13.0, and 2.0 so the end of the list may not be the latest. ",required=true) @PathVariable("identifier") String identifier,
    		@ApiParam(value = "syntax: fields=field1,field2,...  behavior: this parameter and the headder Accept: type determine what content is packaged for the result. While the types application/csv, application/kvp+json, and text/csv return only the fields requesteted, all of the other types have a minimal set of fields that must be returned. Duplicating a minimally required field in this parameter has not effect. The types vnd.nasa.pds.pds4+json and vnd.nasa.pds.pds4+xml have a complete set of fields that must be returned; meaning this parameter does not impact their content. When fields is not used, then the minimal set of fields, or all when minimal is an empty set, is returned.  notes: the blob fields are blocked unless specifically requrested and only for the *_/csv and application/kvp+csv types. ") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
    		@ApiParam(value = "syntax: limit=10  behavior: maximum number of matching results returned, for pagination ", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit,
    		@ApiParam(value = "syntax: sort=asc(field0),desc(field1),...  behavior: is this implemented? ") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
    		@ApiParam(value = "syntax: start=12  behaviro: offset in matching result list, for pagination ", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start,
    		@ApiParam(value = "syntax: summary-only={true,false}  behavior: only return the summary when a list is returned. Useful to get the list of available properties. ", defaultValue = "false") @Valid @RequestParam(value = "summary-only", required = false, defaultValue="false") Boolean summaryOnly
    		)
    {
        return getProductsOfACollectionResponseEntity(new URIParameters()
        		.setFields(fields)
        		.setIdentifier(identifier)
        		.setLimit(limit)
        		.setSelector(ProductVersionSelector.LATEST)
        		.setSort(sort)
        		.setStart(start)
        		.setSummanryOnly(summaryOnly));
    }

    
    @Override
    public ResponseEntity<Object> productsOfACollectionAll(
    		@ApiParam(value = "syntax: lidvid or lid  behavior (lid): returns one or more items whose lid matches this lid exactly. If the endpoint ends with the identifier or /latest then a signle result is returned and it is the highest version. If the endpoint ends with /all then all versions of the lid are returned.  behavior (lidvid): returns one and only one item whose lidvid matches this lidvid exactly.  note: the current lid/lidvid resolution will match all the lids that start with lid. In other words, it acts like a glob of foobar*. It behaves this way from first character to the last  note: simple sorting of the lidvid is being done to select the latest from the end of the list. However, the versions 1.0, 2.0, and 13.0 will sort to 1.0, 13.0, and 2.0 so the end of the list may not be the latest. ",required=true) @PathVariable("identifier") String identifier,
    		@ApiParam(value = "syntax: fields=field1,field2,...  behavior: this parameter and the headder Accept: type determine what content is packaged for the result. While the types application/csv, application/kvp+json, and text/csv return only the fields requesteted, all of the other types have a minimal set of fields that must be returned. Duplicating a minimally required field in this parameter has not effect. The types vnd.nasa.pds.pds4+json and vnd.nasa.pds.pds4+xml have a complete set of fields that must be returned; meaning this parameter does not impact their content. When fields is not used, then the minimal set of fields, or all when minimal is an empty set, is returned.  notes: the blob fields are blocked unless specifically requrested and only for the *_/csv and application/kvp+csv types. ") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
    		@ApiParam(value = "syntax: limit=10  behavior: maximum number of matching results returned, for pagination ", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit,
    		@ApiParam(value = "syntax: sort=asc(field0),desc(field1),...  behavior: is this implemented? ") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
    		@ApiParam(value = "syntax: start=12  behaviro: offset in matching result list, for pagination ", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start,
    		@ApiParam(value = "syntax: summary-only={true,false}  behavior: only return the summary when a list is returned. Useful to get the list of available properties. ", defaultValue = "false") @Valid @RequestParam(value = "summary-only", required = false, defaultValue="false") Boolean summaryOnly
    		)
    {
        return getProductsOfACollectionResponseEntity(new URIParameters()
        		.setFields(fields)
        		.setIdentifier(identifier)
        		.setLimit(limit)
        		.setSelector(ProductVersionSelector.ALL)
        		.setSort(sort)
        		.setStart(start)
        		.setSummanryOnly(summaryOnly));
    }    
    
    
    protected ResponseEntity<Object> getProductsOfACollectionResponseEntity(URIParameters parameters)
    {
        String accept = this.request.getHeader("Accept");
        MyCollectionsApiController.log.info("Get productsOfACollection");

        try
        {
        	RequestAndResponseContext context = RequestAndResponseContext.buildRequestAndResponseContext(this.objectMapper, this.getBaseURL(), parameters, this.presetCriteria, accept);
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
    public ResponseEntity<Object> bundlesContainingCollection(
    		@ApiParam(value = "syntax: lidvid or lid  behavior (lid): returns one or more items whose lid matches this lid exactly. If the endpoint ends with the identifier or /latest then a signle result is returned and it is the highest version. If the endpoint ends with /all then all versions of the lid are returned.  behavior (lidvid): returns one and only one item whose lidvid matches this lidvid exactly.  note: the current lid/lidvid resolution will match all the lids that start with lid. In other words, it acts like a glob of foobar*. It behaves this way from first character to the last  note: simple sorting of the lidvid is being done to select the latest from the end of the list. However, the versions 1.0, 2.0, and 13.0 will sort to 1.0, 13.0, and 2.0 so the end of the list may not be the latest. ",required=true) @PathVariable("identifier") String identifier,
    		@ApiParam(value = "syntax: fields=field1,field2,...  behavior: this parameter and the headder Accept: type determine what content is packaged for the result. While the types application/csv, application/kvp+json, and text/csv return only the fields requesteted, all of the other types have a minimal set of fields that must be returned. Duplicating a minimally required field in this parameter has not effect. The types vnd.nasa.pds.pds4+json and vnd.nasa.pds.pds4+xml have a complete set of fields that must be returned; meaning this parameter does not impact their content. When fields is not used, then the minimal set of fields, or all when minimal is an empty set, is returned.  notes: the blob fields are blocked unless specifically requrested and only for the *_/csv and application/kvp+csv types. ") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
    		@ApiParam(value = "syntax: limit=10  behavior: maximum number of matching results returned, for pagination ", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit,
    		@ApiParam(value = "syntax: sort=asc(field0),desc(field1),...  behavior: is this implemented? ") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
    		@ApiParam(value = "syntax: start=12  behaviro: offset in matching result list, for pagination ", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start,
    		@ApiParam(value = "syntax: summary-only={true,false}  behavior: only return the summary when a list is returned. Useful to get the list of available properties. ", defaultValue = "false") @Valid @RequestParam(value = "summary-only", required = false, defaultValue="false") Boolean summaryOnly
    		)
    {
        String accept = this.request.getHeader("Accept");
        MyCollectionsApiController.log.info("accept value is " + accept);
        URIParameters parameters = new URIParameters()
        		.setFields(fields)
        		.setIdentifier(identifier)
        		.setLimit(limit)
        		.setSort(sort)
        		.setStart(start)
        		.setSummanryOnly(summaryOnly);
        try
        {
        	RequestAndResponseContext context = RequestAndResponseContext.buildRequestAndResponseContext(this.objectMapper, this.getBaseURL(), parameters, this.presetCriteria, accept);
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
            log.warn("Could not find lid(vid) in database: " + identifier);
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
