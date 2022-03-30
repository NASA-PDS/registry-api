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
    		@ApiParam(value = "syntax: lidvid or lid  behavior (lid): returns one or more items whose lid matches this lid exactly. If the endpoint ends with the identifier or /latest then a signle result is returned and it is the highest version. If the endpoint ends with /all then all versions of the lid are returned.  behavior (lidvid): returns one and only one item whose lidvid matches this lidvid exactly.  note: the current lid/lidvid resolution will match all the lids that start with lid. In other words, it acts like a glob of foobar*. It behaves this way from first character to the last  note: simple sorting of the lidvid is being done to select the latest from the end of the list. However, the versions 1.0, 2.0, and 13.0 will sort to 1.0, 13.0, and 2.0 so the end of the list may not be the latest. ",required=true) @PathVariable("identifier") String identifier,
    		@ApiParam(value = "syntax: fileds=field1,field2,...  behavior: this parameter and the headder Accept: type determine what content is packaged for the result. While the types application/csv, application/kvp+json, and text/csv return only the fields requesteted, all of the other types have a minimal set of fields that must be returned. Duplicating a minimally required field in this parameter has not effect. The types vnd.nasa.pds.pds4+json and vnd.nasa.pds.pds4+xml have a complete set of fields that must be returned; meaning this parameter does not impact their content. When fields is not used, then the minimal set of fields, or all when minimal is an empty set, is returned.  notes: the blob fields are blocked unless specifically requrested and only for the *_/csv and application/kvp+csv types. ") @Valid @RequestParam(value = "fields", required = false) List<String> fields)
	{
        return this.getLatestProductResponseEntity(new URIParameters().setFields(fields).setIdentifier(identifier));
    }

    
    @Override
    public ResponseEntity<Object> bundleByLidvidLatest(
    		@ApiParam(value = "syntax: lidvid or lid  behavior (lid): returns one or more items whose lid matches this lid exactly. If the endpoint ends with the identifier or /latest then a signle result is returned and it is the highest version. If the endpoint ends with /all then all versions of the lid are returned.  behavior (lidvid): returns one and only one item whose lidvid matches this lidvid exactly.  note: the current lid/lidvid resolution will match all the lids that start with lid. In other words, it acts like a glob of foobar*. It behaves this way from first character to the last  note: simple sorting of the lidvid is being done to select the latest from the end of the list. However, the versions 1.0, 2.0, and 13.0 will sort to 1.0, 13.0, and 2.0 so the end of the list may not be the latest. ",required=true) @PathVariable("identifier") String identifier,
    		@ApiParam(value = "syntax: fileds=field1,field2,...  behavior: this parameter and the headder Accept: type determine what content is packaged for the result. While the types application/csv, application/kvp+json, and text/csv return only the fields requesteted, all of the other types have a minimal set of fields that must be returned. Duplicating a minimally required field in this parameter has not effect. The types vnd.nasa.pds.pds4+json and vnd.nasa.pds.pds4+xml have a complete set of fields that must be returned; meaning this parameter does not impact their content. When fields is not used, then the minimal set of fields, or all when minimal is an empty set, is returned.  notes: the blob fields are blocked unless specifically requrested and only for the *_/csv and application/kvp+csv types. ") @Valid @RequestParam(value = "fields", required = false) List<String> fields)
    {
        return this.getLatestProductResponseEntity(new URIParameters().setFields(fields).setIdentifier(identifier));
    }

    
    @Override    
    public ResponseEntity<Object> bundleByLidvidAll(
    		@ApiParam(value = "syntax: lidvid or lid  behavior (lid): returns one or more items whose lid matches this lid exactly. If the endpoint ends with the identifier or /latest then a signle result is returned and it is the highest version. If the endpoint ends with /all then all versions of the lid are returned.  behavior (lidvid): returns one and only one item whose lidvid matches this lidvid exactly.  note: the current lid/lidvid resolution will match all the lids that start with lid. In other words, it acts like a glob of foobar*. It behaves this way from first character to the last  note: simple sorting of the lidvid is being done to select the latest from the end of the list. However, the versions 1.0, 2.0, and 13.0 will sort to 1.0, 13.0, and 2.0 so the end of the list may not be the latest. ",required=true) @PathVariable("identifier") String identifier,
    		@ApiParam(value = "syntax: fileds=field1,field2,...  behavior: this parameter and the headder Accept: type determine what content is packaged for the result. While the types application/csv, application/kvp+json, and text/csv return only the fields requesteted, all of the other types have a minimal set of fields that must be returned. Duplicating a minimally required field in this parameter has not effect. The types vnd.nasa.pds.pds4+json and vnd.nasa.pds.pds4+xml have a complete set of fields that must be returned; meaning this parameter does not impact their content. When fields is not used, then the minimal set of fields, or all when minimal is an empty set, is returned.  notes: the blob fields are blocked unless specifically requrested and only for the *_/csv and application/kvp+csv types. ") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
    		@ApiParam(value = "syntax: limit=10  behavior: maximum number of matching results returned, for pagination ", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit,
    		@ApiParam(value = "syntax: sort=asc(field0),desc(field1),...  behavior: is this implemented? ") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
    		@ApiParam(value = "syntax: start=12  behaviro: offset in matching result list, for pagination ", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start,
    		@ApiParam(value = "syntax: summary-only={true,false}  behavior: only return the summary when a list is returned. Useful to get the list of available properties. ", defaultValue = "false") @Valid @RequestParam(value = "summary-only", required = false, defaultValue="false") Boolean summaryOnly
            )
    {
        return this.getAllProductsResponseEntity(new URIParameters()
        		.setFields(fields)
        		.setIdentifier(identifier)
        		.setLimit(limit)
        		.setSelector(ProductVersionSelector.ALL)
        		.setSort(sort)
        		.setStart(start)
        		.setSummanryOnly(summaryOnly));                
    }    
    
    
    @Override    
    public ResponseEntity<Object> getBundles(
    		@ApiParam(value = "syntax: fileds=field1,field2,...  behavior: this parameter and the headder Accept: type determine what content is packaged for the result. While the types application/csv, application/kvp+json, and text/csv return only the fields requesteted, all of the other types have a minimal set of fields that must be returned. Duplicating a minimally required field in this parameter has not effect. The types vnd.nasa.pds.pds4+json and vnd.nasa.pds.pds4+xml have a complete set of fields that must be returned; meaning this parameter does not impact their content. When fields is not used, then the minimal set of fields, or all when minimal is an empty set, is returned.  notes: the blob fields are blocked unless specifically requrested and only for the *_/csv and application/kvp+csv types. ") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
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
    public ResponseEntity<Object> collectionsOfABundle(
    		@ApiParam(value = "syntax: lidvid or lid  behavior (lid): returns one or more items whose lid matches this lid exactly. If the endpoint ends with the identifier or /latest then a signle result is returned and it is the highest version. If the endpoint ends with /all then all versions of the lid are returned.  behavior (lidvid): returns one and only one item whose lidvid matches this lidvid exactly.  note: the current lid/lidvid resolution will match all the lids that start with lid. In other words, it acts like a glob of foobar*. It behaves this way from first character to the last  note: simple sorting of the lidvid is being done to select the latest from the end of the list. However, the versions 1.0, 2.0, and 13.0 will sort to 1.0, 13.0, and 2.0 so the end of the list may not be the latest. ",required=true) @PathVariable("identifier") String identifier,
    		@ApiParam(value = "syntax: fileds=field1,field2,...  behavior: this parameter and the headder Accept: type determine what content is packaged for the result. While the types application/csv, application/kvp+json, and text/csv return only the fields requesteted, all of the other types have a minimal set of fields that must be returned. Duplicating a minimally required field in this parameter has not effect. The types vnd.nasa.pds.pds4+json and vnd.nasa.pds.pds4+xml have a complete set of fields that must be returned; meaning this parameter does not impact their content. When fields is not used, then the minimal set of fields, or all when minimal is an empty set, is returned.  notes: the blob fields are blocked unless specifically requrested and only for the *_/csv and application/kvp+csv types. ") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
    		@ApiParam(value = "syntax: limit=10  behavior: maximum number of matching results returned, for pagination ", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit,
    		@ApiParam(value = "syntax: sort=asc(field0),desc(field1),...  behavior: is this implemented? ") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
    		@ApiParam(value = "syntax: start=12  behaviro: offset in matching result list, for pagination ", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start,
    		@ApiParam(value = "syntax: summary-only={true,false}  behavior: only return the summary when a list is returned. Useful to get the list of available properties. ", defaultValue = "false") @Valid @RequestParam(value = "summary-only", required = false, defaultValue="false") Boolean summaryOnly
            )
    {
        return getBundlesCollectionsEntity(new URIParameters()
        		.setFields(fields)
        		.setIdentifier(identifier)
        		.setLimit(limit)
        		.setSelector(ProductVersionSelector.LATEST)
        		.setSort(sort)
        		.setStart(start)
        		.setSummanryOnly(summaryOnly));
    }
    
    
    @Override    
    public ResponseEntity<Object> collectionsOfABundleAll(
    		@ApiParam(value = "syntax: lidvid or lid  behavior (lid): returns one or more items whose lid matches this lid exactly. If the endpoint ends with the identifier or /latest then a signle result is returned and it is the highest version. If the endpoint ends with /all then all versions of the lid are returned.  behavior (lidvid): returns one and only one item whose lidvid matches this lidvid exactly.  note: the current lid/lidvid resolution will match all the lids that start with lid. In other words, it acts like a glob of foobar*. It behaves this way from first character to the last  note: simple sorting of the lidvid is being done to select the latest from the end of the list. However, the versions 1.0, 2.0, and 13.0 will sort to 1.0, 13.0, and 2.0 so the end of the list may not be the latest. ",required=true) @PathVariable("identifier") String identifier,
    		@ApiParam(value = "syntax: fields=field1,field2,...  behavior: this parameter and the headder Accept: type determine what content is packaged for the result. While the types application/csv, application/kvp+json, and text/csv return only the fields requesteted, all of the other types have a minimal set of fields that must be returned. Duplicating a minimally required field in this parameter has not effect. The types vnd.nasa.pds.pds4+json and vnd.nasa.pds.pds4+xml have a complete set of fields that must be returned; meaning this parameter does not impact their content. When fields is not used, then the minimal set of fields, or all when minimal is an empty set, is returned.  notes: the blob fields are blocked unless specifically requrested and only for the *_/csv and application/kvp+csv types. ") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
    		@ApiParam(value = "syntax: limit=10  behavior: maximum number of matching results returned, for pagination ", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit,
    		@ApiParam(value = "syntax: sort=asc(field0),desc(field1),...  behavior: is this implemented? ") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
    		@ApiParam(value = "syntax: start=12  behaviro: offset in matching result list, for pagination ", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start,
    		@ApiParam(value = "syntax: summary-only={true,false}  behavior: only return the summary when a list is returned. Useful to get the list of available properties. ", defaultValue = "false") @Valid @RequestParam(value = "summary-only", required = false, defaultValue="false") Boolean summaryOnly
    		)
    {
        return getBundlesCollectionsEntity(new URIParameters()
        		.setFields(fields)
        		.setIdentifier(identifier)
        		.setLimit(limit)
        		.setSelector(ProductVersionSelector.ALL)
        		.setSort(sort)
        		.setStart(start)
        		.setSummanryOnly(summaryOnly));
    }    
    
    
    @Override    
    public ResponseEntity<Object> collectionsOfABundleLatest(
    		@ApiParam(value = "syntax: lidvid or lid  behavior (lid): returns one or more items whose lid matches this lid exactly. If the endpoint ends with the identifier or /latest then a signle result is returned and it is the highest version. If the endpoint ends with /all then all versions of the lid are returned.  behavior (lidvid): returns one and only one item whose lidvid matches this lidvid exactly.  note: the current lid/lidvid resolution will match all the lids that start with lid. In other words, it acts like a glob of foobar*. It behaves this way from first character to the last  note: simple sorting of the lidvid is being done to select the latest from the end of the list. However, the versions 1.0, 2.0, and 13.0 will sort to 1.0, 13.0, and 2.0 so the end of the list may not be the latest. ",required=true) @PathVariable("identifier") String identifier,
    		@ApiParam(value = "syntax: fileds=field1,field2,...  behavior: this parameter and the headder Accept: type determine what content is packaged for the result. While the types application/csv, application/kvp+json, and text/csv return only the fields requesteted, all of the other types have a minimal set of fields that must be returned. Duplicating a minimally required field in this parameter has not effect. The types vnd.nasa.pds.pds4+json and vnd.nasa.pds.pds4+xml have a complete set of fields that must be returned; meaning this parameter does not impact their content. When fields is not used, then the minimal set of fields, or all when minimal is an empty set, is returned.  notes: the blob fields are blocked unless specifically requrested and only for the *_/csv and application/kvp+csv types. ") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
    		@ApiParam(value = "syntax: limit=10  behavior: maximum number of matching results returned, for pagination ", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit,
    		@ApiParam(value = "syntax: sort=asc(field0),desc(field1),...  behavior: is this implemented? ") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
    		@ApiParam(value = "syntax: start=12  behaviro: offset in matching result list, for pagination ", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start,
    		@ApiParam(value = "syntax: summary-only={true,false}  behavior: only return the summary when a list is returned. Useful to get the list of available properties. ", defaultValue = "false") @Valid @RequestParam(value = "summary-only", required = false, defaultValue="false") Boolean summaryOnly
    		)
    {
        return getBundlesCollectionsEntity(new URIParameters()
        		.setFields(fields)
        		.setIdentifier(identifier)
        		.setLimit(limit)
        		.setSelector(ProductVersionSelector.LATEST)
        		.setSort(sort)
        		.setStart(start)
        		.setSummanryOnly(summaryOnly));
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

    
    private ResponseEntity<Object> getBundlesCollectionsEntity(URIParameters parameters)
    {
         String accept = this.request.getHeader("Accept");
         MyBundlesApiController.log.info("accept value is " + accept);

         try
         {
        	 RequestAndResponseContext context = RequestAndResponseContext.buildRequestAndResponseContext(this.objectMapper, this.getBaseURL(), parameters, this.presetCriteria, accept);
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
        	 log.warn("Could not find lid(vid) in database: " + parameters.getIdentifier());
        	 return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
         }
         catch (NothingFoundException e)
         {
        	 log.warn("Could not find any matching reference(s) in database.");
        	 return new ResponseEntity<Object>(ErrorFactory.build(e, this.request), HttpStatus.NOT_FOUND);
         }
    }

    
    @Override
    public ResponseEntity<Object> productsOfABundle(
    		@ApiParam(value = "syntax: lidvid or lid  behavior (lid): returns one or more items whose lid matches this lid exactly. If the endpoint ends with the identifier or /latest then a signle result is returned and it is the highest version. If the endpoint ends with /all then all versions of the lid are returned.  behavior (lidvid): returns one and only one item whose lidvid matches this lidvid exactly.  note: the current lid/lidvid resolution will match all the lids that start with lid. In other words, it acts like a glob of foobar*. It behaves this way from first character to the last  note: simple sorting of the lidvid is being done to select the latest from the end of the list. However, the versions 1.0, 2.0, and 13.0 will sort to 1.0, 13.0, and 2.0 so the end of the list may not be the latest. ",required=true) @PathVariable("identifier") String identifier,
    		@ApiParam(value = "syntax: fileds=field1,field2,...  behavior: this parameter and the headder Accept: type determine what content is packaged for the result. While the types application/csv, application/kvp+json, and text/csv return only the fields requesteted, all of the other types have a minimal set of fields that must be returned. Duplicating a minimally required field in this parameter has not effect. The types vnd.nasa.pds.pds4+json and vnd.nasa.pds.pds4+xml have a complete set of fields that must be returned; meaning this parameter does not impact their content. When fields is not used, then the minimal set of fields, or all when minimal is an empty set, is returned.  notes: the blob fields are blocked unless specifically requrested and only for the *_/csv and application/kvp+csv types. ") @Valid @RequestParam(value = "fields", required = false) List<String> fields,
    		@ApiParam(value = "syntax: limit=10  behavior: maximum number of matching results returned, for pagination ", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit,
    		@ApiParam(value = "syntax: sort=asc(field0),desc(field1),...  behavior: is this implemented? ") @Valid @RequestParam(value = "sort", required = false) List<String> sort,
    		@ApiParam(value = "syntax: start=12  behaviro: offset in matching result list, for pagination ", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start,
    		@ApiParam(value = "syntax: summary-only={true,false}  behavior: only return the summary when a list is returned. Useful to get the list of available properties. ", defaultValue = "false") @Valid @RequestParam(value = "summary-only", required = false, defaultValue="false") Boolean summaryOnly
    		)
    {
         String accept = this.request.getHeader("Accept");
         MyBundlesApiController.log.info("accept value is " + accept);
         URIParameters parameters = new URIParameters()
        		 .setFields(fields)
        		 .setIdentifier(identifier)
        		 .setLimit(limit)
        		 .setSort(sort)
        		 .setStart(start)
        		 .setSummanryOnly(summaryOnly);

         try
         {
        	 RequestAndResponseContext context = RequestAndResponseContext.buildRequestAndResponseContext(
        	         this.objectMapper, this.getBaseURL(), parameters, this.presetCriteria, accept);
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
        	 log.warn("Could not find lid(vid) in database: " + identifier);
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
