package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.metrics.ParsedTopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistryConnection;


/**
 * Methods to get latest versions of LIDs
 * @author karpenko
 */
public class LidVidUtils
{
    
    /**
     * Get latest versions of LIDs
     * @param esConnection Elasticsearch connection
     * @param lids list of LIDs
     * @return list of LIDVIDs
     * @throws IOException
     */
    public static List<String> getLatestLids(ElasticSearchRegistryConnection esConnection, Collection<String> lids) throws IOException
    {
        // Create request
        SearchSourceBuilder src = buildGetLatestLidVidsRequest(lids);
        src.timeout(new TimeValue(esConnection.getTimeOutSeconds(), TimeUnit.SECONDS));
        
        SearchRequest esRequest = new SearchRequest(esConnection.getRegistryIndex()).source(src);
        
        // Call Elasticsearch
        RestHighLevelClient client = esConnection.getRestHighLevelClient();
        SearchResponse esResp = client.search(esRequest, RequestOptions.DEFAULT);

        // Parse response
        // (1) Terms aggregation (top level)
        Aggregation agg = esResp.getAggregations().get("lids");
        if(agg == null) return null;
        ParsedStringTerms terms = (ParsedStringTerms)agg;

        List<String> lidvids = new ArrayList<>(lids.size());
        
        for(Bucket buk: terms.getBuckets())
        {
            // (2) Top Hits aggregation (sub-aggregation)
            agg = buk.getAggregations().get("latest");
            if(agg == null) continue;
            
            ParsedTopHits topHits = (ParsedTopHits)agg;
            SearchHit[] hits = topHits.getHits().getHits();
            if(hits != null && hits.length > 0)
            {
                String lidvid = hits[0].getId();
                lidvids.add(lidvid);
            }
        }
        
        return lidvids;
    }
    

    /**
     * Build aggregation query to select latest versions of lids
     * @param lids list of LIDs
     * @return list of LIDVIDs (latest versions)
     */
    public static SearchSourceBuilder buildGetLatestLidVidsRequest(Collection<String> lids)
    {
        if(lids == null || lids.isEmpty()) return null;
        
        SearchSourceBuilder src = new SearchSourceBuilder();
        
        // Query
        src.query(QueryBuilders.termsQuery("lid", lids)).fetchSource(false).size(0);

        // Aggregations
        src.aggregation(AggregationBuilders.terms("lids").field("lid").size(lids.size())
            .subAggregation(AggregationBuilders.topHits("latest").sort(new FieldSortBuilder("vid").order(SortOrder.DESC))
                    .fetchSource(false).size(1))
        );

        return src;
    }
}
