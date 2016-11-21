
package org.aksw.sdw.meta_rdf.file.representations;

import java.util.List;
import java.util.function.Function;

import org.aksw.sdw.meta_rdf.RdfQuad;

public class StandardReificationRepresentation extends AbstractTripleIdBasedRepresentation {

	public StandardReificationRepresentation(Function<String,String> keyConvert, Function<String,String> valueConvert) {
		super(keyConvert,valueConvert);
	}
	
	public StandardReificationRepresentation() {
		super();
	}
	
	@Override
	protected void addRepresentationForRdfQuad(List<RdfQuad> quads, RdfQuad q, String stmtUri)
	{
		q.addStdReification(stmtUri, quads);
	}
	
}
