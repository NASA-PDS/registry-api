package gov.nasa.pds.api.engineering.elasticsearch;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistrySearchRequestBuilder;
import org.elasticsearch.action.search.SearchRequest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ElasticSearchRegistrySearchRequestBuilderTest {

	private ElasticSearchRegistrySearchRequestBuilder requestBuilder;
	
	private final String SIMPLE_COMPARISON_Q = "pds/Primary_Result_Summary/pds/processing_level eq \"Raw\"";
	private final String SIMPLE_COMPARISON_SREQ = "SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":100,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"term\":{\"pds/Primary_Result_Summary/pds/processing_level\":{\"value\":\"Raw\",\"boost\":1.0}}},{\"term\":{\"pds/Identification_Area/pds/product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}"; 
	
	void setup() {
		this.requestBuilder = new ElasticSearchRegistrySearchRequestBuilder();
	}
	
	@Test
	void testGetSearchRequestSimpleComparison() {
		
		SearchRequest searchRequest = this.requestBuilder.getSearchRequest(this.SIMPLE_COMPARISON_Q, 0, 10); 

		assertEquals(searchRequest.toString(), this.SIMPLE_COMPARISON_SREQ);
	}

}
