
package org.aksw.sdw.meta_rdf.file.representations;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.aksw.sdw.meta_rdf.Meta;
import org.aksw.sdw.meta_rdf.MetaStatementsUnitView;
import org.aksw.sdw.meta_rdf.RdfQuad;
import org.aksw.sdw.meta_rdf.RdfTools;
import org.aksw.sdw.meta_rdf.file.metafile.MetaStatementsUnit;
import org.aksw.sdw.meta_rdf.file.metafile.MetadataFact;
import org.aksw.sdw.meta_rdf.file.metafile.MetadataUnit;
import org.aksw.sdw.meta_rdf.file.metafile.Statement;
import org.aksw.sdw.meta_rdf.file.metafile.StatementsUnit;

public abstract class AbstractTripleIdBasedRepresentation extends AbstractRepresentationFormat {
	GraphRepresentation gr ;
	
	public AbstractTripleIdBasedRepresentation(Function<String,String> keyConvert, Function<String,String> valueConvert) {
		super(keyConvert,valueConvert);
		this.gr = new GraphRepresentation(keyConvert, valueConvert);
	}
	public AbstractTripleIdBasedRepresentation() {
		super();
		this.gr = new GraphRepresentation();
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
		// check if statement group has a groupID
			String groupUri; boolean hasGroupID;
			if (!su.getGroupid().equals("")) //
			{
				groupUri = RdfTools.removeBrackets(su.getGroupid());
				hasGroupID = true;
			}
			else
			{
				groupUri = "http://sdw.aksw.org/metardf/StatementGroupID#"+UUID.randomUUID().toString();
				hasGroupID = false;
			}
		// read metadata for group
			List<MetadataUnit> sharedMeta = muv.getMetadataUnitsForStatementsGroup(su);
		// process statements in group
			
			for ( Statement st : su.getStatements()) //process a statement
			{
				boolean hasSid = !st.getSid().equals("");
				RdfQuad q = new RdfQuad(st.getTuple()); 
				
				
				String statementUri;
				if (hasSid)														//sid overwrites anything
					statementUri =  RdfTools.removeBrackets(st.getSid());  
				else
					statementUri =  generateSidForQuad(q);
				
				
			    //if (st.getType()=="triple") //TODO 
			    {	 
			      //add actual statement representation	 
			    	addRepresentationForRdfQuad(quads, q, statementUri,muv);
	    			
	    		  //process embedded mids for the statement 
	    			if ( (hasSid && hasGroupID ) || !hasGroupID) //if it has a groupid (strong group is intended as a group where members share same metadata) but it also has an sid OR it is not a strong group (no groupId)
	    			{
	    				processMetadataGroup(muv.getMetadataUnitsForStatement(st),quads,default_graph,statementUri,muv,1); //then treat this triple and its direct metadata individually using SID
	    				
	    				if (hasGroupID) //if it's a strong group then also inherit metadata from the group and add this as well
	    				{
	    					// replicate metadata from group using the SID
	    					processMetadataGroup(sharedMeta,quads,default_graph,statementUri,muv,1);
	    				}
	    			}
	    			else if (Meta.shareCompactness()) 			// if shareCompactness is set .
	    			{
	    				quads.add(new RdfQuad("<"+statementUri+"> "+"<http://sdw.aksw.org/metardf/hasSharedMeta>"+"<"+groupUri+"> ."));  ; 			// .. we use an specific id for the statement group sharing the same metadata   
	    			}																																// .. instead of attaching the metadata directly
	    			else
	    				processMetadataGroup(sharedMeta,quads,default_graph,statementUri,muv,1); // else just do the regular singleton property stuff
			    }
			}
			if (Meta.shareCompactness())
				processMetadataGroup(sharedMeta,quads,default_graph,groupUri,muv,1); // add metadata which applies to the whole group only once
			
		}
		return quads;
	}
	
	
	
	protected void processMetadataGroup(List<MetadataUnit> mus, List<RdfQuad> quads,String graphUri,String statementUri,MetaStatementsUnitView muv, int recursiveDepth)
	{
		
		if(Meta.metaGroupsAsGraph()) //if this is set proceed with the metadata in ngraphs mode 
			gr.processMetadataGroup(mus, quads, graphUri, statementUri, muv, recursiveDepth);
		else
		{
			if (recursiveDepth>2)
				return; 
			
			for (MetadataUnit mu : mus) 
	    	{   
				boolean hasNested	= !mu.getHasNested().equals("");
				boolean strongGroup =  mu.getGroupType().equals("strong");
				
			  // in case it is a nested group the metafacts need to bei reified as well using the nested metadataUnit as meta-metadata
				if (hasNested || strongGroup)
				{	String groupUri =null;
				  //process nested metadata first
					if (hasNested)
					{
						groupUri = RdfTools.removeBrackets(mu.getHasNested()); //TODO what about the fact if you do not use URIs as identifiers
						processMetadataGroup(muv.getMetadataUnit(mu.getHasNested()),quads,default_graph,groupUri,muv,recursiveDepth+1);
					}
					if (strongGroup)
						groupUri = RdfTools.removeBrackets(mu.getGroupid());
				  //now process the meta facts (reifiy them)
					for (MetadataFact mf : mu.getMetadataFacts())
					{
					  //create actual meta fact 
						String ttl = "<"+RdfTools.removeBrackets(statementUri)+"> "+keyConvert.apply(mf.getKey())+ valueConvert.apply((mf.getValue()))+".";	// convert meta fact to triple 
						RdfQuad q = new RdfQuad(default_graph,ttl); 
					  //change meta triple to representation of metafact
						String metaStatementUri = generateSidForQuad(q);
						addRepresentationForRdfQuad(quads,q,metaStatementUri,muv);
		    			
		    		  //add link from statement ids of the meta statement to its groupId to enable to retrieve the nested metadata
		    			if (hasNested)
		    				quads.add(new RdfQuad(default_graph,"<"+metaStatementUri+"> "+"<http://sdw.aksw.org/metardf/hasSharedMeta>"+"<"+groupUri+"> ."));  			// .. we use a specific id for the meta group sharing the same nested metadata   
	    				if (strongGroup)																																// .. instead of attaching the metadata directly to every fact
	    					quads.add(new RdfQuad(default_graph,"<"+metaStatementUri+"> "+"<http://sdw.aksw.org/metardf/belongsToMetaGroup>"+"<"+groupUri+"> ."));  			// .. we use a specific id for the meta meta facts of the strong meta group 
					}
				}
				else
				{
					for (MetadataFact mf : mu.getMetadataFacts())
					{
						String ttl = "<"+RdfTools.removeBrackets(statementUri)+"> "+keyConvert.apply(mf.getKey())+ valueConvert.apply((mf.getValue()))+".";	
						RdfQuad q2 = new RdfQuad(RdfTools.removeBrackets(default_graph),ttl);
						quads.add(q2);
		    		} 
				}
				
	    	}
		}
		
	}
	
	protected String generateSidForQuad(RdfQuad q)
	{
		return "http://sdw.aksw.org/metardf/StatementID#"+UUID.randomUUID().toString(); 	// generate  generic statement identifier 
	}
	
	protected abstract void addRepresentationForRdfQuad(List<RdfQuad> quads, RdfQuad q, String stmtUri, MetaStatementsUnitView muv);	
	
	
	
}
