
package org.aksw.sdw.meta_rdf.file.representations;

import java.util.List;
import java.util.function.Function;

import org.aksw.sdw.meta_rdf.MetaStatementsUnitView;
import org.aksw.sdw.meta_rdf.RdfQuad;


public class CompanionPropertyRepresentation extends AbstractTripleIdBasedRepresentation {
	static int tmp_cnt = 0;
	
	
	public CompanionPropertyRepresentation(Function<String,String> keyConvert, Function<String,String> valueConvert) {
		super(keyConvert,valueConvert);
	}
	public CompanionPropertyRepresentation() {
		super();
	}
	
	@Override
	protected void addRepresentationForRdfQuad(List<RdfQuad> quads, RdfQuad q, String stmtUri, MetaStatementsUnitView muv)
	{
		String original_predicate	 = q.getPredicate();
		String companion_property 	 = original_predicate+"."+muv.incrementProcessedCountForPredicatePerSubject(q); 	// generate  companion property statement  for quad
		String companion_id_property = companion_property+".SID";
		q.setPredicate(companion_property);
		quads.add(q); 
		quads.add(new RdfQuad(default_graph,			"<"+q.getSubject()+">"			+" <"+companion_id_property+">"										+	" <"+stmtUri+"> ."));
		
		addToDeduplicated(new RdfQuad(default_graph,	"<"+companion_property+">"		+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#companionPropertyOf>"+	" <"+original_predicate+"> .")); 
		addToDeduplicated(new RdfQuad(default_graph,	"<"+companion_id_property+">"	+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#idPropertyOf>"		+	" <"+companion_property+"> .")); 		
	}
	
}
