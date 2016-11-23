/**
 * 
 */
package org.aksw.sdw.meta_rdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aksw.sdw.meta_rdf.file.metafile.MetaStatementsUnit;
import org.aksw.sdw.meta_rdf.file.metafile.MetadataUnit;
import org.aksw.sdw.meta_rdf.file.metafile.Statement;
import org.aksw.sdw.meta_rdf.file.metafile.StatementsUnit;

import com.github.andrewoma.dexx.collection.Pair;


/**
 * @author kilt
 *
 */
public class MetaStatementsUnitView {
	MetaStatementsUnit m;
	Map<String,MetadataUnit> metadataUnits;
	protected Map<Pair<String,String>,Integer> propertyCount = new  ConcurrentHashMap<>();
	
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
	/**
	 * this method counts (and increments) for every subject separately how often a predicate has been processed within this MetaStatementsUnitView
	 * 
	 * @param q the quad which is going to be processed containing the predicate and subject which are use for the lookup 
	 * @return the value of already processed predicates for the given subject predicate combination INCREMENTED by 1
	 */
	public int incrementProcessedCountForPredicatePerSubject(RdfQuad q)
	{ 
		Pair<String,String> p = new Pair<>(q.getSubject(), q.getPredicate());
		Integer cnt = propertyCount.get(p);
		if (null==cnt)
		{
			propertyCount.put(p, 0);
			return 0;
		}else
		{
			cnt++;
			propertyCount.put(p, cnt);
			return cnt;	
		}
			
			
	}
	

}
