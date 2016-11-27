/**
 * 
 */
package org.aksw.sdw.meta_rdf;

import java.util.Properties;

import org.aksw.sdw.meta_rdf.file.representations.AbstractRepresentationFormat;

/**
 * @author kilt
 *
 */
public class Options
{
	private Properties properties;
	
	Class<? extends AbstractRepresentationFormat>  representationsClass = AbstractRepresentationFormat.class;
	
	public Options(Properties options)
	{
		this.properties = options;
	}
	
	/**
	 * 
	 * @param options	the options Object containing parameters for the representation 
	 * @param representationsClass the class for which the option object shall be used (this is used to get ignore specific TODO)
	 */
	public Options(Options options, Class<? extends AbstractRepresentationFormat> representationsClass)
	{
		this(options.properties);
		this.representationsClass = representationsClass;
	}
	
	public boolean shareCompactness()
	{
		return Boolean.parseBoolean(properties.getProperty("shareCompactness","false"));
	}
	
	public boolean metaGroupsAsGraph()
	{
		return Boolean.parseBoolean(properties.getProperty("metaGroupsAsGraph","false"));
	}
	
	public String getDefaultGraph()
	{
		return properties.getProperty("defaultGraph", "");
	}
	
	public Properties getProperties()
	{
		return properties;
	}
	
	public boolean getForcedSID()
	{
		return Boolean.parseBoolean(properties.getProperty("forceSID","false"));
	}
	
	public boolean compStrongGroup()
	{
		return Boolean.parseBoolean(properties.getProperty("compStrongGroup","false"));
	}
	
}
