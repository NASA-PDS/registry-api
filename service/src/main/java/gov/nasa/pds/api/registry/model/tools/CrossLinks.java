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
    private List<Tool> tools;
    public void setTools(List<Tool> t) {
        this.tools = t;
    }
    public List<Tool> getTools() {
        return tools;
    }

    public Object getLinks(HashMap<String, Object> product) {
        // first pull out all the supported metadata from the product
        //String lid = product.get("lid");
        Map<String, Object> values = new HashMap<>();
        String lidvid = "urn:nasa:pds:mars2020_mastcamz_ops_calibrated:data:zlf_1019_0757408892_144ras_n0490000zcam07114_1100lmj::2.0";
        String filename = "NLF_1019_0757408223_941RAD_N0490000NCAM02019_0A0195J01.IMG";
        int filenameLastIndexOf = filename.lastIndexOf('.');
        String filenameWithoutFileExtension = filename.substring(0, filenameLastIndexOf);
        String fileExtension = filename.substring(filenameLastIndexOf + 1);

        values.put("vid", "2.0");
        values.put("lid", "urn:nasa:pds:mars2020_mastcamz_ops_calibrated:data:zlf_1019_0757408892_144ras_n0490000zcam07114_1100lmj");
        values.put("lidvid", lidvid);
        values.put("mission", "Mars2020");
        values.put("spacecraft", "");
        values.put("bundle", lidvid.split(":")[3]);
        values.put("collection", lidvid.split(":")[4]);
        values.put("target", "Mars");
        values.put("filename", filename);
        values.put("filenameWithoutFileExtension", filenameWithoutFileExtension);
        values.put("fileExtension", fileExtension);
        values.put("fileRef", "/mars2020_mastcamz_ops_calibrated/data/sol/01019/ids/rdr/zcam/ZLF_1019_0757408892_144RAS_N0490000ZCAM07114_1100LMJ02.xml");
        values.put("productClass", "Product_Observational");
        values.put("productType", "RAS");
        values.put("nodeName", "PDS_IMG");

        List<Map<String, Object>> response = new ArrayList<>();

        for (Tool t : getTools()) {
            response.add(formToolLink(t, values, product));
        }
        return response;

        //return product;//"lid";
        /*
        vid: properties.vid
        lidvid: properties.lidvid
        mission:  properties.pds:Investigation_Area.pds:name
        spacecraft:
        bundleId:
        target:  properties.pds:Target_Identification.pds:name
        filename:  properties.ops:Data_File_Info.ops:file_name
        filenameWithoutFileExtension
        fileExtension
        fileRef:  properties.ops:Data_File_Info.ops:file_ref
        productClass: properties.productClass
        productType: properties.mgn:Magellan_Parameters.mgn:product_type
        nodeName: properties.ops:Harvest_Info.ops:node_name
        */
    }

    private Map<String, Object> formToolLink(Tool t, Map<String, Object> values, HashMap<String, Object> product) {
        
        Map<String, Object> l = new HashMap<>();
        l.put("tool", t.getName());
        l.put("description", t.getDescription());

        String link = t.getBase();

        for (String field : values.keySet()) {
            String value = (String) values.get(field);

            for( Alias a : t.getAliases()) {
                if( a.getField().equals(field) ) {
                    System.out.println(a.getFrom());
                    if( a.getFrom().contains(value) ) {
                        value = a.getAlias();
                        break;
                    }
                }
            }
            link = link.replaceAll("\\{" + field + "\\}", value);
            
        }

        l.put("link", link);

        return l;
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
        private List<String> supportsLinksFrom;
        public void setSupportsLinksFrom(List<String> s) {
            this.supportsLinksFrom = s;
        }
        public List<String> getSupportsLinksFrom() {
            return supportsLinksFrom;
        }

        private List<Alias> aliases;
        public void setAliases(List<Alias> a) {
            this.aliases = a;
        }
        public List<Alias> getAliases() {
            return aliases;
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


