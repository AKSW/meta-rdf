/**
 * 
 */
package org.aksw.sdw.meta_rdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.sdw.meta_rdf.file.metafile.MetaStatementsUnit;
import org.aksw.sdw.meta_rdf.file.metafile.MetadataUnit;
import org.aksw.sdw.meta_rdf.file.metafile.Statement;
import org.aksw.sdw.meta_rdf.file.metafile.StatementsUnit;

/**
 * @author kilt
 *
 */
public class MetaStatementsUnitView {
	MetaStatementsUnit m;
	Map<String,MetadataUnit> metadataUnits;
	
	public MetaStatementsUnitView(MetaStatementsUnit msu)
	{
		this.m = msu; int cnt=0;
		metadataUnits = new HashMap<>(m.getMetadata().size());
		for (MetadataUnit mo : m.getMetadata())
		{
			String groupid = (mo.getGroupid().equals("")) ? cnt+"" : mo.getGroupid();
			metadataUnits.put(groupid, mo);
			cnt++;
		}	
	}
	
//	public List<StatementsUnit> getStatementUnits()
//	{
//		return m.getStatementUnits();
//	}
	
	public List<StatementsUnit> getStatementUnits()
	{
		return m.getStatementUnits();
	}
	
	public List<MetadataUnit> getMetadataUnitsForStatement(Statement s)
	{
		 List<MetadataUnit> l = new ArrayList<>(s.getMids().size());
		 for ( String mid : s.getMids()) {
			l.add(metadataUnits.get(mid));
		 }
		 return l;
	}
	
	public List<MetadataUnit> getMetadataUnitsForStatementsGroup(StatementsUnit su)
	{
		 List<MetadataUnit> l = new ArrayList<>(su.getMids().size());
		 for ( String mid : su.getMids()) {
			MetadataUnit mu = metadataUnits.get(mid);
			if (null==mu)
				continue;			 //TODO log message or throw exception
			else
				l.add(metadataUnits.get(mid));
		 }
		 return l;
	}
	
	public List<MetadataUnit> getMetadataUnit(String mid)
	{
		 List<MetadataUnit> l = new ArrayList<>();
			MetadataUnit mu = metadataUnits.get(mid);
			if (null==mu)
				return l;			 //TODO log message or throw exception
			else
				l.add(metadataUnits.get(mid));
		 return l;
	}
	
	
	

}
