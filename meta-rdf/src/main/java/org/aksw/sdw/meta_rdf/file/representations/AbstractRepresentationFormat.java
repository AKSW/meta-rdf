/**
 * 
 */
package org.aksw.sdw.meta_rdf.file.representations;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.aksw.sdw.meta_rdf.Meta;
import org.aksw.sdw.meta_rdf.Options;
import org.aksw.sdw.meta_rdf.RdfQuad;
import org.aksw.sdw.meta_rdf.file.metafile.MetaStatementsUnit;

/**
 * @author 
 *
 */
public abstract class AbstractRepresentationFormat {
	
	protected Function<String,String> keyConvert;
	protected Function<String,String> valueConvert;
	protected Map<RdfQuad,AtomicInteger> deduplicated=new ConcurrentHashMap<>();
	protected String default_graph; //= Meta.getDefaultGraph();
	protected Options options;

	public abstract Collection<RdfQuad> getRepresenationForUnit(MetaStatementsUnit mu);
	public abstract String getFileExtension();
	
	public AbstractRepresentationFormat(Function<String,String> keyConvert, Function<String,String> valueConvert) {
		this();
		this.keyConvert=keyConvert  ; this.valueConvert= valueConvert;	
	}
	
	public AbstractRepresentationFormat(Function<String,String> keyConvert, Function<String,String> valueConvert, Options options) {
		this(keyConvert,valueConvert);
		this.options = new Options(options,this.getClass());
		this.default_graph=this.options.getDefaultGraph();
	}
	
	/** 
	 * uses the default configuration (options) set in Meta.options
	 */
	public AbstractRepresentationFormat() {
		
		this.keyConvert = 
				(s) -> {
					if (s.startsWith("<") && s.endsWith(">"))
						return s;
					else 
					{
						 String k = null;
						 try {k =  URLEncoder.encode(s, "UTF-8");} catch (UnsupportedEncodingException e) {e.printStackTrace();}
						 return "<http://sdw.aksw.org/metardf/Key#"+k+">";
					}
						
			
				} ;
		this.valueConvert = 
				(s) -> {
					if (s.startsWith("<") && s.endsWith(">"))
						return s;
					if (s.startsWith("\""))
						return s;
					else
						return "\""+s.replaceAll("\t", "\\t").replaceAll("\"", "\\\"").replaceAll("\n", "\\n").replaceAll("\r", "\\r")+"\"";
				};
		this.options = new Options(Meta.getOptions(),this.getClass());
		this.default_graph=options.getDefaultGraph();	
	}
	
	public AbstractRepresentationFormat(Options options) {
		this();
		this.options = new Options(options,this.getClass());
		this.default_graph=this.options.getDefaultGraph();
	}
	
	
	public Collection<RdfQuad> getDeduplicated()
	{
		System.out.println("Deduplication statistics for "+this.getClass().toString());
		System.out.println("count\tQuad");
		for (Object key : deduplicated.keySet())
		{
			System.out.print(deduplicated.get(key)+"\t"+key);
		} 
		return deduplicated.keySet();
	}
	
	public void addToDeduplicated(RdfQuad q)
	{
		AtomicInteger i = deduplicated.get(q);
		if (i==null)
			deduplicated.put(q, new AtomicInteger(1));
		else
			i.incrementAndGet();
			
	}
	
	
	public synchronized void writeQuads(Collection<RdfQuad> l, PrintStream ps) //TODO static
	{
		//AbstractRepresentationFormat f = new GraphRepresentation();
		//Collection<RdfQuad> l = f.getRepresenationForUnit(msu);
		RdfQuad.writeQuadsAsNq(l, ps);
		//RDFDataMgr.writeQuads(os, l.iterator());
		//System.out.println(f.getRepresenationForUnit(msu));
		//System.out.println(f.getRepresenationForUnit(msu).size());
	}
}
