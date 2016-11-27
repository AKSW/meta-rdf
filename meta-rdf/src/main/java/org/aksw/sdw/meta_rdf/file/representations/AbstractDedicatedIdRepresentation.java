package org.aksw.sdw.meta_rdf.file.representations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.aksw.sdw.meta_rdf.MetaStatement;
import org.aksw.sdw.meta_rdf.MetaStatementsUnitView;
import org.aksw.sdw.meta_rdf.Options;
import org.aksw.sdw.meta_rdf.RdfQuad;
import org.aksw.sdw.meta_rdf.RdfTools;
import org.aksw.sdw.meta_rdf.file.metafile.MetaStatementsUnit;
import org.aksw.sdw.meta_rdf.file.metafile.MetadataFact;
import org.aksw.sdw.meta_rdf.file.metafile.MetadataUnit;
import org.aksw.sdw.meta_rdf.file.metafile.Statement;
import org.aksw.sdw.meta_rdf.file.metafile.StatementsUnit;

public abstract class AbstractDedicatedIdRepresentation extends AbstractRepresentationFormat {
	public static enum statementMode {FLAT_STATEMENTS,GROUPED_STATEMENTS};
	private statementMode mode;
	
	public AbstractDedicatedIdRepresentation(Function<String,String> keyConvert, Function<String,String> valueConvert) {
		super(keyConvert,valueConvert);
	}
	public AbstractDedicatedIdRepresentation() {
		super();
	}
	public AbstractDedicatedIdRepresentation(Function<String, String> keyConvert, Function<String, String> valueConvert, Options options) {
		super(keyConvert, valueConvert, options);
	}
	public AbstractDedicatedIdRepresentation(Options options) {
		super(options);
	}
	
	@Override
	public String getFileExtension()
	{
		return "nq";
	}
	
	@Deprecated
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
		boolean forceSID = options.getForcedSID();
		List<RdfQuad> quads = new LinkedList<>();
		MetaStatementsUnitView muv = new MetaStatementsUnitView(msu);
		
	// process statement groups
		for ( StatementsUnit su : muv.getStatementUnits())
		{
		// check if statement group has a groupID
			String groupUri; boolean hasGroupID;
			if (!su.getGroupid().equals("")) //
			{
				groupUri = RdfTools.removeBrackets(su.getGroupid());
				hasGroupID = true;
			}
			else
			{
				groupUri = "";
				hasGroupID = false;
			}
		// read metadata for group
			List<MetadataUnit> sharedMeta = muv.getMetadataUnitsForStatementsGroup(su);
		// process statements in group
			
			for ( Statement st : su.getStatements()) //process a statement
			{
				boolean hasSid = !st.getSid().equals("");
				String statementUri;
				if (!hasGroupID || forceSID)
					statementUri =  (hasSid) ? RdfTools.removeBrackets(st.getSid()) :"http://sdw.aksw.org/metardf/StatementID#"+UUID.randomUUID().toString()+""; // generate statement identifier if none is provided
				else
					statementUri =  (hasSid) ? RdfTools.removeBrackets(st.getSid()) : groupUri; // explicit sid overrides groupUri

				
			    //if (st.getType()=="triple") //TODO check for tuple type if quads  add the original quad as extra metadata and omit graph in st
			    {	
	    			RdfQuad q = new RdfQuad(st.getTuple()); //add actual statement
	    			addRepresentationForRdfQuad(quads,q,statementUri,muv);
	    			if ( (hasSid && hasGroupID ) || !hasGroupID || forceSID)
	    			{
	    			  // attach the metadata which is specified in the mids field of this statement
	    				processMetadataGroup(muv.getMetadataUnitsForStatement(st),quads,default_graph,statementUri,muv,1);
	    				 
	    				if (forceSID && options.shareCompactness())
	    				{ 
	    					quads.add(new RdfQuad(default_graph,"<"+statementUri+"> "+"<http://sdw.aksw.org/metardf/hasSharedMeta>"+"<"+groupUri+"> ."));  ; 			// .. we use an specific id for the statement group sharing the same metadata   
		    			}																																				// .. instead of attaching the metadata directly
	    				else if (hasGroupID || (forceSID && ! options.shareCompactness()) )
	    				{
	    				  // replicate metadata from group using the SID
	    					processMetadataGroup(sharedMeta,quads,default_graph,statementUri,muv,1);
	    				}	    			
	    			}
			    }	
			}
		// add metadata for statement group only once or add the 'compressed representation' of the shared metadata
			if (!forceSID || (forceSID && options.shareCompactness()))
				processMetadataGroup(sharedMeta,quads,default_graph,groupUri,muv,1);
			
		}
		return quads;
	}
	
	protected void processMetadataGroup(List<MetadataUnit> mus, List<RdfQuad> quads,String graphUri,String statementUri,MetaStatementsUnitView muv, int recursiveDepth)
	{
		if (recursiveDepth>2)
			return;
		
		for (MetadataUnit mu : mus) //retrieve statements
    	{
			boolean hasNested	= !mu.getHasNested().equals("");
			boolean strongGroup =  mu.getGroupType().equals("strong");
			String groupUri = (strongGroup || hasNested) ? RdfTools.removeBrackets(mu.getGroupid()) : default_graph;
			if (hasNested)
			{
				processMetadataGroup(muv.getMetadataUnit(mu.getHasNested()),quads,default_graph,groupUri,muv,recursiveDepth+1);
			}
			for (MetadataFact mf : mu.getMetadataFacts())
			{
				String ttl = "<"+RdfTools.removeBrackets(statementUri)+"> "+keyConvert.apply(mf.getKey())+ valueConvert.apply((mf.getValue()))+".";
				RdfQuad q2 = new RdfQuad(default_graph,ttl);
				if (strongGroup || hasNested)
					addRepresentationForRdfQuad(quads,q2,groupUri,muv);
				else
					quads.add(q2);
					
    		}
    	}
	}
	
	abstract protected void addRepresentationForRdfQuad(List<RdfQuad> quads, RdfQuad q, String stmtUri, MetaStatementsUnitView muv);
	
}
