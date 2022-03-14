package gov.nasa.pds.api.registry.search;

import java.util.concurrent.TimeUnit;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.fetch.subphase.FetchSourceContext;


public class SearchRequestBuilder
{
    private SearchSourceBuilder srcBuilder;
    
    public SearchRequestBuilder(QueryBuilder query, int from, int size)
    {
        srcBuilder = new SearchSourceBuilder();
        srcBuilder.query(query);
        srcBuilder.from(from);
        srcBuilder.size(size);
    }
    
    
    public void fetchSource(boolean fetchSource, String[] includedFields, String[] excludedFields)
    {
        if(fetchSource)
        {
            FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includedFields, excludedFields);
            srcBuilder.fetchSource(fetchSourceContext);
        }
        else
        {
            srcBuilder.fetchSource(false);
        }
    }
    
    
    public void setTimeoutSeconds(int timeOutSeconds)
    {
        srcBuilder.timeout(new TimeValue(timeOutSeconds, TimeUnit.SECONDS));
    }
    
    
    public SearchRequest build(String registryIndex)
    {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(srcBuilder);
        searchRequest.indices(registryIndex);

        return searchRequest;
    }

}
