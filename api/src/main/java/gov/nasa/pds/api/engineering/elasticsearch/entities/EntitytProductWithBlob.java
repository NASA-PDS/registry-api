package gov.nasa.pds.api.engineering.elasticsearch.entities;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.zip.Inflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EntitytProductWithBlob extends EntityProduct {
	
	private static final Logger log = LoggerFactory.getLogger(EntityProduct.class);

	public static final String BLOB_PROPERTY = "ops:Label_File_Info/ops:blob";
	
	@JsonProperty("ops:Label_File_Info/ops:blob")
	private String fileBlob;
	
	public String getPDS4XML() {
		
		if (this.fileBlob == null) {
    		return "no xml available";
    	}
		
    	Inflater iflr = new Inflater();
    	ByteArrayOutputStream baos = null;
    	
    	byte[] decodedCompressedBytes = Base64.getDecoder().decode(this.fileBlob);
    	
    	iflr.setInput(decodedCompressedBytes);
    	baos = new ByteArrayOutputStream();
        byte[] tmp = new byte[4*1024];
      
        try{
        	
        	EntitytProductWithBlob.log.debug("Read blob from ElasticSearch");
        	
            while(!iflr.finished()){
                int size = iflr.inflate(tmp);
                baos.write(tmp, 0, size);
            }
            
            return baos.toString("utf-8");
            
        } catch (Exception ex){
             
        } finally {
            try{
                if(baos != null) baos.close();
            } catch(Exception ex){}
        }
        
        return "no xml available";
    	
    }



}
