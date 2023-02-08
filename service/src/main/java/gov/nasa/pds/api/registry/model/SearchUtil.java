package gov.nasa.pds.api.registry.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.nasa.pds.api.registry.exceptions.UnsupportedSearchProperty;
import gov.nasa.pds.model.Metadata;
import gov.nasa.pds.model.PdsProduct;
import gov.nasa.pds.model.Reference;

public class SearchUtil {

  private static final Logger log = LoggerFactory.getLogger(SearchUtil.class);

  static public String jsonPropertyToOpenProperty(String jsonProperty) {
    return jsonProperty.replace(".", "/");
  }

  static public String[] jsonPropertyToOpenProperty(String[] jsonProperties) {
    if (jsonProperties != null && jsonProperties.length > 0) {
      for (int i = 0; i < jsonProperties.length; i++) {
        jsonProperties[i] = jsonPropertyToOpenProperty(jsonProperties[i]);
      }
    }
    return jsonProperties;
  }

  static public List<String> jsonPropertyToOpenProperty(List<String> jsonProperties) {
    if (jsonProperties != null && jsonProperties.size() > 0) {
      for (int i = 0; i < jsonProperties.size(); i++) {
        jsonProperties.set(i, jsonPropertyToOpenProperty(jsonProperties.get(i)));
      }
    }
    return jsonProperties;
  }

  static public String openPropertyToJsonProperty(String openProperty)
      throws UnsupportedSearchProperty {

    return openProperty.replace('/', '.');
  }

  static private void addReference(ArrayList<Reference> to, String ID, URL baseURL) {
    Reference reference = new Reference();
    reference.setId(ID);

    String spec = "/products/" + reference.getId();

    try {

      URIBuilder uriBuilder = new URIBuilder(baseURL.toURI());

      log.debug("Reference base path is: " + uriBuilder.getPath());
      if (uriBuilder.getPath() == null) {
        uriBuilder.setPath(spec);
      } else {
        uriBuilder.setPath(uriBuilder.getPath() + spec);
      }

      URI uri = uriBuilder.build().normalize();

      log.debug("Reference path is: " + uri.toString());
      reference.setHref(uri.toString());

    } catch (URISyntaxException e) {
      log.warn("Unable to create external URL for reference ");
      e.printStackTrace();
      reference.setHref(spec);
    }

    to.add(reference);
  }


  static private PdsProduct addPropertiesFromESEntity(PdsProduct product, EntityProduct ep,
      URL baseURL) {
    product.setId(ep.getLidVid());
    product.setType(ep.getProductClass());

    String title = ep.getTitle();
    if (title != null) {
      product.setTitle(ep.getTitle());
    }

    String startDateTime = ep.getStartDateTime();
    if (startDateTime != null) {
      product.setStartDateTime(startDateTime);
    }

    String stopDateTime = ep.getStopDateTime();
    if (stopDateTime != null) {
      product.setStopDateTime(ep.getStopDateTime());
    }

    ArrayList<Reference> investigations = new ArrayList<Reference>();
    ArrayList<Reference> observationSystemComponent = new ArrayList<Reference>();
    ArrayList<Reference> targets = new ArrayList<Reference>();
    Metadata meta = new Metadata();
    // String baseURL = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

    String version = ep.getVersion();
    if (version != null) {
      meta.setVersion(ep.getVersion());
    }

    List<String> creationDateTime = ep.getCreationDate();
    if (creationDateTime != null && !creationDateTime.isEmpty()) {
      meta.setCreationDateTime(creationDateTime.get(0));
    }

    List<String> updateDateTime = ep.getModificationDate();
    if (updateDateTime != null && !updateDateTime.isEmpty()) {
      // TODO check which modification time to use when there are more than one
      meta.setUpdateDateTime(updateDateTime.get(0));
    }

    String labelUrl = ep.getPDS4FileRef();
    if (labelUrl != null) {
      meta.setLabelUrl(labelUrl);
    }

    for (String id : ep.getRef_lid_instrument_host()) {
      SearchUtil.addReference(observationSystemComponent, id, baseURL);
    }
    for (String id : ep.getRef_lid_instrument()) {
      SearchUtil.addReference(observationSystemComponent, id, baseURL);
    }
    for (String id : ep.getRef_lid_investigation()) {
      SearchUtil.addReference(investigations, id, baseURL);
    }
    for (String id : ep.getRef_lid_target()) {
      SearchUtil.addReference(targets, id, baseURL);
    }

    product.setInvestigations(investigations);
    product.setMetadata(meta);
    product.setObservingSystemComponents(observationSystemComponent);
    product.setTargets(targets);

    return product;
  }

  static public PdsProduct entityProductToAPIProduct(EntityProduct ep, URL baseURL) {
    log.debug("convert EntityProduct (ep) to API object without XML label");

    PdsProduct product = new PdsProduct();

    return addPropertiesFromESEntity(product, ep, baseURL);
  }
}
