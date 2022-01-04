package gov.nasa.pds.api.engineering.serializer;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.model.WyriwygProduct;
import gov.nasa.pds.model.WyriwygProductKeyValuePairs;
import gov.nasa.pds.model.WyriwygProducts;


final class WyriwygSerializer
{
	private static void writeHeader (List<String> labels, Writer wr) throws IOException
	{
		int n=0;
		for (String label : labels)
		{
			if (0 < n) wr.write(",");
			wr.write (label);
			n++;
		}
		wr.write("\n");
	}

	private static void writeJSON(WyriwygProduct product, Writer wr, ObjectMapper mapper, String indent) throws IOException
	{
		int n=0;
		wr.write(indent + "{\n");
		for (WyriwygProductKeyValuePairs kvp : product.getKeyValuePairs())
		{
			if (0 < n) wr.write(",\n");
			wr.write(indent + "  \"" + kvp.getKey() + "\":" + mapper.writeValueAsString(kvp.getValue()));
		}
		wr.write(indent + "}");
	}

	private static void writeRow (List<String> labels, WyriwygProduct product, Writer wr, ObjectMapper om) throws IOException
	{
		HashMap<String,String> row = new HashMap<String,String>();
		int n=0;

		for (WyriwygProductKeyValuePairs kvp : product.getKeyValuePairs()) row.put(kvp.getKey(), kvp.getValue());
		for (String label : labels)
		{
			if (0 < n) wr.write(",");
			if (row.containsKey(label)) wr.write("\"" + row.get(label) + "\"");
			n++;
		}
		wr.write("\n");
	}

	public static void writeCSV(WyriwygProduct product, Writer wr, ObjectMapper mapper) throws IOException
	{
		List<String> labels = new ArrayList<String>();
		
		for (WyriwygProductKeyValuePairs kvp : product.getKeyValuePairs()) labels.add(kvp.getKey());
		Collections.sort(labels);
		WyriwygSerializer.writeHeader (labels, wr);
		WyriwygSerializer.writeRow(labels, product, wr, mapper);
		wr.close();
	}
	
	public static void writeCSV(WyriwygProducts products, Writer wr, ObjectMapper mapper) throws IOException
	{
		Collections.sort(products.getSummary().getProperties());
		WyriwygSerializer.writeHeader (products.getSummary().getProperties(), wr);
		for (WyriwygProduct product : products.getData()) WyriwygSerializer.writeRow(products.getSummary().getProperties(), product, wr, mapper);
		wr.close();
	}

	public static void writeJSON(WyriwygProduct product, Writer wr, ObjectMapper mapper) throws IOException
	{
		WyriwygSerializer.writeJSON(product, wr, mapper, "");
		wr.write("\n");
		wr.close();
	}
	
	public static void writeJSON(WyriwygProducts products, Writer wr, ObjectMapper mapper) throws IOException
	{
		int n=0;
        // Summary
        wr.write("{\n  \"summary\":" + mapper.writeValueAsString(products.getSummary()) + ",\n");
        
        // Data
        wr.write("  \"data\":[");
        for (WyriwygProduct product : products.getData())
		{
			if (0 < n) wr.write(",\n");
			WyriwygSerializer.writeJSON(product, wr, mapper, "    ");
			n++;
		}
        wr.write("\n  ]\n}\n");
        wr.close();
	}
}
