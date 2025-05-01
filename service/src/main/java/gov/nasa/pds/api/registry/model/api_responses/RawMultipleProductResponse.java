package gov.nasa.pds.api.registry.model.api_responses;

import java.util.*;
import java.util.stream.Collectors;

import gov.nasa.pds.model.SummaryFacet;
import org.opensearch.client.opensearch.core.SearchResponse;
import gov.nasa.pds.model.Summary;

public class RawMultipleProductResponse {
  private Summary summary;
  private List<Map<String, Object>> products;

  private List<SummaryFacet> extractFacetsFromSearchResponse(SearchResponse<HashMap> searchResponse) {
    List<SummaryFacet> facets = new ArrayList<>();

    searchResponse.aggregations().forEach((propertyName, aggregate) -> {
      if (aggregate.isSterms() || aggregate.isLterms() || aggregate.isDterms()) {
        SummaryFacet facet = new SummaryFacet();
        facet.setType(SummaryFacet.TypeEnum.TERMS);
        facet.setProperty(propertyName);

        if (aggregate.isSterms()) {
          aggregate.sterms().buckets().array().forEach(bucket -> {
            facet.putCountsItem(bucket.key(), Math.toIntExact(bucket.docCount()));
          });
        } else if (aggregate.isDterms()) {
          aggregate.dterms().buckets().array().forEach(bucket -> {
            facet.putCountsItem(String.valueOf(bucket.key()), Math.toIntExact(bucket.docCount()));
          });
        } else if (aggregate.isLterms()) {
          aggregate.lterms().buckets().array().forEach(bucket -> {
            facet.putCountsItem(bucket.key(), Math.toIntExact(bucket.docCount()));
          });
        }

        facets.add(facet);
      }

    });

    return facets;
  }

  public RawMultipleProductResponse(SearchResponse<HashMap> searchResponse) {
    this.summary = new Summary();
    this.summary.setHits((int) searchResponse.hits().total().value());
    this.summary.setFacets(extractFacetsFromSearchResponse(searchResponse));
    this.products = searchResponse.hits().hits().stream().map(p -> (Map<String, Object>) p.source())
        .collect(Collectors.toList());

  }

  public RawMultipleProductResponse(HashMap<String, Object> product) {
    this.summary = new Summary();
    this.summary.setHits(1);
    this.products = new ArrayList<Map<String, Object>>();
    this.products.add(product);

  }



  public Summary getSummary() {
    return summary;
  }

  public List<Map<String, Object>> getProducts() {
    return products;
  }


}
