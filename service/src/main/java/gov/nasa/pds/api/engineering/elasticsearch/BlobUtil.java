package gov.nasa.pds.api.engineering.elasticsearch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.zip.InflaterInputStream;

/**
 * Utility class to extract BLOBs stored in Elasticsearch
 * @author karpenko
 */
public class BlobUtil
{
    /**
     * Decompress base64 encoded BLOB.
     * @param blob Base64 encoded compressed BLOB
     * @return Original BLOB value as a string
     * @throws Exception an exception
     */
    public static String blobToString(String blob) throws Exception
    {
        byte[] data = Base64.getDecoder().decode(blob);
        return blobToString(data);
    }
    
    
    /**
     * Decompress binary BLOB.  
     * @param blob compressed BLOB
     * @return Original BLOB value as a string
     * @throws Exception an exception
     */
    public static String blobToString(byte[] blob) throws Exception
    {
        ByteArrayInputStream is = new ByteArrayInputStream(blob);
        // Decompress ("inflate") the BLOB
        InflaterInputStream source = new InflaterInputStream(is);
        
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        copy(source, dest);
        dest.close();
        
        return dest.toString("utf-8");
    }
    
    
    /**
     * Copy data from input to output stream
     * @param source source stream
     * @param dest destination stream
     * @throws Exception an exception
     */
    private static void copy(InputStream source, OutputStream dest) throws Exception
    {
        byte[] buf = new byte[1024];

        int count = 0;
        while((count = source.read(buf)) >= 0)
        {
            dest.write(buf, 0, count);
        }
    }
    
}
