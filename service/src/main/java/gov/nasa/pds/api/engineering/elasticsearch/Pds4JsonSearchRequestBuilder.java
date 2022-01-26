package gov.nasa.pds.api.engineering.elasticsearch;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;

import gov.nasa.pds.api.engineering.elasticsearch.business.RequestAndResponseContext;
import gov.nasa.pds.api.engineering.elasticsearch.business.ProductQueryBuilderUtil;


public class Pds4JsonSearchRequestBuilder
{
    private String registryIndex;
    private int timeOutSeconds;

    /**
     * Constructor
     * @param registryIndex Elasticsearch registry index
     * @param registryRefindex Elasticsearch registry refs index
     * @param timeOutSeconds Elasticsearch request timeout
     */
    public Pds4JsonSearchRequestBuilder(String registryIndex, int timeOutSeconds) 
    {
        this.registryIndex = registryIndex;
        this.timeOutSeconds = timeOutSeconds;
    }
    
    
    /**
     * Default construcotr
     */
    public Pds4JsonSearchRequestBuilder() 
    {
        this("registry", 10);
    }

    
    /**
     * Create Elasticsearch request to fetch product by LIDVID. 
     * Get data required to represent the product in "pds4+json" format.
     * @param lidvid LIDVID of a product
     * @return Elasticsearch request
     */
    public GetRequest getProductRequest(String lidvid)
    {
        GetRequest getProductRequest = new GetRequest(this.registryIndex, lidvid);
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, PDS4_JSON_PRODUCT_FIELDS, null);
        getProductRequest.fetchSourceContext(fetchSourceContext);
        return getProductRequest;
    }


    /**
     * Create Elasticsearch request to find products by PDS query or keywords
     * @param req Request parameters
     * @return Elasticsearch request
     */
    public SearchRequest getSearchProductsRequest(RequestAndResponseContext req)
    {
        QueryBuilder query = null;
        
        // "keyword" parameter provided. Run full-text query.
        if(req.getKeyword() != null && !req.getKeyword().isBlank())
        {
            query = ProductQueryBuilderUtil.createKeywordQuery(req.getKeyword(), req.getPresetCriteria());
        }
        // Run PDS query language ("q" parameter) query
        else
        {
            query = ProductQueryBuilderUtil.createPqlQuery(req.getQueryString(), null, req.getPresetCriteria());
        }
        
        SearchRequestBuilder bld = new SearchRequestBuilder(query, req.getStart(), req.getLimit());
        
        if(req.isOnlySummary())
        {
            bld.fetchSource(false, null, null);
        }
        else
        {
            bld.fetchSource(true, {}, null);
        }
        
        bld.setTimeoutSeconds(this.timeOutSeconds);

        SearchRequest searchRequest = bld.build(this.registryIndex);

        return searchRequest;
    }

}
