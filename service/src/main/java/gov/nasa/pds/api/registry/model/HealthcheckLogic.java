package gov.nasa.pds.api.registry.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.opensearch.rest.RestStatus;
import org.opensearch.client.core.CountRequest;
import org.opensearch.client.core.CountResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.action.search.ShardSearchFailure;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import com.google.errorprone.annotations.Immutable;

import gov.nasa.pds.api.registry.ConnectionContext;
import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.exceptions.NothingFoundException;
import gov.nasa.pds.api.registry.model.identifiers.LidVidUtils;

@Immutable
public class HealthcheckLogic {

  public static String DOC_COUNT = "registry_document_count";
  public static String TOTAL_SHARDS = "total_shards";
  public static String SUCCESSFUL_SHARDS = "successful_shards";
  public static String FAILED_SHARDS = "failed_shards";
  public static String SKIPPED_SHARDS = "skipped_shards";
  public static String FREE_MEMORY = "free_memory";
  public static String MAX_MEMORY = "max_memory";
  public static String TOTAL_MEMORY = "total_memory";
  public static String FAILURE_MESSAGES = "failure_messages";
  public static String FAILURES_PRESENT = "failures_present";

  private static final Logger log = LoggerFactory.getLogger(HealthcheckLogic.class);

  ControlContext control;

  public HealthcheckLogic(ControlContext control) {
    this.control = control;
  }

  public Map<String, Object> healthcheck() {
    Map<String, Object> response = new HashMap<>();
    response.put(FAILURES_PRESENT, false);

    getDocumentCount(response);
    getJvmStats(response);

    return response;
  }

  private void getDocumentCount(Map<String, Object> response) {
    try {
      CountRequest countRequest = new CountRequest();
      countRequest.indices(this.control.getConnection().getRegistryIndex());

      countRequest.query(QueryBuilders.matchAllQuery());

      CountResponse countResponse = control.getConnection().getRestHighLevelClient().count(countRequest, RequestOptions.DEFAULT);
      RestStatus countResponseStatus = countResponse.status();
      if (countResponseStatus != RestStatus.OK) {
        addFailureMessage(response, String.format("Opensearch count request failure [%d]", countResponseStatus));
      }
      response.put(TOTAL_SHARDS, countResponse.getTotalShards());
      response.put(SKIPPED_SHARDS, countResponse.getSkippedShards());
      response.put(SUCCESSFUL_SHARDS, countResponse.getSuccessfulShards());
      int failedShards = countResponse.getFailedShards();
      response.put(FAILED_SHARDS, failedShards);
      if (failedShards > 0) {
           addFailureMessage(response, String.format("Opensarch shard failures are >0 (%d)", failedShards));
      }
      for (ShardSearchFailure failure : countResponse.getShardFailures()) {
         addFailureMessage(response, String.format("Shard search failure [%s]", failure.toString()));
      }
      response.put(DOC_COUNT, countResponse.getCount());
    }
    catch(Exception ex) {
      addFailureMessage(response, String.format("Opensearch request failure [%s]", ex.toString()));
    }
  }

  private void addFailureMessage(Map<String,Object> response, String message) {
    List<String> messageList = (ArrayList<String>) response.getOrDefault(FAILURE_MESSAGES, new ArrayList<String>());
    messageList.add(message);
    response.put(FAILURE_MESSAGES, messageList);
    response.put(FAILURES_PRESENT, true);
  }

  private void getJvmStats(Map<String, Object> response) {
    Runtime jvmRuntime = Runtime.getRuntime();
    response.put(FREE_MEMORY, jvmRuntime.freeMemory());
    response.put(MAX_MEMORY, jvmRuntime.maxMemory());
    response.put(TOTAL_MEMORY, jvmRuntime.totalMemory());
  }

}
