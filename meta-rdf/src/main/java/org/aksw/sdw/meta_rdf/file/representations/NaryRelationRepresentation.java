
package org.aksw.sdw.meta_rdf.file.representations;

import java.util.List;
import java.util.function.Function;

import org.aksw.sdw.meta_rdf.MetaStatementsUnitView;
import org.aksw.sdw.meta_rdf.RdfQuad;

public class NaryRelationRepresentation extends AbstractTripleIdBasedRepresentation {
	
	public NaryRelationRepresentation(Function<String,String> keyConvert, Function<String,String> valueConvert) {
		super(keyConvert,valueConvert);
	}
	public NaryRelationRepresentation() {
		super();
	}	
	
	@Override
	protected void addRepresentationForRdfQuad(List<RdfQuad> quads, RdfQuad q, String stmtUri,MetaStatementsUnitView muv)
	{
		String predicate=q.getPredicate();
		quads.add(new RdfQuad(null,null,null,stmtUri,q)); 								//use graph subject and predicat from orginial quad but use statement identifier as object
		quads.add(new RdfQuad(default_graph,stmtUri,predicate+"-NARY-value",null, q)); 	//copy orginal object to statement node		
	}
	
}
