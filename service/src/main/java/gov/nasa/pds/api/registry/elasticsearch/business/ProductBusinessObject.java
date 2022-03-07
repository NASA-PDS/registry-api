package gov.nasa.pds.api.registry.elasticsearch.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.model.PropertyArrayValues;
import gov.nasa.pds.api.model.xml.XMLMashallableProperyValue;
import gov.nasa.pds.api.registry.elasticsearch.ElasticSearchRegistryConnection;
import gov.nasa.pds.api.registry.elasticsearch.ElasticSearchRegistrySearchRequestBuilder;
import gov.nasa.pds.api.registry.elasticsearch.ElasticSearchUtil;
import gov.nasa.pds.api.registry.exceptions.UnsupportedElasticSearchProperty;


public class ProductBusinessObject
{
    
    private static final Logger log = LoggerFactory.getLogger(ProductBusinessObject.class);
    
    private static final String DEFAULT_NULL_VALUE = null; 
    
    private ElasticSearchRegistryConnection elasticSearchConnection;
    private ElasticSearchRegistrySearchRequestBuilder searchRequestBuilder;

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
}
