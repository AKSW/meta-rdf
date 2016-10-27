package org.aksw.sdw.meta_rdf;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public class RdfStatement 
{
	protected Triple t;
	
	RdfStatement(String NTriple)
	{
		/*final Model model = ModelFactory.createDefaultModel();
        model.read(new ByteArrayInputStream(NTriple.getBytes(StandardCharsets.UTF_8)), null,"N-TRIPLE");*/
        Iterator<Triple> it = RDFDataMgr.createIteratorTriples(new ByteArrayInputStream(NTriple.getBytes(StandardCharsets.UTF_8)), Lang.NT, null);
        this.t=it.next();
	}
	
	RdfStatement()
	{
		
	}
	
	public String getSubject()
	{
		return null;
	}
	
	public String getPredicate()
	{
		return "";
	}
	
	public String getObject()
	{
		return "";
	}
	
	public boolean isLiteral()
	{
		return true;
	}
	
}
