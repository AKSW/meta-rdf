
package org.aksw.sdw.meta_rdf.file.representations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.aksw.sdw.meta_rdf.Meta;
import org.aksw.sdw.meta_rdf.MetaStatementsUnitView;
import org.aksw.sdw.meta_rdf.RdfQuad;
import org.aksw.sdw.meta_rdf.RdfStatement;
import org.aksw.sdw.meta_rdf.RdfTools;
import org.aksw.sdw.meta_rdf.RdrStatement;
import org.aksw.sdw.meta_rdf.file.metafile.MetaStatementsUnit;
import org.aksw.sdw.meta_rdf.file.metafile.MetadataFact;
import org.aksw.sdw.meta_rdf.file.metafile.MetadataUnit;
import org.aksw.sdw.meta_rdf.file.metafile.Statement;
import org.aksw.sdw.meta_rdf.file.metafile.StatementsUnit;


public class RdrRepresentation extends AbstractRepresentationFormat {
	String default_graph = "";//"https://nl.wikipedia.org/wiki/BLØF"; //TODO read from properties file
	GraphRepresentation gr ;
	
	public RdrRepresentation(Function<String,String> keyConvert, Function<String,String> valueConvert) {
		super(keyConvert,valueConvert);
		this.gr = new GraphRepresentation(keyConvert, valueConvert);
	}
	public RdrRepresentation() {
		super();
		this.gr = new GraphRepresentation();
	}
	
	@Override
	public String getFileExtension()
	{
		return "ntx";
	}
	
	@Override
	public Collection<RdfQuad> getRepresenationForUnit(MetaStatementsUnit msu)
	{
		List<RdfQuad> quads = new LinkedList<>();// RdrStatement r = new RdrStatement(new RdfQuad("","<http:/a.de> <http:/b.de> <http:/c.de> ."), "<http:/t.de>", "\"o\""); r = new RdrStatement(r, "<http:/t2.de>", "\"o2\""); quads.add(r); //return quads;
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
//				String predicate= q.getPredicate();
				
				
//				String statementUri;
//				if (hasSid)														//sid overwrites anything
//					statementUri =  RdfTools.removeBrackets(st.getSid());  
//				else
//					statementUri = predicate+"-"+UUID.randomUUID().toString(); 	// generate  singleton property statement identifier 
				
				
			    //if (st.getType()=="triple") //TODO 
			    {
			      //add actual statement with the singleton property
//	    			q.setPredicate(statementUri);
//	    			quads.add(q); 
//	    			quads.add(new RdfQuad(default_graph,"<"+statementUri+">"+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#singletonPropertyOf>"+" <"+predicate+"> .")); 
	    			
	    		  //process embedded mids for the statement 
	    			//if ( (hasSid && hasGroupID ) || !hasGroupID) //if it has a groupid (stron group is intended as a group where members share same metadata) but it also has an sid OR it is not a strong group (no groupId)
	    			{
//	    				processMetadataGroup(muv.getMetadataUnitsForStatement(st),quads,default_graph,statementUri,muv,1); //then treat this triple and its direct metadata individually using SID    				
	    				quads.addAll(processMetadataForStatement(muv.getMetadataUnitsForStatement(st),q,muv,1));
	    				
	    				//if (hasGroupID) //if it's a strong group then also inherit metadata from the group and add this as well
	    				{
	    					// replicate metadata from group using the SID
//	    					processMetadataGroup(sharedMeta,quads,default_graph,statementUri,muv,1);
	    					quads.addAll(processMetadataForStatement(sharedMeta,q,muv,1));
	    					
	    				}
	    			}
//	    			else if (Meta.shareCompactness()) 			// if shareCompactness is set .
//	    			{
//	    				quads.add(new RdfQuad("<"+statementUri+"> "+"<http://sdw.aksw.org/metardf/hasSharedId>"+"<"+groupUri+"> ."));  ; 			// .. we use an specific id for the statement group sharing the same metadata   
//	    			}																																// .. instead of attaching the metadata directly
//	    			else
//	    				processMetadataGroup(sharedMeta,quads,default_graph,statementUri,muv,1); // else just do the regular singleton property stuff
			    }
			}
//			if (Meta.shareCompactness())
//				processMetadataGroup(sharedMeta,quads,default_graph,groupUri,muv,1); // add metadata which applies to the whole group only once
			
		}
		return quads;
	}
	
	protected List<RdrStatement> processMetadataForStatement(List<MetadataUnit> mus, RdfQuad statement,MetaStatementsUnitView muv, int recursiveDepth)
	{
		List<RdrStatement> l = new ArrayList<>();
		if (recursiveDepth>2)
			return l; 
		for (MetadataUnit mu : mus) 
    	{   
			for (MetadataFact mf : mu.getMetadataFacts())
			{
				RdrStatement r = new RdrStatement(statement, 	keyConvert.apply(mf.getKey()),	valueConvert.apply(mf.getValue())	);
				if (!mu.getHasNested().equals(""))
				{	
					l.addAll(processMetadataForStatement(muv.getMetadataUnit(mu.getHasNested()),r,muv,recursiveDepth+1));
//					for (RdrStatement rdrStatement : processMetadataForStatement(muv.getMetadataUnit(mu.getHasNested()),r,muv,recursiveDepth+1))
//					{
//						l.add(new RdrStatement(s, property, object))
//					}
				}
				else
				{
					l.add(r);
				}
					
			}
    	}
		return l;
	}
	
	protected void processMetadataGroup(List<MetadataUnit> mus, List<RdfQuad> quads,String graphUri,String statementUri,MetaStatementsUnitView muv, int recursiveDepth)
	{
		if(Meta.metaGroupsAsGraph()) //if this is set proceed with the metadata in ngraphs mode 
			gr.processMetadataGroup(mus, quads, graphUri, statementUri, muv, recursiveDepth);
		else
		{
			//List<MetadataUnit> recursiveGroups = new LinkedList<MetadataUnit>();
			if (recursiveDepth>2)
				return; //recursiveGroups;
			
			for (MetadataUnit mu : mus) 
	    	{   
				if (!mu.getHasNested().equals(""))
				{	
				  //process nested metadata first
					String groupUri = RdfTools.removeBrackets(mu.getHasNested());
					processMetadataGroup(muv.getMetadataUnit(mu.getHasNested()),quads,default_graph,groupUri,muv,recursiveDepth+1);
				  //now process the meta facts
					for (MetadataFact mf : mu.getMetadataFacts())
					{
					  //create actual meta fact 
						String ttl = "<"+RdfTools.removeBrackets(statementUri)+"> "+keyConvert.apply(mf.getKey())+ valueConvert.apply((mf.getValue()))+".";	// convert meta fact to triple 
						RdfQuad q = new RdfQuad(default_graph,ttl); 
					  //change meta triple to singleton property representation of metafact 
						String predicate=q.getPredicate();
						String metaStatementUri = predicate+"-"+UUID.randomUUID().toString(); 	// generate  singleton property statement identifier for meta triple
						q.setPredicate(metaStatementUri);
		    			quads.add(q); 
		    			quads.add(new RdfQuad(default_graph,"<"+metaStatementUri+">"+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#singletonPropertyOf>"+" <"+predicate+"> .")); 	
		    			
		    		  //add link from statement ids of the meta statement to its groupId to enable to retrieve the nested metadata
		    			quads.add(new RdfQuad(default_graph,"<"+metaStatementUri+"> "+"<http://sdw.aksw.org/metardf/hasSharedId>"+"<"+groupUri+"> ."));  ; 			// .. we use an specific id for the meta group sharing the same nested metadata   
	    																																				// .. instead of attaching the metadata directly to every fact
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
	
	/* (non-Javadoc)
	 * @see org.aksw.sdw.meta_rdf.file.representations.AbstractRepresentationFormat#getDeduplicatedForUnit(org.aksw.sdw.meta_rdf.file.metafile.MetaStatementsUnit)
	 */
	@Override
	public Collection<RdfQuad> getDeduplicatedForUnit(MetaStatementsUnit mu) {
		// TODO Auto-generated method stub
		return null;
	}
}