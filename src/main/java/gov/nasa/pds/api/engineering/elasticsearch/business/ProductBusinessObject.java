package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnection;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistrySearchRequestBuilder;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchUtil;
import gov.nasa.pds.api.engineering.elasticsearch.GetProductsRequest;
import gov.nasa.pds.api.engineering.elasticsearch.Pds4JsonSearchRequestBuilder;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntityProduct;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntitytProductWithBlob;
import gov.nasa.pds.api.engineering.exceptions.UnsupportedElasticSearchProperty;
import gov.nasa.pds.api.model.xml.ProductWithXmlLabel;
import gov.nasa.pds.model.Pds4Product;
import gov.nasa.pds.model.Pds4Products;
import gov.nasa.pds.model.Product;
import gov.nasa.pds.model.PropertyArrayValues;
import gov.nasa.pds.model.Summary;
import gov.nasa.pds.api.model.xml.XMLMashallableProperyValue;


public class ProductBusinessObject {
    
    private static final Logger log = LoggerFactory.getLogger(ProductBusinessObject.class);
    
    private static final String DEFAULT_NULL_VALUE = null; 
    
    private ElasticSearchRegistryConnection elasticSearchConnection;
    private ElasticSearchRegistrySearchRequestBuilder searchRequestBuilder;
    private Pds4JsonSearchRequestBuilder pds4SearchRequestBuilder;

    private ObjectMapper objectMapper;
    
    static final String LIDVID_SEPARATOR = "::";

    private LidVidDAO lidVidDao;
    private BundleDAO bundleDao;
    
    public ProductBusinessObject(ElasticSearchRegistryConnection esRegistryConnection) {
        this.elasticSearchConnection = esRegistryConnection;
        
        this.searchRequestBuilder = new ElasticSearchRegistrySearchRequestBuilder(
                this.elasticSearchConnection.getRegistryIndex(),
                this.elasticSearchConnection.getRegistryRefIndex(),
                this.elasticSearchConnection.getTimeOutSeconds());
        
        this.pds4SearchRequestBuilder = new Pds4JsonSearchRequestBuilder(
                this.elasticSearchConnection.getRegistryIndex(), 
                this.elasticSearchConnection.getTimeOutSeconds());
        
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        lidVidDao = new LidVidDAO(esRegistryConnection);
        bundleDao = new BundleDAO(esRegistryConnection);
    }
    

    public LidVidDAO getLidVidDao()
    {
        return lidVidDao;
    }

    public BundleDAO getBundleDao()
    {
        return bundleDao;
    }
    
    
    public String getLatestLidVidFromLid(String lid) throws IOException,LidVidNotFoundException
    {
        /*
         * if lid is a lidvid then it return the same lidvid if available in the elasticsearch database
         */
            
        lid = !lid.contains(LIDVID_SEPARATOR)?lid+LIDVID_SEPARATOR:lid;
        SearchRequest searchRequest = this.searchRequestBuilder.getSearchProductRequestHasLidVidPrefix(lid);
            
        SearchResponse searchResponse = this.elasticSearchConnection.getRestHighLevelClient().search(searchRequest, 
                RequestOptions.DEFAULT);

        if (searchResponse != null)
        {
            ArrayList<String> lidvids = new ArrayList<String>();
            String lidvid;
            for (SearchHit searchHit : searchResponse.getHits())
            {
                lidvid = (String)searchHit.getSourceAsMap().get("lidvid");;
                lidvids.add(lidvid);                
            }
            Collections.sort(lidvids);

            if (lidvids.size() == 0) throw new LidVidNotFoundException(lid);
            else return lidvids.get(lidvids.size() - 1);
        }
        else throw new LidVidNotFoundException(lid);
    }
       
       
    private static XMLMashallableProperyValue object2PropertyValue(Object o) {
           XMLMashallableProperyValue pv = new XMLMashallableProperyValue();
           
           if (o instanceof List<?>) {
               for (Object p : (List<?>) o) {
                   ((ArrayList<String>)(PropertyArrayValues)pv).add(String.valueOf(p));
               }
               
           }
           else {
               // TODO find a type which make String castable in PropertyValue, 
               // currently I am desperate so I transform String in a List<String>
               ((ArrayList<String>)(PropertyArrayValues)pv).add(String.valueOf(o));            
           }
           
           return pv;
           
       }
       
       
       /**
     * @param sourceAsMap source map coming from elasticSearch
     * @param included_fields, in API syntax, with .
     * @param excluded_fields is ignored is included_fields is not null and not empty, in API syntax
     * @return
     */
    public static Map<String, XMLMashallableProperyValue> getFilteredProperties(
               Map<String, Object> sourceAsMap, // in ES syntax 
               List<String> included_fields,    // in API syntax
               List<String> excluded_fields){   // in API syntax
            
            Map<String, XMLMashallableProperyValue> filteredMapJsonProperties  = new HashMap<String, XMLMashallableProperyValue>();
                        
            if ((included_fields == null) || (included_fields.size() ==0)) {
                
                String apiProperty;
                for (Map.Entry<String, Object> entry : sourceAsMap.entrySet()) {
                    try {
                        apiProperty = ElasticSearchUtil.elasticPropertyToJsonProperty(entry.getKey());
                        if ((excluded_fields == null)
                                || (! excluded_fields.contains(apiProperty)))
                     filteredMapJsonProperties.put(
                             apiProperty, 
                             ProductBusinessObject.object2PropertyValue(entry.getValue())
                             );
                    } catch (UnsupportedElasticSearchProperty e) {
                        log.warn("ElasticSearch property " + entry.getKey() + " is not supported, ignored");
                    }
                }
                
            }
            else {      
                
                String esField;
                for (String field : included_fields) {
                    
                    esField = ElasticSearchUtil.jsonPropertyToElasticProperty(field);
                
                    if (sourceAsMap.containsKey(esField)) {
                        filteredMapJsonProperties.put(
                                field, 
                                ProductBusinessObject.object2PropertyValue(sourceAsMap.get(esField))
                                );
                    }
                    else {
                        filteredMapJsonProperties.put(
                                field, 
                                ProductBusinessObject.object2PropertyValue(ProductBusinessObject.DEFAULT_NULL_VALUE)
                                );
                    }
                    
                }
                    
            }
            
            
            return filteredMapJsonProperties;
                
                
        }
        
       
       public Product getProduct(String lidvid, URL baseURL) throws IOException {
           return this.getProduct(lidvid, baseURL, null);
       }
       
       

       @SuppressWarnings("unchecked")
       public Product getProduct(String lidvid, URL baseURL, @Nullable List<String> fields) throws IOException {

           GetRequest getProductRequest = this.searchRequestBuilder.getGetProductRequest(lidvid, false);
           
           GetResponse getResponse = null;
           
           
           RestHighLevelClient restHighLevelClient = this.elasticSearchConnection.getRestHighLevelClient();
            
           getResponse = restHighLevelClient.get(
                   getProductRequest, 
                   RequestOptions.DEFAULT
                   );
        
            if (getResponse.isExists()) {
                log.info("get response " + getResponse.toString());
                Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
                Map<String, XMLMashallableProperyValue> filteredMapJsonProperties = 
                        ProductBusinessObject.getFilteredProperties(sourceAsMap, fields, null);
                
                EntityProduct entityProduct;
                
                entityProduct = this.objectMapper.convertValue(sourceAsMap, EntityProduct.class);
                
                Product product = ElasticSearchUtil.ESentityProductToAPIProduct(entityProduct, baseURL);
            
                product.setProperties((Map<String, PropertyArrayValues>)(Map<String, ?>)filteredMapJsonProperties);
                
                return product;
               
           } 
            else {
                return null;
            }
       }
       
       
       public ProductWithXmlLabel getProductWithXml(String lidvid, URL baseURL)  throws IOException {
           return this.getProductWithXml(lidvid, baseURL, null);
       }
       
       // TODO make the method more generic by having the name of the class we want to cast the object into instead of a boolean, the code will be more neat also
       @SuppressWarnings("unchecked")
       public ProductWithXmlLabel getProductWithXml(String lidvid, URL baseURL, @Nullable List<String> field) throws IOException {
           
           GetRequest getProductRequest = this.searchRequestBuilder.getGetProductRequest(lidvid, true);
           
           GetResponse getResponse = null;
           
           
           RestHighLevelClient restHighLevelClient = this.elasticSearchConnection.getRestHighLevelClient();
            
           getResponse = restHighLevelClient.get(
                   getProductRequest, 
                   RequestOptions.DEFAULT
                   );
        
            if (getResponse.isExists()) {
                log.info("get response " + getResponse.toString());
                Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
                
                try {
                
                    Map<String, XMLMashallableProperyValue> filteredMapJsonProperties = 
                            ProductBusinessObject.getFilteredProperties(
                                    sourceAsMap, 
                                    null,
                                    new ArrayList<String>(Arrays.asList(ElasticSearchUtil.elasticPropertyToJsonProperty(EntitytProductWithBlob.BLOB_PROPERTY)))
                                    );
                
                    EntitytProductWithBlob entityProduct;
                    
                    entityProduct = this.objectMapper.convertValue(sourceAsMap, EntitytProductWithBlob.class);
                    

                    ProductWithXmlLabel product = ElasticSearchUtil.ESentityProductToAPIProduct(entityProduct, baseURL);            
                    product.setProperties((Map<String, PropertyArrayValues>)(Map<String, ?>)filteredMapJsonProperties);
                
                    return product;
                    
                }
                catch (UnsupportedElasticSearchProperty e) {
                    log.error("This should never happen " + e.getMessage());
                    return null;
                }
                
                
               
           } 
            else {
                return null;
            }
       }
       
       
        public Pds4Product getPds4Product(String lidvid) throws IOException 
        {
            GetRequest req = this.pds4SearchRequestBuilder.getProductRequest(lidvid);
            RestHighLevelClient client = this.elasticSearchConnection.getRestHighLevelClient();           
            GetResponse resp = client.get(req, RequestOptions.DEFAULT);
            
            if(!resp.isExists())
            {
                return null;
            }

            Map<String, Object> fieldMap = resp.getSourceAsMap();
            Pds4Product prod = Pds4JsonProductFactory.createProduct(lidvid, fieldMap);
            return prod;
        }

        
        public Pds4Products getPds4Products(GetProductsRequest req) throws IOException 
        {
            SearchRequest searchRequest = pds4SearchRequestBuilder.getSearchProductsRequest(req);

            SearchResponse searchResponse = elasticSearchConnection.getRestHighLevelClient().search(searchRequest,
                    RequestOptions.DEFAULT);

            Pds4Products products = new Pds4Products();

            // Summary
            Summary summary = new Summary();
            summary.setQ(req.queryString);
            summary.setStart(req.start);
            summary.setLimit(req.limit);
            summary.setSort(req.sort);
            products.setSummary(summary);
            
            if(searchResponse == null) return products;
            
            // Products
            for(SearchHit hit : searchResponse.getHits()) 
            {
                String id = hit.getId();
                Map<String, Object> fieldMap = hit.getSourceAsMap();
                Pds4Product prod = Pds4JsonProductFactory.createProduct(id, fieldMap);
                products.addDataItem(prod);
            }

            return products;
        }

}
