package gov.nasa.pds.api.engineering.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchHitIterator;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnection;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistrySearchRequestBuilder;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchUtil;
import gov.nasa.pds.api.engineering.elasticsearch.GetProductsRequest;
import gov.nasa.pds.api.engineering.elasticsearch.business.LidVidNotFoundException;
import gov.nasa.pds.api.engineering.elasticsearch.business.ProductBusinessObject;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntityProduct;

import gov.nasa.pds.api.model.xml.XMLMashallableProperyValue;
import gov.nasa.pds.model.Product;
import gov.nasa.pds.model.PropertyArrayValues;
import gov.nasa.pds.model.Products;
import gov.nasa.pds.model.Summary;

@Component
public class MyProductsApiBareController {
    
    private static final Logger log = LoggerFactory.getLogger(MyProductsApiBareController.class);  
    
    protected final ObjectMapper objectMapper;

    protected final HttpServletRequest request;   

    protected Map<String, String> presetCriteria = new HashMap<String, String>();
    
    @Value("${server.contextPath}")
    protected String contextPath;
    
    @Autowired
    protected HttpServletRequest context;
    
    // TODO remove and replace by BusinessObjects 
    @Autowired
    ElasticSearchRegistryConnection esRegistryConnection;
    
    @Autowired
    protected ProductBusinessObject productBO;
    
    @Autowired
    ElasticSearchRegistrySearchRequestBuilder searchRequestBuilder;
    

    public MyProductsApiBareController(ObjectMapper objectMapper, HttpServletRequest context) {
        this.objectMapper = objectMapper;
        this.request = context;
    }

    @SuppressWarnings("unchecked")
    protected void fillProductsFromLidvids (Products products, HashSet<String> uniqueProperties, List<String> lidvids, List<String> fields, boolean onlySummary) throws IOException
    {
        for (final Map<String,Object> kvp : new ElasticSearchHitIterator(lidvids.size(), this.esRegistryConnection.getRestHighLevelClient(),
                ElasticSearchRegistrySearchRequestBuilder.getQueryFieldsFromKVP("lidvid",
                        lidvids, fields, this.esRegistryConnection.getRegistryIndex())))
        {
            uniqueProperties.addAll(kvp.keySet());

            if (!onlySummary)
            {
                products.addDataItem(ElasticSearchUtil.ESentityProductToAPIProduct(objectMapper.convertValue(kvp, EntityProduct.class), this.getBaseURL()));
                products.getData().get(products.getData().size()-1).setProperties((Map<String, PropertyArrayValues>)(Map<String, ?>)ProductBusinessObject.getFilteredProperties(kvp, null, null));
            }
        }

    }

    
    @SuppressWarnings("unchecked")
    protected void fillProductsFromParents (Products products, HashSet<String> uniqueProperties, List<Map<String,Object>> results, boolean onlySummary) throws IOException
    {
        for (Map<String,Object> kvp : results)
        {
            uniqueProperties.addAll(kvp.keySet());

            if (!onlySummary)
            {
                products.addDataItem(ElasticSearchUtil.ESentityProductToAPIProduct(objectMapper.convertValue(kvp, EntityProduct.class), this.getBaseURL()));
                products.getData().get(products.getData().size()-1).setProperties((Map<String, PropertyArrayValues>)(Map<String, ?>)ProductBusinessObject.getFilteredProperties(kvp, null, null));
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected Products getProducts(String q, String keyword, int start, int limit, List<String> fields, List<String> sort, boolean onlySummary) throws IOException
    {
        long begin = System.currentTimeMillis();

        SearchRequest searchRequest = this.searchRequestBuilder.getSearchProductsRequest(q, keyword, fields, start, limit, this.presetCriteria);
        
        SearchResponse searchResponse = this.esRegistryConnection.getRestHighLevelClient().search(searchRequest, 
                RequestOptions.DEFAULT);
        
        Products products = new Products();
        
        HashSet<String> uniqueProperties = new HashSet<String>();
        
        Summary summary = new Summary();

        summary.setHits(-1);
        summary.setLimit(limit);
        summary.setQ((q != null)?q:"" );
        summary.setStart(start);
        summary.setTook(-1);

        if (sort == null) {
            sort = Arrays.asList();
        }   
        summary.setSort(sort);
        
        products.setSummary(summary);
        
        if (searchResponse != null) {
            summary.setHits((int)searchResponse.getHits().getTotalHits().value);
            for (SearchHit searchHit : searchResponse.getHits()) {
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                
                Map<String, XMLMashallableProperyValue> filteredMapJsonProperties = ProductBusinessObject.getFilteredProperties(
                        sourceAsMap, 
                        fields, 
                        null
                        );
                
                uniqueProperties.addAll(filteredMapJsonProperties.keySet());

                if (!onlySummary) {
                    
                    
                    EntityProduct entityProduct = objectMapper.convertValue(sourceAsMap, EntityProduct.class);

                    Product product = ElasticSearchUtil.ESentityProductToAPIProduct(
                            entityProduct, 
                            this.getBaseURL());
                    product.setProperties((Map<String, PropertyArrayValues>)(Map<String, ?>)filteredMapJsonProperties);

                    products.addDataItem(product);
                }
                
            }
     
            
        }
        
        
        summary.setProperties(new ArrayList<String>(uniqueProperties));
        summary.setTook((int)(System.currentTimeMillis() - begin));
        return products;
    }
    
 

    
    
    protected ResponseEntity<Object> getProductsResponseEntity(String q, String keyword, int start, int limit,
            List<String> fields, List<String> sort, boolean onlySummary)
    {
        String accept = this.request.getHeader("Accept");
        log.info("accept value is " + accept);
        if ((accept != null && 
                (accept.contains("application/json")
                        || accept.contains("application/pds4+json")
                        || accept.contains("text/html")
                        || accept.contains("application/xml")
                        || accept.contains("*/*"))) 
                        || (accept == null))
        {
            try
            {
                Object products = null;
                if("application/pds4+json".equals(accept))
                {
                    GetProductsRequest req = new GetProductsRequest();
                    req.setSearchCriteria(q, keyword);
                    req.setPageInfo(start, limit);
                    req.setFields(fields, sort);
                    req.presetCriteria = presetCriteria;
                    req.onlySummary = onlySummary;
                    
                    products = productBO.getPds4Products(req);
                }
                else
                {
                    products = this.getProducts(q, keyword, start, limit, fields, sort, onlySummary);
                }
                
                return new ResponseEntity<Object>(products, HttpStatus.OK);
            }
            catch (IOException e)
            {
                log.error("Couldn't serialize response for content type " + accept, e);
                return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            catch (ParseCancellationException pce)
            {
                log.error("Could not parse the query string: " + q);
                return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
        }
    }    
    
    
    protected ResponseEntity<Object> getAllProductsResponseEntity(String lidvid, int start, int limit)
    {
        String accept = this.request.getHeader("Accept");
        log.info("accept value is " + accept);
        if ((accept != null && (accept.contains("application/json") || accept.contains("text/html")
                || accept.contains("application/xml") || accept.contains("*/*"))) || (accept == null))
        {
            try
            {
                Products products = getProductsByLid(lidvid, start, limit);
                return new ResponseEntity<Object>(products, HttpStatus.OK);
            }
            catch (IOException e)
            {
                log.error("Couldn't serialize response for content type " + accept, e);
                return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            catch (ParseCancellationException pce)
            {
                log.error("", pce);
                return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
        }
    }    
    
    
    public Products getProductsByLid(String lid, int start, int limit) throws IOException 
    {
        SearchRequest req = searchRequestBuilder.getSearchProductsByLid(lid, start, limit);
        SearchResponse resp = esRegistryConnection.getRestHighLevelClient().search(req, RequestOptions.DEFAULT);
        
        Products products = new Products();
        
        Summary summary = new Summary();
        summary.setStart(start);
        summary.setLimit(limit);
        products.setSummary(summary);
        
        if(resp == null) return products;

        Set<String> uniqueProperties = new TreeSet<>();

        for(SearchHit searchHit : resp.getHits()) 
        {
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            
            Map<String, XMLMashallableProperyValue> filteredMapJsonProperties = ProductBusinessObject
                    .getFilteredProperties(sourceAsMap, null, null);
            
            uniqueProperties.addAll(filteredMapJsonProperties.keySet());

            EntityProduct entityProduct = objectMapper.convertValue(sourceAsMap, EntityProduct.class);

            Product product = ElasticSearchUtil.ESentityProductToAPIProduct(entityProduct, this.getBaseURL());
            product.setProperties((Map<String, PropertyArrayValues>)(Map<String, ?>)filteredMapJsonProperties);

            products.addDataItem(product);
        }
        
        summary.setProperties(new ArrayList<String>(uniqueProperties));
        return products;
    }

    
    protected ResponseEntity<Object> getLatestProductResponseEntity(String lidvid)
    {
        String accept = request.getHeader("Accept");
        if ((accept != null) 
                && (accept.contains("application/json")
                || accept.contains("text/html")
                || accept.contains("*/*")
                || accept.contains("application/xml")
                || accept.contains("application/pds4+xml")
                || accept.contains("application/pds4+json"))) {
            
            try 
            {
                lidvid = this.productBO.getLatestLidVidFromLid(lidvid);
                
                Object product = null;
                
                if("application/pds4+json".equals(accept))
                {
                    product = productBO.getPds4Product(lidvid);
                }
                else
                {
                    product = this.productBO.getProductWithXml(lidvid, this.getBaseURL());
                }
                
                if (product != null) 
                {   
                    return new ResponseEntity<Object>(product, HttpStatus.OK);
                }                   
                else 
                {
                    // TODO send 302 redirection to a different server if one exists
                    return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
                }
            } 
            catch (IOException e) 
            {
                log.error("Couldn't get or serialize response for content type " + accept, e);
                return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            catch (LidVidNotFoundException e)
            {
                log.warn("Could not find lid(vid) in database: " + lidvid);
                return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
            }
        }

        return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
    }

    
    private boolean proxyRunsOnDefaultPort() {
        return (((this.context.getScheme() == "https")  && (this.context.getServerPort() == 443)) 
                || ((this.context.getScheme() == "http")  && (this.context.getServerPort() == 80)));
    }
 
    protected URL getBaseURL() {
        try {
            MyProductsApiBareController.log.debug("contextPath is: " + this.contextPath);
            
            URL baseURL;
            if (this.proxyRunsOnDefaultPort()) {
                baseURL = new URL(this.context.getScheme(), this.context.getServerName(), this.contextPath);
            } 
            else {
                baseURL = new URL(this.context.getScheme(), this.context.getServerName(), this.context.getServerPort(), this.contextPath);
            }
            
            MyProductsApiBareController.log.info("baseUrl is " + baseURL.toString());
            return baseURL;
            

        } catch (MalformedURLException e) {
            log.error("Server URL was not retrieved");
            return null;
        }
    }

}
