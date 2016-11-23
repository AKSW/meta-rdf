package org.aksw.sdw.meta_rdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.Quad;



public class RdfQuad extends RdfStatement
{
	private Node graph;
	
	protected RdfQuad()
	{
		
	}
	
	
	public RdfQuad(String NQuad)
	{
		Iterator<Quad> it = RDFDataMgr.createIteratorQuads(new ByteArrayInputStream(NQuad.getBytes(StandardCharsets.UTF_8)), Lang.NQ, null);
		Quad q = it.next();
        this.t= q.asTriple(); this.graph=q.getGraph();
	}
	
	public RdfQuad(String graph, String NTriple)
	{
		super(NTriple);
		this.graph= NodeFactory.createURI(graph);
	}
	
	public RdfQuad(String graph, String NTriple, boolean checkSyntax) // not working
	{
		this(NTriple.substring(0, NTriple.lastIndexOf('.', NTriple.length()-5))+" <"+graph+"> .");
//		if (!checkSyntax)
//		{
//			super(NTriple);
//			this.graph= NodeUtils.asNode(graph);
//		}
//		else
		{
			
		}
		
	}
	
	public RdfQuad(String graph, String subjectUri, String predicateUri, String objectUri, RdfQuad valuesFromQuad) // not working
	{
		Node newGraph 		=  (null==graph) 			? valuesFromQuad.graph 							: NodeFactory.createURI(graph);
		Node newSubject 	=  (null==subjectUri) 		? valuesFromQuad.getAsTriple().getSubject()		: NodeFactory.createURI(subjectUri);
		Node newPredicate 	=  (null==predicateUri) 	? valuesFromQuad.getAsTriple().getPredicate()	: NodeFactory.createURI(predicateUri);
		Node newObject	 	=  (null==objectUri) 		? valuesFromQuad.getAsTriple().getObject()		: NodeFactory.createURI(objectUri);
		this.graph 	= newGraph;
		this.t 		= new Triple(newSubject,	newPredicate, newObject);
	}
	
	public RdfQuad(String graph, RdfStatement s)
	{
		this.t=s.t;
		this.graph= NodeFactory.createURI(graph);
	}
	
	private RdfQuad(Quad q)
	{
		this.t= q.asTriple(); this.graph=q.getGraph();
	}
	
	
	public void addStdReification(String StmtUri, List<RdfQuad> l)
	{
		Node sid = NodeFactory.createURI(StmtUri);
		this.t.getSubject();
		l.add(new RdfQuad(new Quad(this.graph,	sid,	NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),		NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement")		)));
		l.add(new RdfQuad(new Quad(this.graph,	sid,	NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#subject"),	this.t.getSubject()		)));
		l.add(new RdfQuad(new Quad(this.graph,	sid,	NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate"),	this.t.getPredicate()	)));
		l.add(new RdfQuad(new Quad(this.graph,	sid,	NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#object"),		this.t.getObject()		)));
	}
	
	public boolean isTriple()
	{
		return this.graph.toString().isEmpty();
	}
	
	
	private Quad getAsQuad()
	{
		return new Quad(this.graph, this.t); 
	}
	

	@Override
	public String toString()
	{
		return getAsNq();
		//return "RdfQuad [graph=" + graph + ", triple=" + t + "]";
	}
	
	public String getAsNq()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();		
		if (!this.isTriple())
		{
			List<Quad> l = new LinkedList<>();
			l.add(this.getAsQuad());
			RDFDataMgr.writeQuads(baos, l.iterator());
		}
		else
		{
			return getAsNt();
		}		
		try
		{
			return baos.toString(StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			return "# error serializing quad"+e.getMessage();
		}
	}
	
	
	@Deprecated
	public static void writeQuadsAsNq(Collection<RdfQuad> l, OutputStream os)
	{
		for (RdfQuad rdfQuad : l)
		{
			List<Quad> l2 = new LinkedList<>();
			l2.add(rdfQuad.getAsQuad());
			RDFDataMgr.writeQuads(os, l2.iterator());
		}
	}
	
	
	public static void writeQuadsAsNq(Collection<RdfQuad> l, PrintStream os)
	{
		for (RdfQuad rdfQuad : l)
		{
			os.print(rdfQuad.getAsNq());
		}
	}


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((graph == null) ? 0 : graph.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof RdfQuad))
			return false;
		RdfQuad other = (RdfQuad) obj;
		if (graph == null)
		{
			if (other.graph != null)
				return false;
		} else if (!graph.equals(other.graph))
			return false;
		return true;
	}
	
	
}
