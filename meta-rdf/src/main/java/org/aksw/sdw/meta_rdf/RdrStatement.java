package org.aksw.sdw.meta_rdf;

public class RdrStatement extends RdfQuad
{
	RdfStatement s;
	RdrStatement innerStatement;
	String property;
	String object;
	
//	public RdrStatement(RdrStatement innerStatement, String property, String object)
//	{
//		this.innerStatement=innerStatement;
//		this.property = property;
//		this.object = object;
//	}
	
	public RdrStatement(RdfStatement s, String property, String object) 
	{
		if(s instanceof RdrStatement) //that is really dirty because of the recursive definition and inheritance from RdfQuad/RdfStatement
		{
			this.innerStatement=(RdrStatement) s;
			this.property = property;
			this.object = object;
		}
		else
		{
			this.s = s;
			this.property = property;
			this.object = object;
		}
	}
	
	public boolean isRecursiveStatement()
	{
		if (innerStatement==null)
			return false;
		else
			return true;
	}
	
	public String getAsRdr()
	{
		if(isRecursiveStatement())
			return "<<"+innerStatement.getAsRdr()+" >> "+property+" "+object;	
		else
		{
			String nt = s.getAsNt();
			nt = nt.substring(0,nt.length()-2);
			return "<<"+nt+" >> "+property+" "+object;
		}		
	}
	
	@Override
	public String getAsNq()
	{
		return getAsRdr()+" .\n";
	}	
	
	
	
}
