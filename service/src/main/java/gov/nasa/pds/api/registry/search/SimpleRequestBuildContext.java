package gov.nasa.pds.api.registry.search;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.pds.api.registry.GroupConstraint;
import gov.nasa.pds.api.registry.RequestBuildContext;
import gov.nasa.pds.api.registry.util.GroupConstraintImpl;

class SimpleRequestBuildContext implements RequestBuildContext {
  final private boolean justLatest;
  final private List<String> fields;
  final private GroupConstraint preset;
  final private String node;

  SimpleRequestBuildContext(boolean justLatest) {
    this.fields = new ArrayList<String>();
    this.justLatest = justLatest;
    this.preset = GroupConstraintImpl.empty();
    this.node = "";
  }

  SimpleRequestBuildContext(boolean justLatest, List<String> fields) {
    this.fields = fields;
    this.justLatest = justLatest;
    this.preset = GroupConstraintImpl.empty();
    this.node = "";
  }

  SimpleRequestBuildContext(boolean justLatest, List<String> fields, GroupConstraint preset) {
    this.fields = fields;
    this.justLatest = justLatest;
    this.preset = preset;
    this.node = "";
  }

  SimpleRequestBuildContext(boolean justLatest, List<String> fields, GroupConstraint preset, String node) {
    this.fields = fields;
    this.justLatest = justLatest;
    this.preset = preset;
    this.node = node;
  }

  @Override
  public boolean justLatest() {
    return this.justLatest;
  }

  @Override
  public List<String> getFields() {
    return this.fields;
  }

  @Override
  public GroupConstraint getPresetCriteria() {
    return this.preset;
  }

  @Override
  public String getNode() {
    return this.node;
  }
}
