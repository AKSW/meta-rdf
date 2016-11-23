/**
 * 
 */
package org.aksw.sdw.meta_rdf;

/**
 * @author kilt
 *
 */
public class RdfTools
{
	public static String removeBrackets(String uri)
	{		
		if (uri!=null && !uri.isEmpty() && uri.charAt(0)=='<' && uri.charAt(uri.length()-1)=='>' )
			return uri.substring(1, uri.length()-1);		
		else
			return uri;
	}
}
