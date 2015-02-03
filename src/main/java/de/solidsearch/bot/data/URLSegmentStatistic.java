package de.solidsearch.bot.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Index;

@Entity
public class URLSegmentStatistic implements Serializable
{

	private static final long serialVersionUID = 4283625098896182405L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "projectSummaryId", nullable = false)
	private ProjectSummary projectSummary;

	private String urlsegment;
	private long pages;
	private long nofollowLinksToSegment;
	private long followLinksToSegment;
	private int avgresponsetime;
	private int avgpagesize;
	private long keywords = 0;


	public ProjectSummary getProjectSummary()
	{
		return projectSummary;
	}

	public void setProjectSummary(ProjectSummary projectSummary)
	{
		this.projectSummary = projectSummary;
	}

	public String getUrlsegment()
	{
		return urlsegment;
	}

	public void setUrlsegment(String urlsegment)
	{
		this.urlsegment = urlsegment;
	}

	public long getPages()
	{
		return pages;
	}

	public void setPages(long pages)
	{
		this.pages = pages;
	}

	public long getNofollowLinksToSegment()
	{
		return nofollowLinksToSegment;
	}

	public void setNofollowLinksToSegment(long nofollowLinksToSegment)
	{
		this.nofollowLinksToSegment = nofollowLinksToSegment;
	}

	public long getFollowLinksToSegment()
	{
		return followLinksToSegment;
	}

	public void setFollowLinksToSegment(long followLinksToSegment)
	{
		this.followLinksToSegment = followLinksToSegment;
	}

	public int getAvgresponsetime()
	{
		return avgresponsetime;
	}

	public void setAvgresponsetime(int avgresponsetime)
	{
		this.avgresponsetime = avgresponsetime;
	}

	public int getAvgpagesize()
	{
		return avgpagesize;
	}

	public void setAvgpagesize(int avgpagesize)
	{
		this.avgpagesize = avgpagesize;
	}

	public long getKeywords()
	{
		return keywords;
	}

	public void setKeywords(long keywords)
	{
		this.keywords = keywords;
	}

}
