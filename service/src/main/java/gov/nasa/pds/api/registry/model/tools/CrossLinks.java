package gov.nasa.pds.api.registry.model.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class solves the problem: given this lidvid, what are all the
 * PDS tools and services that can display this product and what are
 * their deep-links?
 * 
 * This class provides a core method, getLinks(product), to map a product's
 * metadata fields into the available deep-links.
 * 
 * The inner datatypes and classes are for storing a jackson/json representation
 * of a cross-link configuration loaded by ./CrossLinksLoader.java
 * 
 * @see gov.nasa.pds.api.registry.controllers.ProductsController#productCrossLinks(String identifier)
 * @see gov.nasa.pds.api.registry.model.tools.CrossLinksLoader
 * @author tariqksoliman
 */
public class CrossLinks {
    /**
     * Unused. cross-links.json. injectableParams is present in the cross-links.json configuration
     * merely as a convenience so that writers of it can quickly see which
     * parameters get {param} replaced in the base urls.  
     */ 
    private String[] injectableParams;
    public void setInjectableParams(String[] i) {
        this.injectableParams = i;
    }
    public String[] getInjectableParams() {
        return injectableParams;
    }

    /**
     * cross-links.json. List of tools configured for cross linking.
     */
    private List<Tool> tools;
    public void setTools(List<Tool> t) {
        this.tools = t;
    }
    public List<Tool> getTools() {
        return tools;
    }

    /**
     * Gets a value from a product's opensearch document.
     * Accounts for values like "[hello,world]" and will return the first
     * element of that parsed-into array.
     * 
     * @param product Map of product's opensearch document/metadata
     * @param field Which field to get
     * @return Value of product[field]. If it's an array, returns first element 
     */
    public String get(HashMap<String, Object> product, String field) {
        Object obj = product.get(field);
        String value = obj != null ? obj.toString() : null;
        if( value != null ) {
            String array[] = value.replace("[", "").replace("]", "").split(",");
            return array[0];
        }
        return "";
    }

    /**
     * Core method. Given a product's opensearch document/metadata, 
     * and given the current pre-loaded cross-links configuration,
     * returns deep-links to all tools that support showing that product.
     * 
     * @param product Map of product's opensearch document/metadata
     * @param field Which field to get
     * @return [{ tooL: , description: , link: }, ...] 
     */
    public Object getLinks(HashMap<String, Object> product) {

        /**
         * First pull out all the supported metadata from the product
         */
        Map<String, Object> values = new HashMap<>();
        String lidvid = this.get(product, "lidvid");
        String filename = this.get(product, "pds:File/pds:file_name");

        // Parse out file extension injectable params from the filename
        int filenameLastIndexOf = -1;
        String filenameWithoutFileExtension = "";
        String fileExtension = "";
        if( filename != null ) {
            filenameLastIndexOf = filename.lastIndexOf('.');
            if( filenameLastIndexOf > -1 ) {
                filenameWithoutFileExtension = filename.substring(0, filenameLastIndexOf);
                fileExtension = filename.substring(filenameLastIndexOf + 1);
            }
        }

        // Populate the values Map so that we can iterate it later
        values.put("lid", this.get(product, "lid"));
        values.put("vid", this.get(product, "vid"));
        values.put("lidvid", lidvid);
        values.put("mission", this.get(product, "pds:Investigation_Area/pds:name"));
        values.put("spacecraft", this.get(product, "pds:Observing_System/pds:name"));
        values.put("bundle", lidvid.split(":")[3]);
        values.put("collection", lidvid.split(":")[4]);
        values.put("target", this.get(product, "pds:Target_Identification/pds:name"));
        values.put("filename", filename);
        values.put("filenameWithoutFileExtension", filenameWithoutFileExtension);
        values.put("fileExtension", fileExtension);
        values.put("fileRef", this.get(product, "ops:Data_File_Info/ops:file_ref"));
        values.put("productClass", this.get(product, "product_class"));
        values.put("productType", this.get(product, "msn:Mission_Information/msn:product_type_name"));
        values.put("nodeName", this.get(product, "ops:Harvest_Info/ops:node_name"));

        List<Map<String, Object>> response = new ArrayList<>();

        /**
         * For each tool from the configuration, if possible, form a link for it
         */
        for (Tool t : getTools()) {
            Map<String, Object> linkObj = formToolLink(t, values, product);
            if( linkObj != null ) {
                response.add(linkObj);
            }
        }
        return response;
    }

    /**
     * Helper method for getLinks(product)
     * 
     * @see #getLinks(product)
     * 
     * @param t A tool's cross-links configuration 
     * @param values key/values of all injectable params.
     * @param product Map of product's opensearch document/metadata
     * @return { tooL: , description: , link: } 
     */
    private Map<String, Object> formToolLink(Tool t, Map<String, Object> values, HashMap<String, Object> product) {
        
        Map<String, Object> l = new HashMap<>();
        l.put("tool", t.getName());
        l.put("description", t.getDescription());

        /**
         * getBase() gets the base url template from the tool
         * 
         * For example: "https://pds-imaging.jpl.nasa.gov/beta/record?lidvid={lidvid}&mission={mission}"
         */
        String link = t.getBase();
        
        /**
         * Just because a tool is configured, it does not mean that is always has
         * a deep-link for a given lidvid. We'll accept or reject is as needed below
         */
        boolean accept = false;
        boolean reject = false;
        
        // Iterate each field: lid, vid, lidvid, mission, spacecraft...
        for (String field : values.keySet()) {
            String value = (String) values.get(field);

            // Aliases
            /**
             * aliases are a tool's cross-link configuration that allows mappings to different values.
             * 
             * For example: "Mars2020" may be the value that gets used in the registry for the mission name
             * but a deep-link may require "m20" instead. Thus an alias like the following would support that:
             * {
             *   "field": "mission",
             *   "alias": "m20",
             *   "from": ["mars_2020", "m2020", "mars2020", "Mars2020"]
             * }
             */
            for( Alias a : t.getAliases()) {
                // Only map an alias if the current field matches the alias field
                if( a.getField().equals(field) ) {
                    // Only map if the current value exists in "from"
                    if( a.getFrom().contains(value) ) {
                        // If so use the alias
                        value = a.getAlias();
                        // Note that we do not break here, multiple aliases can be applied to the same field
                    }
                }
            }

            // AcceptOnly
            /**
             * acceptOnly are a tool's cross-link configuration that specifies a hard requirement to be accepted
             * as a working deep-link.
             * 
             * For example: { "field": "mission", "match": "m20" } would accept if values[mission] == "m20"
             * 
             * Note that this works after aliases are applied.
             * Note the acceptOnly is ORed together -- only one acceptOnly needs to match -- not all of them
             */
            for( Accept a : t.getAcceptOnly()) {
                if( a.getField().equals(field) ) {
                    if( value.matches(a.getMatch()) ) {
                        accept = true;
                    }
                }
            }
            // If acceptOnly is an empty List, accept regardless.
            if( t.getAcceptOnly().size() == 0 ) {
                accept = true;
            }

            // Reject
            /**
             * Works just like acceptOnly but if any single match is found, the
             * tool is rejected from being deep-linked to.
             */
            for( Reject a : t.getReject()) {
                if( a.getField().equals(field) ) {
                    if( value.matches(a.getMatch()) ) {
                        reject = true;
                    }
                }
            }

            // Continually replace the next {field} for the final link
            link = link.replaceAll("\\{" + field + "\\}", value);
            
        }
        
        l.put("link", link);
        
        if( accept == true && reject == false ) {
            return l;
        }

        // If it's null, the caller won't add it
        return null;

    }
    
    private static class Tool {
        private String name;
        public void setName(String n) {
            this.name = n;
        }
        public String getName() {
            return name;
        }

        private String base;
        public void setBase(String b) {
            this.base = b;
        }
        public String getBase() {
            return base;
        }

        private String description;
        public void setDescription(String d) {
            this.description = d;
        }
        public String getDescription() {
            return description;
        }

        private List<Alias> aliases;
        public void setAliases(List<Alias> a) {
            this.aliases = a;
        }
        public List<Alias> getAliases() {
            return aliases;
        }


        private List<Accept> acceptOnly;
        public void setAcceptOnly(List<Accept> a) {
            this.acceptOnly = a;
        }
        public List<Accept> getAcceptOnly() {
            return acceptOnly;
        }

        private List<Reject> reject;
        public void setReject(List<Reject> r) {
            this.reject = r;
        }
        public List<Reject> getReject() {
            return reject;
        }
    }

    private static class Alias {
        private String field;
        public void setField(String f) {
            this.field = f;
        }
        public String getField() {
            return field;
        }

        private String alias;
        public void setAlias(String a) {
            this.alias = a;
        }
        public String getAlias() {
            return alias;
        }

        private List<String> from;
        public void setFrom(List<String> f) {
            this.from = f;
        }
        public List<String> getFrom() {
            return from;
        }

    }

    private static class Accept {
        private String field;
        public void setField(String f) {
            this.field = f;
        }
        public String getField() {
            return field;
        }

        private String match;
        public void setMatch(String m) {
            this.match = m;
        }
        public String getMatch() {
            return match;
        }

    }

    private static class Reject {
        private String field;
        public void setField(String f) {
            this.field = f;
        }
        public String getField() {
            return field;
        }

        private String match;
        public void setMatch(String m) {
            this.match = m;
        }
        public String getMatch() {
            return match;
        }

    }
}


