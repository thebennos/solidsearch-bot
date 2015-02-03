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
public class HostnameStatistic implements Serializable
{

	private static final long serialVersionUID = -6702857446889504742L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "projectSummaryId", nullable = false)
	private ProjectSummary projectSummary;
	
	private String hostname;
	private long pages;
	private long followLinksToHostname;
	private long nofollowLinksToHostname;

	public ProjectSummary getProjectSummary()
	{
		return projectSummary;
	}

	public void setProjectSummary(ProjectSummary projectSummary)
	{
		this.projectSummary = projectSummary;
	}

	public String getHostname()
	{
		return hostname;
	}

	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}

	public long getPages()
	{
		return pages;
	}

	public void setPages(long pages)
	{
		this.pages = pages;
	}

	public long getFollowLinksToHostname()
	{
		return followLinksToHostname;
	}

	public void setFollowLinksToHostname(long followLinksToHostname)
	{
		this.followLinksToHostname = followLinksToHostname;
	}

	public long getNofollowLinksToHostname()
	{
		return nofollowLinksToHostname;
	}

	public void setNofollowLinksToHostname(long nofollowLinksToHostname)
	{
		this.nofollowLinksToHostname = nofollowLinksToHostname;
	}

}
