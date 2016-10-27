package org.aksw.sdw.meta_rdf;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

public class GraphRepresentation {
	public static enum statementMode {FLAT_STATEMENTS,GROUPED_STATEMENTS};
	private statementMode mode;
	String default_graph = "<http://default.de/>"; //TODO read from properties file
	Function<String,String> keyConvert;
	Function<String,String> valueConvert;
	
	public GraphRepresentation(statementMode mode,Function<String,String> keyConvert, Function<String,String> valueConvert) {
		this.mode=mode;   this.keyConvert=keyConvert  ; this.valueConvert= valueConvert;
	}
	
	public GraphRepresentation(statementMode mode)  {
		this.mode=mode;   
		this.keyConvert = 
				(s) -> {
					if (s.startsWith("<") && s.endsWith(">"))
						return s;
					else 
					{
						 String k = null;
						 try {k =  URLEncoder.encode(s, "UTF-8");} catch (UnsupportedEncodingException e) {e.printStackTrace();}
						 return "http://sdw.aksw.org/metardf/Key#"+k;
					}
						
			
				} ;
		this.valueConvert = 
				(s) -> {
					if (s.startsWith("<") && s.endsWith(">"))
						return s;
					if (s.startsWith("\""))
						return s;
					else
						return s.replaceAll("\t", "\\t").replaceAll("\"", "\\\"").replaceAll("\n", "\\n").replaceAll("\r", "\\r");
		};
	}
	
	public Collection<RdfQuad> getStatementRepresentation(MetaStatement m)
	{
		ArrayList<RdfQuad> quads = new ArrayList<>();
		if (mode==statementMode.GROUPED_STATEMENTS)
		{
			String statementUri = "http://sdw.aksw.org/metardf/StatementID#"+UUID.randomUUID().toString(); // generate statement identifier
			for (String key : m.getAllMedadataFactKeys())
			{
				quads.add(new RdfQuad(statementUri,m.getRdfStatement())); // add plain rdf statement with its statement identifier
				for (String fact : m.getMedadataFact(key))
				{
					String ttl = "<"+statementUri+"> "+keyConvert.apply(key) + valueConvert.apply((fact));	//TODO check if not URI and generate one of it 	
					quads.add(new RdfQuad(default_graph,ttl));
				}
			}
		}
		return quads;
	}
	
	
	public String getGraph()
	{
		return null;
	}
	public String getSubject()
	{
		return null;
	}
	
	public String getPredicate()
	{
		return null;
	}
	
	public String getObject()
	{
		return null;
	}
}
