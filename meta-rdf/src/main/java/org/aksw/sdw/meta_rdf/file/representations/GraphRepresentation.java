package org.aksw.sdw.meta_rdf.file.representations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

import org.aksw.sdw.meta_rdf.MetaStatement;
import org.aksw.sdw.meta_rdf.MetaStatementsUnitView;
import org.aksw.sdw.meta_rdf.RdfQuad;
import org.aksw.sdw.meta_rdf.file.metafile.MetaStatementsUnit;
import org.aksw.sdw.meta_rdf.file.metafile.MetadataUnit;
import org.aksw.sdw.meta_rdf.file.metafile.Statement;

public class GraphRepresentation extends AbstractRepresentationFormat {
	public static enum statementMode {FLAT_STATEMENTS,GROUPED_STATEMENTS};
	private statementMode mode;
	String default_graph = "<http://default.de/>"; //TODO read from properties file
	
	public GraphRepresentation(statementMode mode,Function<String,String> keyConvert, Function<String,String> valueConvert) {
		this.mode=mode;   this.keyConvert=keyConvert  ; this.valueConvert= valueConvert;
	}
	
	public Collection<RdfQuad> getStatementRepresentation(MetaStatement m)
	{
		ArrayList<RdfQuad> quads = new ArrayList<>();
		if (mode==statementMode.GROUPED_STATEMENTS)
		{
			String statementUri = "http://sdw.aksw.org/metardf/StatementID#"+UUID.randomUUID().toString(); // generate statement identifier
			for (String key : m.getAllMedadataFactKeys())
			{
				quads.add(new RdfQuad(statementUri,m.getRdfStatement())); // add plain rdf statement with its statement identifier
				for (String fact : m.getMedadataFact(key))
				{
					String ttl = "<"+statementUri+"> "+keyConvert.apply(key) + valueConvert.apply((fact));	//TODO check if not URI and generate one of it 	
					quads.add(new RdfQuad(default_graph,ttl));
				}
			}
		}
		return quads;
	}
	
	@Override
	public Collection<RdfQuad> getRepresenationForUnit(MetaStatementsUnit msu)
	{
		MetaStatementsUnitView muv = new MetaStatementsUnitView(msu);
		for ( Statement st : muv.getStatements()) {
		    if (st.getType()=="triple") //process a statement
		    {
		    	for (MetadataUnit mu : muv.getMetadataUnitsForStatement(st)) {
		    		;
		    	}
		    }
		}
		return null;
	}
	
	
	public String getGraph()
	{
		return null;
	}
	public String getSubject()
	{
		return null;
	}
	
	public String getPredicate()
	{
		return null;
	}
	
	public String getObject()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see org.aksw.sdw.meta_rdf.file.representations.AbstractRepresentationFormat#getDeduplicatedForUnit(org.aksw.sdw.meta_rdf.file.metafile.MetaStatementsUnit)
	 */
	@Override
	public Collection<RdfQuad> getDeduplicatedForUnit(MetaStatementsUnit mu) {
		// TODO Auto-generated method stub
		return null;
	}
}
