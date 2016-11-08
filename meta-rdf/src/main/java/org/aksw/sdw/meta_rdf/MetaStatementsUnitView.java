/**
 * 
 */
package org.aksw.sdw.meta_rdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aksw.sdw.meta_rdf.file.metafile.MetaStatementsUnit;
import org.aksw.sdw.meta_rdf.file.metafile.MetadataUnit;
import org.aksw.sdw.meta_rdf.file.metafile.Statement;

/**
 * @author kilt
 *
 */
public class MetaStatementsUnitView {
	MetaStatementsUnit m;
	Map<String,MetadataUnit> metadataUnits;
	
	public MetaStatementsUnitView(MetaStatementsUnit mu)
	{
		this.m = mu;
		for (MetadataUnit mo : m.getMetadata())
		{
			metadataUnits.put(mo.getGroupid(), mo);
		}	
	}
	
	public List<Statement> getStatements()
	{
		return m.getStatements();
	}
	
	
	public List<MetadataUnit> getMetadataUnitsForStatement(Statement s)
	{
		 List<MetadataUnit> l = new ArrayList<>(s.getMids().size());
		 for ( String mid : s.getMids()) {
			l.add(metadataUnits.get(mid));
		 }
		 return l;
	}
	
	
	

}
