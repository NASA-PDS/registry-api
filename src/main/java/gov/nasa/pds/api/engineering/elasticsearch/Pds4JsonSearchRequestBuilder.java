package gov.nasa.pds.api.engineering.elasticsearch;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;

import gov.nasa.pds.api.engineering.elasticsearch.business.ProductQueryBuilderUtil;


public class Pds4JsonSearchRequestBuilder
{
    private static final String[] PDS4_JSON_PRODUCT_FIELDS = { 
            // JSON BLOB
            "ops:Label_File_Info/ops:json_blob",
            // Label Metadata
            "ops:Label_File_Info/ops:file_name",
            "ops:Label_File_Info/ops:creation_date_time",
            "ops:Label_File_Info/ops:file_ref",
            "ops:Label_File_Info/ops:file_size",
            "ops:Label_File_Info/ops:md5_checksum",
            // File Metadata
            "ops:Data_File_Info/ops:creation_date_time",
            "ops:Data_File_Info/ops:file_ref",
            "ops:Data_File_Info/ops:file_name",
            "ops:Data_File_Info/ops:file_size",
            "ops:Data_File_Info/ops:md5_checksum",
            "ops:Data_File_Info/ops:mime_type",
            // Node Name
            "ops:Harvest_Info/ops:node_name"
        };

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
    public SearchRequest getSearchProductsRequest(GetProductsRequest req)
    {
        QueryBuilder query = null;
        
        // "keyword" parameter provided. Run full-text query.
        if(req.keyword != null && !req.keyword.isBlank())
        {
            query = ProductQueryBuilderUtil.createKeywordQuery(req.keyword, req.presetCriteria);
        }
        // Run PDS query language ("q" parameter) query
        else
        {
            query = ProductQueryBuilderUtil.createPqlQuery(req.queryString, null, req.presetCriteria);
        }
        
        SearchRequestBuilder bld = new SearchRequestBuilder(query, req.start, req.limit);
        
        if(req.onlySummary)
        {
            bld.fetchSource(false, null, null);
        }
        else
        {
            bld.fetchSource(true, PDS4_JSON_PRODUCT_FIELDS, null);
        }
        
        bld.setTimeoutSeconds(this.timeOutSeconds);

        SearchRequest searchRequest = bld.build(this.registryIndex);

        return searchRequest;
    }

}
