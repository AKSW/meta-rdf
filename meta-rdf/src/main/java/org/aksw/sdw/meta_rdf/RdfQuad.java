package org.aksw.sdw.meta_rdf;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.Quad;



public class RdfQuad extends RdfStatement
{
	private Node graph;
	
	RdfQuad(String NQuad)
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
	
	public RdfQuad(String graph, RdfStatement s)
	{
		this.t=s.t;
		this.graph= NodeFactory.createURI(graph);
	}
	
}
