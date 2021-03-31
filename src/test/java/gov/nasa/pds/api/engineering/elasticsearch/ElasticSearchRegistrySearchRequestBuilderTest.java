package gov.nasa.pds.api.engineering.elasticsearch;

import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchRegistrySearchRequestBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.action.search.SearchRequest;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

//@ExtendWith(SpringExtension.class)
//@SpringBootTest
class ElasticSearchRegistrySearchRequestBuilderTest {

	private static final Logger log = LoggerFactory.getLogger(ElasticSearchRegistrySearchRequestBuilderTest.class);

	private ElasticSearchRegistrySearchRequestBuilder requestBuilder  = new ElasticSearchRegistrySearchRequestBuilder();; 
	
	public static Map<String, String> queryMap;
	static {
		queryMap = new HashMap<>();
		queryMap.put(null, 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("pds:Primary_Result_Summary/pds:processing_level eq \"Raw\"", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"term\":{\"pds:Primary_Result_Summary/pds:processing_level\":{\"value\":\"Raw\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("pds:Primary_Result_Summary/pds:processing_level ne \"Raw\"", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must_not\":[{\"term\":{\"pds:Primary_Result_Summary/pds:processing_level\":{\"value\":\"Raw\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("pds:Primary_Result_Summary/pds:processing_level gt \"Partially Processed\"", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"range\":{\"pds:Primary_Result_Summary/pds:processing_level\":{\"from\":\"Partially Processed\",\"to\":null,\"include_lower\":false,\"include_upper\":true,\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("pds:Primary_Result_Summary/pds:processing_level lt \"Partially Processed\"", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"range\":{\"pds:Primary_Result_Summary/pds:processing_level\":{\"from\":null,\"to\":\"Partially Processed\",\"include_lower\":true,\"include_upper\":false,\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("pds:Primary_Result_Summary/pds:processing_level ge \"Partially Processed\"", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"range\":{\"pds:Primary_Result_Summary/pds:processing_level\":{\"from\":\"Partially Processed\",\"to\":null,\"include_lower\":true,\"include_upper\":true,\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("pds:Primary_Result_Summary/pds:processing_level le \"Partially Processed\"", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"range\":{\"pds:Primary_Result_Summary/pds:processing_level\":{\"from\":null,\"to\":\"Partially Processed\",\"include_lower\":true,\"include_upper\":true,\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("_file_size eq 8042", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"term\":{\"_file_size\":{\"value\":8042.0,\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("_file_size ne 8042", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must_not\":[{\"term\":{\"_file_size\":{\"value\":8042.0,\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("_file_size gt 8963", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"range\":{\"_file_size\":{\"from\":8963.0,\"to\":null,\"include_lower\":false,\"include_upper\":true,\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("_file_size ge 8963", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"range\":{\"_file_size\":{\"from\":8963.0,\"to\":null,\"include_lower\":true,\"include_upper\":true,\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("_file_size lt 8963", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"range\":{\"_file_size\":{\"from\":null,\"to\":8963.0,\"include_lower\":true,\"include_upper\":false,\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("_file_size le 8963", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"range\":{\"_file_size\":{\"from\":null,\"to\":8963.0,\"include_lower\":true,\"include_upper\":true,\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("(pds:Primary_Result_Summary/pds:processing_level eq \"Raw\")", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"term\":{\"pds:Primary_Result_Summary/pds:processing_level\":{\"value\":\"Raw\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("not (pds:Primary_Result_Summary/pds:processing_level eq \"Raw\")", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must_not\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"term\":{\"pds:Primary_Result_Summary/pds:processing_level\":{\"value\":\"Raw\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("((pds:Primary_Result_Summary/pds:processing_level eq \"Raw\"))", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"term\":{\"pds:Primary_Result_Summary/pds:processing_level\":{\"value\":\"Raw\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("((pds:Primary_Result_Summary/pds:processing_level eq \"Raw\") and (_file_size le 8942))", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"range\":{\"_file_size\":{\"from\":null,\"to\":8942.0,\"include_lower\":true,\"include_upper\":true,\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"term\":{\"pds:Primary_Result_Summary/pds:processing_level\":{\"value\":\"Raw\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");
		queryMap.put("((pds:Primary_Result_Summary/pds:processing_level eq \"Raw\") or (_file_size le 8942))", 
				"SearchRequest{searchType=QUERY_THEN_FETCH, indices=[registry], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source={\"from\":0,\"size\":10,\"timeout\":\"60s\",\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"should\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"range\":{\"_file_size\":{\"from\":null,\"to\":8942.0,\"include_lower\":true,\"include_upper\":true,\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"term\":{\"pds:Primary_Result_Summary/pds:processing_level\":{\"value\":\"Raw\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"minimum_should_match\":\"1\",\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"term\":{\"product_class\":{\"value\":\"Product_Collection\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}}}");	
		
	}
	

	
	@Test
	void testGetSearchRequest() {
		
		String queryString;
		SearchRequest searchRequest;
		
		for (Entry<String, String> queryEntry : this.queryMap.entrySet()) {
			
			queryString = queryEntry.getKey();
			List<String> fields = new ArrayList<String>(Arrays.asList("title","lidvid"));
			searchRequest = this.requestBuilder.getSearchCollectionRequest(
					queryString,
					fields, 0, 10); 

			log.info("{\"" + queryString + "\", \"" + searchRequest + "\"}\n");
			/*
			 * TODO reactivate unit test
			Assertions.assertArrayEquals(
					searchRequest.toString().toCharArray(), 
					queryEntry.getValue().toCharArray(), 
					"elasticSearch query is " 
							+ searchRequest.toString());
			*/
			
		}
		Assertions.assertTrue(false);
		
	}

}
