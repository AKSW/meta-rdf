package org.aksw.sdw.meta_rdf;

import java.util.Collection;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class MetaStatement {
	private RdfStatement triple;
	private Multimap<String,String> metadata;
	
	public MetaStatement(RdfStatement triple, String key, String value) {
		this.triple=triple; 
		this.metadata = Multimaps.synchronizedSetMultimap(HashMultimap.create()); 
		this.metadata.put(key, value);
	}
	
	public MetaStatement(RdfStatement triple) {
		this.triple=triple; 
		this.metadata = Multimaps.synchronizedSetMultimap(HashMultimap.create()); 
	}
	
	public RdfStatement getRdfStatement() {
		return this.triple;
	}
	
	public boolean addMedadataFact(String key, String value)
	{
		 return this.metadata.put(key, value);
	}
	
	public Collection<String> getMedadataFact(String key)
	{
		return this.metadata.get(key);
	}
	
	public Collection<String> getAllMedadataFactKeys()
	{
		return this.metadata.keySet();
	}
}
