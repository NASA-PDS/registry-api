package gov.nasa.pds.api.registry.search;

import java.util.List;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;

import gov.nasa.pds.api.registry.RequestBuildContext;
import gov.nasa.pds.api.registry.RequestConstructionContext;
import gov.nasa.pds.api.registry.business.BlobUtil;
import gov.nasa.pds.api.registry.business.LidVidUtils;
import gov.nasa.pds.api.registry.business.ProductQueryBuilderUtil;

public class SearchRequestFactory
{
    final private BoolQueryBuilder base = QueryBuilders.boolQuery();
    
    public SearchRequestFactory(RequestConstructionContext context)
    {
    	if (!context.getKeyValuePairs().isEmpty())
    	{
    		for (String key: context.getKeyValuePairs().keySet())
    		if (context.isTerm())
    		{ this.base.must(QueryBuilders.termsQuery(key, context.getKeyValuePairs().get(key))); }
    		else
    		{
    			for (String value : context.getKeyValuePairs().get(key))
    			{ this.base.must(QueryBuilders.matchQuery(key, value)); }
    		}
    	}

    	if (context.getKeywords().isEmpty() && !context.getQueryString().isBlank())
    		this.base.must (ProductQueryBuilderUtil.parseQueryString(context.getQueryString()));

    	if (!context.getKeywords().isEmpty())
    	{
    		context.getKeywords().forEach((keyword) ->
    		{ this.base.must (QueryBuilders.queryStringQuery(keyword).field("title").field("description")); });
    	}

    	if (!context.getLIDVID().isBlank())
    	{
    		String key = LidVidUtils.extractLidFromLidVid(context.getLIDVID()).equals(context.getLIDVID()) ? "lid" : "lidvid";

    		this.base.must(QueryBuilders.termQuery(key, context.getLIDVID())); // term is exact match which lidvid look should be
    	}
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
    
    public SearchRequest build (RequestBuildContext context, String index)
    {
        ProductQueryBuilderUtil.addArchiveStatusFilter(base);
    	ProductQueryBuilderUtil.addPresetCriteria(base, context.getPresetCriteria());
    	return new SearchRequest().indices(index)
    			.source(new SearchSourceBuilder()
    					.query(this.base)
    					.fetchSource(context.getFields().toArray(new String[0]),
    							     SearchRequestFactory.excludes (context.getFields())));
    }
    
    public BoolQueryBuilder getQueryBuilder(RequestBuildContext context)
    { 
        ProductQueryBuilderUtil.addArchiveStatusFilter(base);
    	ProductQueryBuilderUtil.addPresetCriteria(base, context.getPresetCriteria());
    	return this.base;
    }
}
