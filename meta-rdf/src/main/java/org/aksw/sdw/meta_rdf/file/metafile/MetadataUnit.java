package org.aksw.sdw.meta_rdf.file.metafile;

import java.util.List;

public class MetadataUnit {
	

	String 				groupid;
	List<MetadataFact> 	metadataFacts;
	String 				hasNested;


	public MetadataUnit() {
		// TODO Auto-generated constructor stub
	}


	public String getGroupid() {
		return groupid;
	}


	public List<MetadataFact> getMetadataFacts() {
		return metadataFacts;
	}


	public String getHasNested() {
		return hasNested;
	}
	

	
}
