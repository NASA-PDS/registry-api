package gov.nasa.pds.api.engineering.elasticsearch;

import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;


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
