package org.aksw.sdw.meta_rdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public class RdfStatement 
{
	protected Triple t;
	
	public RdfStatement(String NTriple)
	{
		/*final Model model = ModelFactory.createDefaultModel();
        model.read(new ByteArrayInputStream(NTriple.getBytes(StandardCharsets.UTF_8)), null,"N-TRIPLE");*/
        Iterator<Triple> it = RDFDataMgr.createIteratorTriples(new ByteArrayInputStream(NTriple.getBytes(StandardCharsets.UTF_8)), Lang.NT, null);
        this.t=it.next();
	}
	
	RdfStatement()
	{
		
	}
	
	public void setSubject(String subjectUri)
	{
		Node n = NodeFactory.createURI(subjectUri);
		t = new Triple(t.getSubject(),n,t.getObject());	
	}
	
	public String getSubject()
	{
		return t.getSubject().toString();
	}
	
	public void setPredicate(String predUri)
	{
		//Model m = ModelFactory.createDefaultModel();
		//Property p = m.createProperty(predUri);
		Node n = NodeFactory.createURI(predUri);
		t = new Triple(t.getSubject(),n,t.getObject());	
	}
	
	public String getPredicate()
	{
		return t.getPredicate().toString();
	}
	
	protected Triple getAsTriple()
	{
		return this.t; 
	}
	
	public String getAsNt()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();		
		List<Triple> l = new LinkedList<>();
		l.add(this.getAsTriple());
		RDFDataMgr.writeTriples(baos, l.iterator());
		try
		{
			return baos.toString(StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			return "# error serializing quad"+e.getMessage();
		}
	}

	@Override
	public String toString()
	{
		return getAsNt();
	}	
	
	@Override
	public int hashCode()
	{
		return t.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RdfStatement other = (RdfStatement) obj;
		if (t == null)
		{
			if (other.t != null)
				return false;
		} else if (!t.equals(other.t))
			return false;
		return true;
	}

	
//	public String getObject()
//	{
//		return "";
//	}
//	
//	public boolean isLiteral()
//	{
//		return true;
//	}
	
}
