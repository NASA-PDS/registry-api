package gov.nasa.pds.api.engineering.elasticsearch;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

public class ElasticSearchHitIterator implements Iterable<Map<String,Object>>,Iterator<Map<String,Object>>
{
	private int size=10; // define size to use here to prevent page skipping if elasticsearch default size ever changes
	private int at=0, page=0;
	private SearchHits currentBatch;
	private RestHighLevelClient client;
	private SearchRequest request;
	
	public ElasticSearchHitIterator (RestHighLevelClient client, SearchRequest request) throws IOException
	{
		super();
		this.client = client;
		this.request = request;
		this.currentBatch = this.fetch();
	}

	public ElasticSearchHitIterator (int size, RestHighLevelClient client, SearchRequest request) throws IOException
	{
		super();
		this.client = client;
		this.request = request;
		this.size = size;
		this.currentBatch = this.fetch();
	}

	private SearchHits fetch() throws IOException
	{
		this.request.source().from(this.page * this.size);
		this.request.source().size(this.size);
		return this.client.search (this.request, RequestOptions.DEFAULT).getHits();
	}

	private SearchHit getAt() throws IOException
	{
		if (this.size <= this.at)
		{
			this.page++;
			this.at = 0;
			this.currentBatch = this.fetch();
		}

		return this.currentBatch.getAt(this.at);
	}

	@Override
	public boolean hasNext() { return this.currentBatch == null ? false : (this.at + this.page * this.size) < this.currentBatch.getTotalHits().value; }

	@Override
	public Iterator<Map<String,Object>> iterator() { return this; }

	@Override
	public Map<String,Object> next()
	{
		if (this.hasNext())
		{
			try
			{ 
				SearchHit hit = this.getAt();
				at++;
				return hit.getSourceAsMap();
			}
			catch (IOException ioe) { return null; }
		}
		else { return null; }
	}
}
