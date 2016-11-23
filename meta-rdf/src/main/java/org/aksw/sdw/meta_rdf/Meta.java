package org.aksw.sdw.meta_rdf;

import java.io.IOException;
import java.util.Properties;

import org.aksw.sdw.meta_rdf.file.metafile.MetaStatementsUnit;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Meta 
{
	public static MetaStatementsUnit readMetaStatementsUnit(String line)
	{
		MetaStatementsUnit mu = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

		try {

			// Convert JSON string from file to Object
			mu = mapper.readValue(line, MetaStatementsUnit.class); 
			//System.out.println(mu);

			//Pretty print
			//String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mu);
			//System.out.println(pretty);
			

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mu;
	}
	
	public static Properties options;
	
	public static boolean shareCompactness()
	{
		return Boolean.parseBoolean(options.getProperty("shareCompactness","false"));
	}
	
	public static boolean metaGroupsAsGraph()
	{
		return Boolean.parseBoolean(options.getProperty("metaGroupsAsGraph","false"));
	}
	
	public static String getDefaultGraph()
	{
		return options.getProperty("defaultGraph", "");
	}
	
}