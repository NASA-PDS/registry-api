package gov.nasa.pds.api.registry.search;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.fetch.subphase.FetchSourceContext;

import gov.nasa.pds.api.registry.RequestBuildContext;
import gov.nasa.pds.api.registry.RequestConstructionContext;
import gov.nasa.pds.api.registry.business.BlobUtil;
import gov.nasa.pds.api.registry.business.ProductQueryBuilderUtil;

public class SearchRequestBuilder
{
    final private BoolQueryBuilder base = QueryBuilders.boolQuery();
    final private SearchSourceBuilder srcBuilder = new SearchSourceBuilder();
    
    public SearchRequestBuilder(RequestConstructionContext context)
    {
    	if (context.getKeyValuePairs().size() > 0)
    	{
    		for (String key: context.getKeyValuePairs().keySet())
    		if (context.isTerm())
    		{ this.base.must (QueryBuilders.termsQuery(key, context.getKeyValuePairs().get(key))); }
    		else
    		{
    			for (String value : context.getKeyValuePairs().get(key))
    			{ this.base.must(QueryBuilders.matchQuery(key, value)); }
    		}
    	}

    	if (context.getKeywords().size() == 0 && context.getQueryString().length() > 0)
    		this.base.must (ProductQueryBuilderUtil.parseQueryString(context.getQueryString()));

    	if (context.getKeywords().size() > 0)
    	{
    		context.getKeywords().forEach((keyword) ->
    		{ this.base.should (QueryBuilders.queryStringQuery(keyword).field("title").field("description")); });
    	}

    	if (context.getLIDVID().length() > 0)
    	{
    		if (context.isTerm()) this.base.must(QueryBuilders.termQuery("lidvid", context.getLIDVID()));
    		else this.base.must(QueryBuilders.matchQuery("lidvid", context.getLIDVID()));
    	}
    }

    public SearchRequestBuilder(QueryBuilder query, int from, int size)
    {
        srcBuilder.query(query);
        srcBuilder.from(from);
        srcBuilder.size(size);
    }
    
    static private String[] excludes (List<String> fields)
    {
    	String[] exclude, ex0 = new String[0], exbp = {BlobUtil.XML_BLOB_PROPERTY},
                exjbp = {BlobUtil.JSON_BLOB_PROPERTY},
                exall = {BlobUtil.XML_BLOB_PROPERTY, BlobUtil.JSON_BLOB_PROPERTY};

    	if (fields.contains(BlobUtil.XML_BLOB_PROPERTY) && fields.contains(BlobUtil.JSON_BLOB_PROPERTY)) exclude = ex0;
    	else if (fields.contains(BlobUtil.XML_BLOB_PROPERTY)) exclude = exjbp;
    	else if (fields.contains(BlobUtil.JSON_BLOB_PROPERTY)) exclude = exbp;
    	else exclude = exall;

    	return exclude;
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

    public SearchRequest build (RequestBuildContext context, String index)
    {
        ProductQueryBuilderUtil.addArchiveStatusFilter(base);
    	ProductQueryBuilderUtil.addPresetCriteria(base, context.getPresetCriteria());
    	return new SearchRequest().indices(index)
    			.source(new SearchSourceBuilder()
    					.query(this.base)
    					.fetchSource(context.getFields().toArray(new String[0]),
    							     SearchRequestBuilder.excludes (context.getFields())));
    }
}
