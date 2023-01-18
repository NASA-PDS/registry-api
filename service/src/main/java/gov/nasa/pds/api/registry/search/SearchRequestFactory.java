package gov.nasa.pds.api.registry.search;

import java.util.List;
import java.util.Map;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.registry.ConnectionContext;
import gov.nasa.pds.api.registry.RequestBuildContext;
import gov.nasa.pds.api.registry.RequestConstructionContext;
import gov.nasa.pds.api.registry.model.BlobUtil;
import gov.nasa.pds.api.registry.model.LidVidUtils;
import gov.nasa.pds.api.registry.model.ProductQueryBuilderUtil;

public class SearchRequestFactory
{
    private static final Logger log = LoggerFactory.getLogger(SearchRequestFactory.class);
    final private BoolQueryBuilder base = QueryBuilders.boolQuery();
    final private ConnectionContext regContext;

    public SearchRequestFactory(RequestConstructionContext context, ConnectionContext registry)
    {
    	this.regContext = registry;

    	if (!context.getKeyValuePairs().isEmpty())
    	{
    		for (Map.Entry<String, List<String>> entry: context.getKeyValuePairs().entrySet())
    		{
    			if (context.isTerm())
    			{
    				if (entry.getValue().size() == 1)
    				{ this.base.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue().get(0))); }
    				else
    				{ this.base.filter(QueryBuilders.termsQuery(entry.getKey(), entry.getValue())); }
    			}
    			else
    			{
    				if (entry.getValue().size() == 1)
    				{ this.base.must(QueryBuilders.matchQuery(entry.getKey(), entry.getValue().get(0))); }
    				else
    				{
    					for (String value : entry.getValue())
    					{ this.base.filter(QueryBuilders.matchQuery(entry.getKey(), value)); }
    				}
    			}
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
    		String key = LidVidUtils.parseLid(context.getLIDVID()).equals(context.getLIDVID()) ? "lid" : "lidvid";

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
    	if (this.regContext.getRegistryIndex().equals(index))
    	{
    		log.debug("************          Just the latest lidvids: " + Boolean.toString(context.justLatest()));

    		if (context.justLatest())
    		{
    			ProductQueryBuilderUtil.addHistoryStopband (this.base);
    			log.debug("************          created and filled the stopband:" + this.base.mustNot().toString());
    		}

    		ProductQueryBuilderUtil.addArchiveStatusFilter(this.base);
    		ProductQueryBuilderUtil.addPresetCriteria(this.base, context.getPresetCriteria());
    	}

    	return new SearchRequest()
    			.indices(index)
    			.source(new SearchSourceBuilder()
    					.query(this.base)
    					.fetchSource(context.getFields().toArray(new String[0]),
    							     SearchRequestFactory.excludes (context.getFields())));
    }
}
