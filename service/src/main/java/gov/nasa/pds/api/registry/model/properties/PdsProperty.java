package gov.nasa.pds.api.registry.model.properties;

import gov.nasa.pds.api.registry.exceptions.UnsupportedSearchProperty;
import gov.nasa.pds.api.registry.model.exceptions.UnhandledException;

import static gov.nasa.pds.api.registry.model.SearchUtil.jsonPropertyToOpenProperty;
import static gov.nasa.pds.api.registry.model.SearchUtil.openPropertyToJsonProperty;

/**
 * Provides a unified interface for dealing with PDS product properties, which are named differently depending on the
 * context.  The canonical names used by OpenSearch use '/' as a separator, whereas users and the API URL interface
 * replace these with "."
 */
public class PdsProperty {
    private final String value;

    public PdsProperty(String value) {
        this.value = value;
    }

    /**
     * Returns the property, in the format used by OpenSearch
     */
    public static String toOpenPropertyString(String value) {
        // TODO: replace all uses of SearchUtil.jsonPropertyToOpenProperty with PdsProperty, move conversion logic here,
        //  and excise SearchUtil.jsonPropertyToOpenProperty
        return jsonPropertyToOpenProperty(value);
    }

    public String toOpenPropertyString() {
        return toOpenPropertyString(this.value);
    }

    /**
     * Returns the property, in the format used by the API interface (i.e. end-users)
     */
    public static String toJsonPropertyString(String value) {
        // TODO: replace all uses of SearchUtil.openPropertyToJsonProperty with PdsProperty, move conversion logic here,
        //  and excise SearchUtil.openPropertyToJsonProperty

        //TODO: Remove this stopgap once https://github.com/NASA-PDS/registry-api/issues/528 is resolved
        try{
            return openPropertyToJsonProperty(value);
        } catch (UnsupportedSearchProperty err) {
            return value.replace('/', '.');
        }
    }

    public String toJsonPropertyString() {
        return toJsonPropertyString(this.value);
    }


}
