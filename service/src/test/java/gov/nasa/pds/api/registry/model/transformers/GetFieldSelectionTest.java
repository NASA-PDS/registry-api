package gov.nasa.pds.api.registry.model.transformers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import gov.nasa.pds.api.registry.model.properties.FieldSelection;
import gov.nasa.pds.api.registry.model.properties.PdsProperty;
import gov.nasa.pds.api.registry.model.properties.PdsPropertyConstants;

/**
 * Tests the properties which each transformer asks OpenSearch to include and exclude.
 */
class GetFieldSelectionTest {

  private static final PdsProperty XML_BLOB = PdsPropertyConstants.XML_BLOB;
  private static final PdsProperty JSON_BLOB = PdsPropertyConstants.JSON_BLOB;
  private static final PdsProperty USER_FIELD = new PdsProperty("pds:Target_Identification.pds:name");

  /** The two ways for a user to not request any specific field. */
  private static final List<List<PdsProperty>> NO_USER_FIELDS =
      Arrays.<List<PdsProperty>>asList(null, List.of());

  /** What a user may request: nothing, a regular field, or fields including both blobs. */
  private static final List<List<PdsProperty>> USER_FIELD_REQUESTS = Arrays
      .<List<PdsProperty>>asList(null, List.of(), List.of(USER_FIELD),
          List.of(USER_FIELD, XML_BLOB, JSON_BLOB));

  @BeforeEach
  void bindRequest() {
    // the transformers read the base URL from the current request when they are instantiated
    RequestContextHolder
        .setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
  }

  @AfterEach
  void unbindRequest() {
    RequestContextHolder.resetRequestAttributes();
  }

  private static Set<PdsProperty> asSet(List<PdsProperty> properties) {
    return new HashSet<>(properties);
  }

  private static Set<PdsProperty> setOf(List<PdsProperty> properties, PdsProperty... others) {
    List<PdsProperty> all = new ArrayList<>(properties);
    all.addAll(List.of(others));
    return new HashSet<>(all);
  }

  // ---- FieldSelection

  @Test
  void fieldSelectionNullListsBecomeEmpty() {
    FieldSelection selection = new FieldSelection(null, null);

    assertTrue(selection.included().isEmpty());
    assertTrue(selection.excluded().isEmpty());
  }

  // ---- PdsProductTransformer

  @Test
  void pdsWithoutUserFieldsFetchesEverythingButBlobs() {
    PdsProductTransformer transformer = new PdsProductTransformer();

    for (List<PdsProperty> noFields : NO_USER_FIELDS) {
      FieldSelection selection = transformer.getFieldSelection(noFields);

      assertTrue(selection.included().isEmpty(), "everything is included");
      assertEquals(Set.of(XML_BLOB, JSON_BLOB), asSet(selection.excluded()));
    }
  }

  @Test
  void pdsWithUserFieldsIncludesRequiredAndUserFieldsAndExcludesBlobs() {
    FieldSelection selection =
        new PdsProductTransformer().getFieldSelection(List.of(USER_FIELD));

    assertEquals(setOf(PdsProductTransformer.REQUIRED_FIELDS, USER_FIELD),
        asSet(selection.included()));
    assertEquals(Set.of(XML_BLOB, JSON_BLOB), asSet(selection.excluded()));
  }

  @Test
  void pdsDoesNotDuplicateUserFieldsAlreadyRequired() {
    // TITLE is part of the required fields
    FieldSelection selection =
        new PdsProductTransformer().getFieldSelection(List.of(PdsPropertyConstants.TITLE));

    assertEquals(asSet(PdsProductTransformer.REQUIRED_FIELDS).size(), selection.included().size());
    assertEquals(asSet(PdsProductTransformer.REQUIRED_FIELDS), asSet(selection.included()));
  }

  @Test
  void pdsDoesNotExcludeABlobExplicitlyRequestedByTheUser() {
    FieldSelection selection =
        new PdsProductTransformer().getFieldSelection(List.of(XML_BLOB));

    assertTrue(selection.included().contains(XML_BLOB));
    assertEquals(Set.of(JSON_BLOB), asSet(selection.excluded()));
  }

  // ---- WyriwygProductTransformer

  @Test
  void wyriwygWithoutUserFieldsFetchesEverythingButBlobs() {
    WyriwygProductTransformer transformer = new WyriwygProductTransformer();

    for (List<PdsProperty> noFields : NO_USER_FIELDS) {
      FieldSelection selection = transformer.getFieldSelection(noFields);

      assertTrue(selection.included().isEmpty(), "everything is included");
      assertEquals(Set.of(XML_BLOB, JSON_BLOB), asSet(selection.excluded()));
    }
  }

  @Test
  void wyriwygWithUserFieldsIncludesOnlyUserFieldsAndExcludesBlobs() {
    FieldSelection selection =
        new WyriwygProductTransformer().getFieldSelection(List.of(USER_FIELD));

    assertEquals(List.of(USER_FIELD), selection.included());
    assertEquals(Set.of(XML_BLOB, JSON_BLOB), asSet(selection.excluded()));
  }

  @Test
  void wyriwygDoesNotExcludeABlobExplicitlyRequestedByTheUser() {
    FieldSelection selection =
        new WyriwygProductTransformer().getFieldSelection(List.of(USER_FIELD, JSON_BLOB));

    assertEquals(Set.of(USER_FIELD, JSON_BLOB), asSet(selection.included()));
    assertEquals(Set.of(XML_BLOB), asSet(selection.excluded()));
  }

  // ---- Pds4JsonProductTransformer and Pds4XmlProductTransformer

  /**
   * The Pds4 transformers do not support user requested fields: the response has no place to
   * return them, so OpenSearch is never asked for them, whatever the user requests. The selection
   * is the required fields plus the blob of the transformer, nothing is excluded.
   */
  private static void assertPds4Selection(FieldSelection selection, PdsProperty expectedBlob,
      PdsProperty otherBlob, List<PdsProperty> userRequestedFields) {
    String message = "user requested fields: " + userRequestedFields;

    assertEquals(setOf(Pds4ProductTransformer.REQUIRED_FIELDS, expectedBlob),
        asSet(selection.included()), message);
    assertFalse(selection.included().contains(USER_FIELD), message);
    assertFalse(selection.included().contains(otherBlob), message);
    assertTrue(selection.excluded().isEmpty(), message);
  }

  @Test
  void pds4JsonSelectionIgnoresUserRequestedFields() {
    for (List<PdsProperty> userFields : USER_FIELD_REQUESTS) {
      FieldSelection selection = new Pds4JsonProductTransformer().getFieldSelection(userFields);

      assertPds4Selection(selection, JSON_BLOB, XML_BLOB, userFields);
    }
  }

  @Test
  void pds4XmlSelectionIgnoresUserRequestedFields() {
    for (List<PdsProperty> userFields : USER_FIELD_REQUESTS) {
      FieldSelection selection = new Pds4XmlProductTransformer().getFieldSelection(userFields);

      assertPds4Selection(selection, XML_BLOB, JSON_BLOB, userFields);
    }
  }
}
