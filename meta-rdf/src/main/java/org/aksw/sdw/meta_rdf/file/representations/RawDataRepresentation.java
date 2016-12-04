
package org.aksw.sdw.meta_rdf.file.representations;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Function;

import org.aksw.sdw.meta_rdf.MetaStatementsUnitView;
import org.aksw.sdw.meta_rdf.Options;
import org.aksw.sdw.meta_rdf.RdfQuad;
import org.aksw.sdw.meta_rdf.RdfTools;
import org.aksw.sdw.meta_rdf.file.metafile.MetaStatementsUnit;
import org.aksw.sdw.meta_rdf.file.metafile.MetadataFact;
import org.aksw.sdw.meta_rdf.file.metafile.MetadataUnit;
import org.aksw.sdw.meta_rdf.file.metafile.Statement;
import org.aksw.sdw.meta_rdf.file.metafile.StatementsUnit;

public class RawDataRepresentation extends AbstractRepresentationFormat {
	GraphRepresentation gr ;
	
	public RawDataRepresentation(Function<String,String> keyConvert, Function<String,String> valueConvert) {
		super(keyConvert,valueConvert);
		this.gr = new GraphRepresentation(keyConvert, valueConvert, new Options(new Properties()));
	}
	public RawDataRepresentation() {
		super();
		this.gr = new GraphRepresentation(keyConvert, valueConvert, new Options(new Properties()));
	}
	public RawDataRepresentation(Function<String, String> keyConvert, Function<String, String> valueConvert, Options options) {
		super(keyConvert, valueConvert, options);
		this.gr = new GraphRepresentation(keyConvert, valueConvert, new Options(new Properties()));
	}
	public RawDataRepresentation(Options options) {
		super(options);
		this.gr = new GraphRepresentation(keyConvert, valueConvert, new Options(new Properties()));
	}
	
	@Override
	public String getFileExtension()
	{
		return "nq";
	}
	
	@Override
	public Collection<RdfQuad> getRepresenationForUnit(MetaStatementsUnit msu)
	{
		List<RdfQuad> quads = new LinkedList<>();
		MetaStatementsUnitView muv = new MetaStatementsUnitView(msu);
		
	// process statement groups
		for ( StatementsUnit su : muv.getStatementUnits())
		{
		  //process a statement		
			for ( Statement st : su.getStatements()) 
			{
				quads.add(new RdfQuad(st.getTuple()));
			}
		}
		return quads;
	}	
	
}
