package de.solidsearch.bot.data;

import java.io.Serializable;

public class KeywordByLevel implements Serializable
{

	private static final long serialVersionUID = 4258131587499776719L;
	
	private String keyword;
	private Short depthFromRoot;
	private Integer frequency;
	
	public String getKeyword()
	{
		return keyword;
	}
	
	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}
	
	public Short getDepthFromRoot()
	{
		return depthFromRoot;
	}
	
	public void setDepthFromRoot(Short depthFromRoot)
	{
		this.depthFromRoot = depthFromRoot;
	}
	
	public Integer getFrequency()
	{
		return frequency;
	}
	
	public void setFrequency(Integer frequency)
	{
		this.frequency = frequency;
	}
	
}
