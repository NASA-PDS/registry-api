package gov.nasa.pds.api.engineering.elasticsearch;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistrySearchRequestBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.action.search.SearchRequest;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

//@ExtendWith(SpringExtension.class)
//@SpringBootTest
class ElasticSearchRegistrySearchRequestBuilderTest {

	private ElasticSearchRegistrySearchRequestBuilder requestBuilder  = new ElasticSearchRegistrySearchRequestBuilder();; 
	
	public static Map<String, String> queryMap;
	static {
		queryMap = new HashMap<>();
		queryMap.put(null, 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"term\":{\"pds/Identification_Area/pds/product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"pds/Identification_Area/pds/product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("pds/Primary_Result_Summary/pds/processing_level eq \"Raw\"", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"term\":{\"pds/Primary_Result_Summary/pds/processing_level\":{\"value\":\"Raw\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"pds/Identification_Area/pds/product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("pds/Primary_Result_Summary/pds/processing_level ne \"Raw\"", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must_not\":[{\"term\":{\"pds/Primary_Result_Summary/pds/processing_level\":{\"value\":\"Raw\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"pds/Identification_Area/pds/product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("pds/Primary_Result_Summary/pds/processing_level gt \"Partially Processed\"", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"range\":{\"pds/Primary_Result_Summary/pds/processing_level\":{\"from\":\"Partially Processed\",\"to\":null,\"include_lower\":false,\"include_upper\":true,\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"pds/Identification_Area/pds/product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		//queryMap.put("", "");
		//queryMap.put("", "");
		//queryMap.put("", "");
		//queryMap.put("", "");
		//queryMap.put("", "");
		//queryMap.put("", "");
		
	}
	

	
	@Test
	void testGetSearchRequest() {
		
		SearchRequest searchRequest;
		
		for (Entry<String, String> queryEntry : this.queryMap.entrySet()) {
			searchRequest = this.requestBuilder.getSearchCollectionRequest(queryEntry.getKey(), 0, 10); 

			Assertions.assertArrayEquals(searchRequest.toString().toCharArray(), 
					queryEntry.getValue().toCharArray(), 
					"elasticSearch query is " 
							+ searchRequest.toString());
		}
		
	}

}
