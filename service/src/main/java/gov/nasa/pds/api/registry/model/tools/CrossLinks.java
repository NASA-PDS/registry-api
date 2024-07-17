package gov.nasa.pds.api.registry.model.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

public class CrossLinks {
    private String[] injectableParams;
    public void setInjectableParams(String[] i) {
        this.injectableParams = i;
    }
    public String[] getInjectableParams() {
        return injectableParams;
    }

    private List<Tool> tools;
    public void setTools(List<Tool> t) {
        this.tools = t;
    }
    public List<Tool> getTools() {
        return tools;
    }

    public String get(HashMap<String, Object> product, String str) {
        Object obj = product.get(str);
        String value = obj != null ? obj.toString() : null;
        if( value != null ) {
            String array[] = value.replace("[", "").replace("]", "").split(",");
            return array[0];
        }
        return "";
    }

    public Object getLinks(HashMap<String, Object> product) {
        // first pull out all the supported metadata from the product

        Map<String, Object> values = new HashMap<>();
        String lidvid = this.get(product, "lidvid");
        String filename = this.get(product, "pds:File/pds:file_name");
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

        values.put("vid", this.get(product, "vid"));
        values.put("lid", this.get(product, "lid"));
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

        for (Tool t : getTools()) {
            Map<String, Object> linkObj = formToolLink(t, values, product);
            if( linkObj != null ) {
                response.add(linkObj);
            }
        }
        return response;
    }

    private Map<String, Object> formToolLink(Tool t, Map<String, Object> values, HashMap<String, Object> product) {
        
        Map<String, Object> l = new HashMap<>();
        l.put("tool", t.getName());
        l.put("description", t.getDescription());

        String link = t.getBase();

        boolean accept = false;
        boolean reject = false;

        for (String field : values.keySet()) {
            String value = (String) values.get(field);

            // Aliases
            for( Alias a : t.getAliases()) {
                if( a.getField().equals(field) ) {
                    if( a.getFrom().contains(value) ) {
                        value = a.getAlias();
                        break;
                    }
                }
            }

            // Accept
            for( Accept a : t.getAcceptOnly()) {
                if( a.getField().equals(field) ) {
                    if( value.matches(a.getMatch()) ) {
                        accept = true;
                    }
                }
            }
            if( t.getAcceptOnly().size() == 0 ) {
                accept = true;
            }

            // Reject
            for( Reject a : t.getReject()) {
                if( a.getField().equals(field) ) {
                    if( value.matches(a.getMatch()) ) {
                        reject = false;
                    }
                }
            }

            link = link.replaceAll("\\{" + field + "\\}", value);
            
        }
        
        l.put("link", link);

        if( accept == true && reject == false ) {
            return l;
        }
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


