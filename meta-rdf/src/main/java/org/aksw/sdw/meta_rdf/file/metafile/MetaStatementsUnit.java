/**
 * 
 */
package org.aksw.sdw.meta_rdf.file.metafile;

import java.util.List;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class MetaStatementsUnit {
	
public	
	String groupid;
	List<Statement> statements;
	List<MetadataUnit> metadata;
	List<String> params;
	List<String> mids;
	
	public String toString() {
	     return new ReflectionToStringBuilder(this, new RecursiveToStringStyle()).toString();
	}
	
	
	/* Getters */
	public List<String> getMids() {
		return mids;
	}

	public String getGroupid() {
		return groupid;
	}

	public List<Statement> getStatements() {
		return statements;
	}

	public List<MetadataUnit> getMetadata() {
		return metadata;
	}

	public List<String> getParams() {
		return params;
	}
	
	
}


