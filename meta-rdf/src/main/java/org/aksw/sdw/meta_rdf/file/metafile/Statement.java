package org.aksw.sdw.meta_rdf.file.metafile;

import java.util.List;

public class Statement {
	
	String type;
	String tuple;
	String sid;
	List<String> mids;
	
	public List<String> getMids() {
		return mids;
	}

	public Statement() {
		// TODO Auto-generated constructor stub
	}

	public String getType() {
		return type;
	}

	public String getTuple() {
		return tuple;
	}

	public String getSid() {
		return sid;
	}
	
	

}
