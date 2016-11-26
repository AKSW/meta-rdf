
package org.aksw.sdw.meta_rdf.file.representations;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.aksw.sdw.meta_rdf.MetaStatementsUnitView;
import org.aksw.sdw.meta_rdf.Options;
import org.aksw.sdw.meta_rdf.RdfQuad;

public class SingletonPropertyRepresentation extends AbstractTripleIdBasedRepresentation {
	
	public SingletonPropertyRepresentation(Function<String,String> keyConvert, Function<String,String> valueConvert) {
		super(keyConvert,valueConvert);
	}
	public SingletonPropertyRepresentation() {
		super();
	}
	public SingletonPropertyRepresentation(Function<String, String> keyConvert, Function<String, String> valueConvert, Options options) {
		super(keyConvert, valueConvert, options);
	}
	public SingletonPropertyRepresentation(Options options) {
		super(options);
	}
	

	@Override
	protected String generateSidForQuad(RdfQuad q)
	{
		String predicate=q.getPredicate();
		return predicate+"-"+UUID.randomUUID().toString(); 	// generate  singleton property statement identifier for meta triple
	}
	
	@Override
	protected void addRepresentationForRdfQuad(List<RdfQuad> quads, RdfQuad q, String stmtUri,MetaStatementsUnitView muv)
	{
		String predicate=q.getPredicate();
		///String metaStatementUri = predicate+"-"+UUID.randomUUID().toString(); 	// generate  singleton property statement identifier for meta triple
		q.setPredicate(stmtUri);
		quads.add(q); 
		quads.add(new RdfQuad(default_graph,"<"+stmtUri+">"+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#singletonPropertyOf>"+" <"+predicate+"> .")); 		
	}
	
}
