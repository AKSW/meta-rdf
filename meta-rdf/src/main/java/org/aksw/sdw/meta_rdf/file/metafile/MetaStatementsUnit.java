/**
 * 
 */
package org.aksw.sdw.meta_rdf.file.metafile;

import java.util.List;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class MetaStatementsUnit {
	
public	
	List<StatementsUnit> statementgroups;
	List<MetadataUnit> metadata;
	List<String> params;
	
	public String toString() {
	     return new ReflectionToStringBuilder(this, new RecursiveToStringStyle()).toString();
	}
	
	/* Getters */

	public List<MetadataUnit> getMetadata() {
		return metadata;
	}

	public List<StatementsUnit> getStatementUnits() {
		return statementgroups;
	}

	public List<String> getParams() {
		return params;
	}
	
	
}


