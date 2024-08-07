package gov.nasa.pds.api.registry.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EntityProduct {
  public static final String[] JSON_PROPERTIES = new String[] {"lidvid", "title", "product_class",
      "pds:Time_Coordinates/pds:start_date_time", "pds:Time_Coordinates/pds:stop_date_time",
      "pds:Modification_Detail/pds:modification_date", "pds:File/pds:creation_date_time",
      "ref_lid_instrument_host", "ref_lid_instrument", "ref_lid_investigation", "ref_lid_target",
      "vid", "ops:Label_File_Info/ops:file_ref", "ops:Tracking_Meta/ops:archive_status"};

  @JsonProperty("lidvid")
  private String lidvid;

  @JsonProperty("title")
  private String title;

  @JsonProperty("product_class")
  private String productClass;

  // for compliance with data loaded with version of harvest < 3.7.4
  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  @JsonProperty("pds:Time_Coordinates/pds:start_date_time")
  private List<String> start_date_time;

  // for compliance with data loaded with version of harvest < 3.7.4
  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  @JsonProperty("pds:Time_Coordinates/pds:stop_date_time")
  private List<String> stop_date_time;

  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  @JsonProperty("pds:Modification_Detail/pds:modification_date")
  private List<String> modification_date;

  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  @JsonProperty("pds:File/pds:creation_date_time")
  private List<String> creation_date;

  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  @JsonProperty("ref_lid_instrument_host")
  private List<String> ref_lid_instrument_host = new ArrayList<String>();

  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  @JsonProperty("ref_lid_instrument")
  private List<String> ref_lid_instrument = new ArrayList<String>();

  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  @JsonProperty("ref_lid_investigation")
  private List<String> ref_lid_investigation = new ArrayList<String>();

  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  @JsonProperty("ref_lid_target")
  private List<String> ref_lid_target = new ArrayList<String>();

  @JsonProperty("vid")
  private String version;

  // for compliance with data loaded with version of harvest < 3.7.4
  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  @JsonProperty("ops:Label_File_Info/ops:file_ref")
  private List<String> pds4FileReference;

  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  @JsonProperty("ops:Tracking_Meta/ops:archive_status")
  private List<String> archive_status;

  private Map<String, Object> properties;

  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public String getLidVid() {
    return this.lidvid;
  }

  public String getTitle() {
    return this.title;
  }

  public String getProductClass() {
    return this.productClass;
  }

  public static <T> Iterable<T> emptyIfNull(Iterable<T> iterable) {
    return iterable == null ? Collections.<T>emptyList() : iterable;
  }

  public List<String> getRef_lid_instrument() {
    return ref_lid_instrument;
  }

  public List<String> getRef_lid_instrument_host() {
    return ref_lid_instrument_host;
  }

  public List<String> getRef_lid_investigation() {
    return ref_lid_investigation;
  }

  public List<String> getRef_lid_target() {
    return ref_lid_target;
  }

  private static String getFirstElmentOrNull(List<String> l) {
    return l != null ? l.get(0) : null;
  }

  public String getPDS4FileRef() {
    return EntityProduct.getFirstElmentOrNull(this.pds4FileReference);
  }

  public String getStartDateTime() {
    return EntityProduct.getFirstElmentOrNull(this.start_date_time);
  }

  public String getStopDateTime() {
    return EntityProduct.getFirstElmentOrNull(this.stop_date_time);
  }

  public List<String> getModificationDate() {
    return modification_date;
  }

  public List<String> getCreationDate() {
    return creation_date;
  }

  public String getVersion() {
    return version;
  }

  public List<String> getArchiveStatus() {
    return this.archive_status;
  }
}
