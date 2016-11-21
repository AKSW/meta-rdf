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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public class RdrStatement extends RdfQuad
{
	RdfStatement s;
	RdrStatement innerStatement;
	String property;
	String object;
	
//	public RdrStatement(RdrStatement innerStatement, String property, String object)
//	{
//		this.innerStatement=innerStatement;
//		this.property = property;
//		this.object = object;
//	}
	
	public RdrStatement(RdfStatement s, String property, String object) 
	{
		if(s instanceof RdrStatement) //that is really dirty because of the recursive definition and inheritance from RdfQuad/RdfStatement
		{
			this.innerStatement=(RdrStatement) s;
			this.property = property;
			this.object = object;
		}
		else
		{
			this.s = s;
			this.property = property;
			this.object = object;
		}
	}
	
	public boolean isRecursiveStatement()
	{
		if (innerStatement==null)
			return false;
		else
			return true;
	}
	
	public String getAsRdr()
	{
		if(isRecursiveStatement())
			return "<<"+innerStatement.getAsRdr()+" >> "+property+" "+object;	
		else
		{
			String nt = s.getAsNt();
			nt = nt.substring(0,nt.length()-2);
			return "<<"+nt+" >> "+property+" "+object;
		}		
	}
	
	@Override
	public String getAsNq()
	{
		return getAsRdr()+" .\n";
	}	
	
	
	
}
