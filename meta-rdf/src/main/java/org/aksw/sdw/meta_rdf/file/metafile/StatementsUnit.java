package org.aksw.sdw.meta_rdf.file.metafile;

import java.util.LinkedList;
import java.util.List;

public class StatementsUnit {
	
	String groupid;
	List<Statement> statements;
	List<String> mids;
	

	public StatementsUnit() {
		// TODO Auto-generated constructor stub
	}


	public String getGroupid()
	{
		if (groupid==null)
			return "";
		else
			return groupid;
	}


	public List<Statement> getStatements()
	{
		return statements;
	}


	public List<String> getMids()
	{
		if (mids==null)
			return new LinkedList<>();
		else
			return mids;
	}

	
	

}
