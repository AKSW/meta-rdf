
package org.aksw.sdw.meta_rdf.file.representations;

import java.util.List;
import java.util.function.Function;

import org.aksw.sdw.meta_rdf.MetaStatementsUnitView;
import org.aksw.sdw.meta_rdf.Options;
import org.aksw.sdw.meta_rdf.RdfQuad;

public class StandardReificationRepresentation extends AbstractTripleIdBasedRepresentation {

	public StandardReificationRepresentation(Function<String,String> keyConvert, Function<String,String> valueConvert) {
		super(keyConvert,valueConvert);
	}
	public StandardReificationRepresentation() {
		super();
	}
	public StandardReificationRepresentation(Function<String, String> keyConvert, Function<String, String> valueConvert, Options options) {
		super(keyConvert, valueConvert, options);
	}
	public StandardReificationRepresentation(Options options) {
		super(options);
	}
	
	@Override
	protected void addRepresentationForRdfQuad(List<RdfQuad> quads, RdfQuad q, String stmtUri,MetaStatementsUnitView muv)
	{
		q.addStdReification(stmtUri, quads);
	}
	
}
