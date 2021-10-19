package gov.nasa.pds.api.engineering.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.model.Pds4Product;
import gov.nasa.pds.model.Products;


/**
 * Custom serializer to write a Pds4Product in "pds4+json" format.
 * @author karpenko
 */
public class Pds4JsonProductsSerializer extends AbstractHttpMessageConverter<Products>
{
    /**
     * Constructor
     */
    public Pds4JsonProductsSerializer()
    {
        super(new MediaType("application", "pds4+json"));
    }

    
    @Override
    protected boolean supports(Class<?> clazz)
    {
        return Products.class.isAssignableFrom(clazz);
    }

    
    @Override
    protected Products readInternal(Class<? extends Products> clazz, HttpInputMessage msg)
            throws IOException, HttpMessageNotReadableException
    {
        return new Products();
    }

    
    @Override
    public void writeInternal(Products products, HttpOutputMessage msg)
            throws IOException, HttpMessageNotWritableException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        
        OutputStream os = msg.getBody();
        OutputStreamWriter wr = new OutputStreamWriter(os);
        
        wr.write("{\n");
        
        // Summary
        wr.write("\"summary\":");
        String value = mapper.writeValueAsString(products.getSummary());
        wr.write(value);
        wr.write(",\n");
        
        // Data
        wr.write("\"data\":[");
        writeProducts(products.getPds4Json(), wr, mapper);
        wr.write("]\n");
                
        wr.write("}\n");
        wr.close();
    }


    private void writeProducts(List<Pds4Product> list, Writer wr, ObjectMapper mapper) throws IOException
    {
        if(list == null) return;

        int size = list.size();
        for(int i = 0; i < size; i++)
        {
            Pds4Product prod = list.get(i);
            Pds4JsonProductSerializer.writeProduct(prod, wr, mapper);

            if(i < size-1)
            {
                wr.write(",\n");
            }
        }
    }
    
}
