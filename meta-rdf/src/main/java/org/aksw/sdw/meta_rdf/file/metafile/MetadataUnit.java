package org.aksw.sdw.meta_rdf.file.metafile;

import java.util.LinkedList;
import java.util.List;

public class MetadataUnit {
	

	String 				groupid;
	List<MetadataFact> 	metadataFacts;
	String 				hasMeta;


	public MetadataUnit() {
		// TODO Auto-generated constructor stub
	}


	public String getGroupid() {
		return groupid;
	}


	public List<MetadataFact> getMetadataFacts() {
			if (metadataFacts==null)
				return new LinkedList<MetadataFact>();
			else
				return metadataFacts;
	}


	public String getHasNested() {
		if (null==hasMeta)
			return "";
		else
			return hasMeta;
	}
	

	
}
