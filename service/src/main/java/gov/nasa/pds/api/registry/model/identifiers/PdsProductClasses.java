package gov.nasa.pds.api.registry.model.identifiers;

import java.util.Arrays;
import java.util.List;

/**
 * An enumeration of the valid product_class values from the PDS4 Data Dictionary
 * <a href="https://pds.nasa.gov/datastandards/documents/dd/v1/PDS4_PDS_DD_1M00.html#N-1195656387">...</a>
 */
public enum PdsProductClasses {
    Product_AIP("Product_AIP"),
    Product_Ancillary("Product_Ancillary"),
    Product_Attribute_Definition("Product_Attribute_Definition"),
    Product_Browse("Product_Browse"),
    Product_Bundle("Product_Bundle"),
    Product_Class_Definition("Product_Class_Definition"),
    Product_Collection("Product_Collection"),
    Product_Context("Product_Context"),
    Product_DIP("Product_DIP"),
    Product_DIP_Deep_Archive("Product_DIP_Deep_Archive"),
    Product_Data_Set_PDS3("Product_Data_Set_PDS3"),
    Product_Document("Product_Document"),
    Product_External("Product_External"),
    Product_File_Repository("Product_File_Repository"),
    Product_File_Text("Product_File_Text"),
    Product_Instrument_Host_PDS3("Product_Instrument_Host_PDS3"),
    Product_Instrument_PDS3("Product_Instrument_PDS3"),
    Product_Metadata_Supplemental("Product_Metadata_Supplemental"),
    Product_Mission_PDS3("Product_Mission_PDS3"),
    Product_Native("Product_Native"),
    Product_Observational("Product_Observational"),
    Product_Proxy_PDS3("Product_Proxy_PDS3"),
    Product_SIP("Product_SIP"),
    Product_SIP_Deep_Archive("Product_SIP_Deep_Archive"),
    Product_SPICE_Kernel("Product_SPICE_Kernel"),
    Product_Service("Product_Service"),
    Product_Software("Product_Software"),
    Product_Subscription_PDS3("Product_Subscription_PDS3"),
    Product_Target_PDS3("Product_Target_PDS3"),
    Product_Thumbnail("Product_Thumbnail"),
    Product_Update("Product_Update"),
    Product_Volume_PDS3("Product_Volume_PDS3"),
    Product_Volume_Set_PDS3("Product_Volume_Set_PDS3"),
    Product_XML_Schema("Product_XML_Schema"),
    Product_Zipped("Product_Zipped");

    private final String value;

    PdsProductClasses(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * @return the database property/field these string values appear in. Provided for convenience.
     */
    public static String getPropertyName() {
        return "product_class";
    }

    public static List<String> getValidSwaggerNames() {
        return Arrays.stream(PdsProductClasses.values()).map(PdsProductClasses::getSwaggerName).toList();
    }

    /**
     * @return the shorthand name used for convenience in API routes, ex. "Product_Bundle" -> "bundle"
     */
    public String getSwaggerName() {
        List<String> chunks = Arrays.asList(this.value.split("_"));

        if (chunks.size() < 2 || !chunks.get(0).equals("Product")) {
            throw new IllegalArgumentException("Could not generate swagger name for PDSProductClasses value '" + this.value + "'");
        }

        List<String> relevantChunks = chunks.subList(1, chunks.size());

        return String.join("-", relevantChunks).toLowerCase();
    }

    /**
     * @return the PdsProductClass which is equivalent to a given swagger-formatted product class name
     */
    public static PdsProductClasses fromSwaggerName(String swaggerName) {
        List<PdsProductClasses> matches = Arrays.stream(PdsProductClasses.values())
                .filter(value -> value.getSwaggerName().equals(swaggerName))
                .toList();

        if (matches.isEmpty()) {
            throw new IllegalArgumentException("No PdsProductClass matches swagger name '" + swaggerName + "'"
                    + "'. Valid names are: " + String.join(", ", getValidSwaggerNames()));
        }

        if (matches.size() > 1) {
//            This should never be possible and indicates an error in the enum value definitions
            throw new IllegalArgumentException("Multiple PdsProductClass matches swagger name '" + swaggerName + "'");
        }

        return matches.get(0);
    }

    public Boolean isBundle() {
        return this == Product_Bundle;
    }

    public Boolean isCollection() {
        return this == Product_Collection;
    }

    public Boolean isBasicProduct() {
        return !(isBundle() || isCollection());
    }
}
