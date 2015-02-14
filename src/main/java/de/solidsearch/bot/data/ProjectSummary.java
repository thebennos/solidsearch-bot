package de.solidsearch.bot.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class ProjectSummary  implements Serializable
{
	private static final long serialVersionUID = 2827703672254524737L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long projectSummaryId;
	
	@Index(name="projectId_idx")
	private long projectId;
	
	private boolean finished = false;
    
    private long resultFromTimestampMills = 0;
	
    private long crawlingDurationMills = 0;
	
    private long totalLinks = 0;
    
    private long internalURLs = 0;
	
    private long internalNofollowURLs = 0; 
    
    private long internalFollowURLs = 0;
	
    private long internalNoindexURLs = 0;
	
    private long internalIndexURLs = 0;
	
    private long externalURLs = 0;
    
    private long externalURLsDifferentDomains = 0;
	
    private long crawledURLs = 0;
	
    private long clientErrorURLs = 0;
	
    private long timeoutURLs = 0;
	
    private long redirectionURLs = 0;
	
    private long serverErrorURLs = 0;
    
    private long canonicaltagToSource = 0;
    
    private long canonicalTagIssues = 0;
    
    private long missingH1 = 0;
	
    private int avgResponseTimeMills = 0;
		
    private long avgPageSize = 0;
        
    private String[] foundUrlParameter;
    
    private String[] mostInternalURLsTo;
    
    private String[] fewInternalURLsTo;
    
    private long missingTitleURLs = 0;
    
    private long longTitleURLs = 0;
    
    private long missingMetaDescriptionURLs = 0;
    
    private long longMetaDescriptionURLs = 0;
    
    private long missingOnPageTextURLs = 0;
    
    private long duplicateContentURLs = 0;
    
    private long headlinesNotInRightOrderURLs = 0;
    
    private long duplicateTitleURLs = 0;
    
    private long duplicateMetaDescriptionURLs = 0;
    
    private long duplicateH1URLs = 0;
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="projectSummary")
    @LazyCollection(LazyCollectionOption.FALSE)
    @OnDelete(action=OnDeleteAction.CASCADE)
    @MapKey(name="projectSummaryInfoCode")
    private Map<Integer,AlarmList> infoMessages = new HashMap<Integer,AlarmList>();
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="projectSummary")
    @LazyCollection(LazyCollectionOption.FALSE)
    @OnDelete(action=OnDeleteAction.CASCADE)
    private List<HostnameStatistic> hostnameStatisticList = new ArrayList<HostnameStatistic>();
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="projectSummary")
    @LazyCollection(LazyCollectionOption.FALSE)
    @OnDelete(action=OnDeleteAction.CASCADE)
    private List<URLSegmentStatistic> urlSegmentStatisticList = new ArrayList<URLSegmentStatistic>();
    
    private long missingGoogleAnalyticsCode = 0;
    
    private boolean googleAnalyticsUsed = false;
    
    // annotation is necessary for later db schema updates
    @Column(columnDefinition = "int2 default 0")
    private short qualityScore = 0; 
    
    @Column(columnDefinition = " bigint NOT NULL default 0")
    private long differentURLSameAnchor = 0;

    private double avgInternalFollowLinks = 0; 
    
    private double maxInternalLinksThreshold = 0;
    
    private double externalLinksThreshold = 0;
    
    private long totalCountOfRelevantKeywords = 0;
    
    private long totalCountOfKeywords = 0;
        
    private double readinglevel = 0;
    
    private long trailingSlashIssues = 0;
    
    private long gzipIssues = 0;
    
    @Column(columnDefinition = "text")
    private String domainBrandName = "";
    
    private long keywordOrientationShortTermURLs = 0;
    
    private long keywordOrientationTwoTermsURLs = 0;
    
    @Column(columnDefinition = "text")
    private String homeDocument = "";
    
	public ProjectSummary()
	{

	}
	
	public ProjectSummary(long timestamp, long projectId)
	{
		this.resultFromTimestampMills = timestamp;
		this.projectId = projectId;
	}

	public long getProjectSummaryId()
	{
		return projectSummaryId;
	}

	public void setProjectSummaryId(long projectSummaryId)
	{
		this.projectSummaryId = projectSummaryId;
	}

	public boolean isFinished()
	{
		return finished;
	}

	public void setFinished(boolean finished)
	{
		this.finished = finished;
	}

	public Long getProjectId()
	{
		return projectId;
	}

	public void setProjectId(Long projectId)
	{
		this.projectId = projectId;
	}

	public long getResultFromTimestampMills()
	{
		return resultFromTimestampMills;
	}

	public long getCrawlingDurationMills()
	{
		return crawlingDurationMills;
	}

	public void setCrawlingDurationMills(long crawlingDurationMills)
	{
		this.crawlingDurationMills = crawlingDurationMills;
	}

	public void setResultFromTimestampMills(long resultFromTimestampMills)
	{
		this.resultFromTimestampMills = resultFromTimestampMills;
	}

	public long getTotalLinks()
	{
		return totalLinks;
	}

	public void setTotalLinks(long totalLinks)
	{
		this.totalLinks = totalLinks;
	}

	public long getInternalURLs()
	{
		return internalURLs;
	}

	public void setInternalURLs(long internalURLs)
	{
		this.internalURLs = internalURLs;
	}

	public long getInternalNofollowURLs()
	{
		return internalNofollowURLs;
	}

	public void setInternalNofollowURLs(long internalNofollowURLs)
	{
		this.internalNofollowURLs = internalNofollowURLs;
	}
	
	public long getInternalFollowURLs()
	{
		return internalFollowURLs;
	}

	public void setInternalFollowURLs(long internalFollowURLs)
	{
		this.internalFollowURLs = internalFollowURLs;
	}

	public long getInternalNoindexURLs()
	{
		return internalNoindexURLs;
	}

	public void setInternalNoindexURLs(long internalNoindexURLs)
	{
		this.internalNoindexURLs = internalNoindexURLs;
	}

	public long getExternalURLs()
	{
		return externalURLs;
	}

	public void setExternalURLs(long externalURLs)
	{
		this.externalURLs = externalURLs;
	}
	
	public long getExternalURLsDifferentDomains()
	{
		return externalURLsDifferentDomains;
	}

	public void setExternalURLsDifferentDomains(long externalURLsDifferentDomains)
	{
		this.externalURLsDifferentDomains = externalURLsDifferentDomains;
	}

	public int getAvgResponseTimeMills()
	{
		return avgResponseTimeMills;
	}

	public void setAvgResponseTimeMills(int avgResponseTimeMills)
	{
		this.avgResponseTimeMills = avgResponseTimeMills;
	}

	public long getAvgPageSize()
	{
		return avgPageSize;
	}

	public void setAvgPageSize(long avgPageSize)
	{
		this.avgPageSize = avgPageSize;
	}

	public String[] getFoundUrlParameter()
	{
		return foundUrlParameter;
	}

	public void setFoundUrlParameter(String[] foundUrlParameter)
	{
		this.foundUrlParameter = foundUrlParameter;
	}

	public String[] getMostInternalURLsTo()
	{
		return mostInternalURLsTo;
	}

	public void setMostInternalURLsTo(String[] mostInternalURLsTo)
	{
		this.mostInternalURLsTo = mostInternalURLsTo;
	}

	public String[] getFewInternalURLsTo()
	{
		return fewInternalURLsTo;
	}

	public void setFewInternalURLsTo(String[] fewInternalURLsTo)
	{
		this.fewInternalURLsTo = fewInternalURLsTo;
	}

	public long getMissingTitleURLs()
	{
		return missingTitleURLs;
	}

	public void setMissingTitleURLs(long missingTitleURLs)
	{
		this.missingTitleURLs = missingTitleURLs;
	}

	public long getMissingMetaDescriptionURLs()
	{
		return missingMetaDescriptionURLs;
	}

	public void setMissingMetaDescriptionURLs(long missingMetaDescriptionURLs)
	{
		this.missingMetaDescriptionURLs = missingMetaDescriptionURLs;
	}

	public long getMissingOnPageTextURLs()
	{
		return missingOnPageTextURLs;
	}

	public void setMissingOnPageTextURLs(long missingOnPageTextURLs)
	{
		this.missingOnPageTextURLs = missingOnPageTextURLs;
	}

	public long getInternalIndexURLs()
	{
		return internalIndexURLs;
	}

	public void setInternalIndexURLs(long internalIndexURLs)
	{
		this.internalIndexURLs = internalIndexURLs;
	}

	public long getCrawledURLs()
	{
		return crawledURLs;
	}

	public void setCrawledURLs(long crawledURLs)
	{
		this.crawledURLs = crawledURLs;
	}

	public long getClientErrorURLs()
	{
		return clientErrorURLs;
	}

	public void setClientErrorURLs(long clientErrorURL)
	{
		this.clientErrorURLs = clientErrorURL;
	}

	public long getTimeoutURLs()
	{
		return timeoutURLs;
	}

	public void setTimeoutURLs(long timeoutURLs)
	{
		this.timeoutURLs = timeoutURLs;
	}
	
	public long getRedirectionURLs()
	{
		return redirectionURLs;
	}

	public void setRedirectionURLs(long redirectionURLs)
	{
		this.redirectionURLs = redirectionURLs;
	}

	public long getServerErrorURLs()
	{
		return serverErrorURLs;
	}

	public void setServerErrorURLs(long serverErrorURLs)
	{
		this.serverErrorURLs = serverErrorURLs;
	}

	public long getLongTitleURLs()
	{
		return longTitleURLs;
	}

	public void setLongTitleURLs(long longTitleURLs)
	{
		this.longTitleURLs = longTitleURLs;
	}

	public long getLongMetaDescriptionURLs()
	{
		return longMetaDescriptionURLs;
	}

	public void setLongMetaDescriptionURLs(long longMetaDescriptionURLs)
	{
		this.longMetaDescriptionURLs = longMetaDescriptionURLs;
	}

	public long getDuplicateContentURLs()
	{
		return duplicateContentURLs;
	}

	public void setDuplicateContentURLs(long duplicateContentURLs)
	{
		this.duplicateContentURLs = duplicateContentURLs;
	}

	public long getCanonicaltagToSource()
	{
		return canonicaltagToSource;
	}

	public void setCanonicaltagToSource(long canonicaltagToSource)
	{
		this.canonicaltagToSource = canonicaltagToSource;
	}

	public long getMissingH1()
	{
		return missingH1;
	}

	public void setMissingH1(long missingH1)
	{
		this.missingH1 = missingH1;
	}

	public long getCanonicalTagIssues()
	{
		return canonicalTagIssues;
	}

	public void setCanonicalTagIssues(long canonicalTagIssues)
	{
		this.canonicalTagIssues = canonicalTagIssues;
	}

	public long getHeadlinesNotInRightOrderURLs()
	{
		return headlinesNotInRightOrderURLs;
	}

	public void setHeadlinesNotInRightOrderURLs(long headlinesNotInRightOrderURLs)
	{
		this.headlinesNotInRightOrderURLs = headlinesNotInRightOrderURLs;
	}

	public long getDuplicateTitleURLs()
	{
		return duplicateTitleURLs;
	}

	public void setDuplicateTitleURLs(long duplicateTitleURLs)
	{
		this.duplicateTitleURLs = duplicateTitleURLs;
	}

	public long getDuplicateMetaDescriptionURLs()
	{
		return duplicateMetaDescriptionURLs;
	}

	public void setDuplicateMetaDescriptionURLs(long duplicateMetaDescriptionURLs)
	{
		this.duplicateMetaDescriptionURLs = duplicateMetaDescriptionURLs;
	}

	public long getDuplicateH1URLs()
	{
		return duplicateH1URLs;
	}

	public void setDuplicateH1URLs(long duplicateH1URLs)
	{
		this.duplicateH1URLs = duplicateH1URLs;
	}

	/**
	 * Returns a map of key=InfoMessagesCodes and value=AlarmMessages. 
	 * Every info-Message-code has got a ArrayList of URL examples.
	 * @return
	 */
	public Map<Integer,AlarmList> getInfoMessages()
	{
		return infoMessages;
	}
	/**
	 * Set infoMessages to this summary with a belonging list of example-URLs
	 * @param infoMessages
	 */
	public void setInfoMessages(Map<Integer,AlarmList> infoMessages)
	{
		this.infoMessages = infoMessages;
	}

	public short getQualityScore()
	{
		return qualityScore;
	}

	public void setQualityScore(short qualityScore)
	{
		this.qualityScore = qualityScore;
	}

	public long getDifferentURLSameAnchor()
	{
		return differentURLSameAnchor;
	}

	public void setDifferentURLSameAnchor(long differentURLSameAnchor)
	{
		this.differentURLSameAnchor = differentURLSameAnchor;
	}

	public List<HostnameStatistic> getHostnameStatisticList() 
	{
		return hostnameStatisticList;
	}

	public void setHostnameStatisticList(List<HostnameStatistic> hostnameStatisticList) 
	{
		this.hostnameStatisticList = hostnameStatisticList;
	}

	public List<URLSegmentStatistic> getUrlSegmentStatisticList() {
		return urlSegmentStatisticList;
	}

	public void setUrlSegmentStatisticList(List<URLSegmentStatistic> urlSegmentStatisticList) 
	{
		this.urlSegmentStatisticList = urlSegmentStatisticList;
	}

	public long getMissingGoogleAnalyticsCode() 
	{
		return missingGoogleAnalyticsCode;
	}

	public void setMissingGoogleAnalyticsCode(long missingGoogleAnalyticsCode) 
	{
		this.missingGoogleAnalyticsCode = missingGoogleAnalyticsCode;
	}

	public boolean isGoogleAnalyticsUsed() 
	{
		return googleAnalyticsUsed;
	}

	public void setGoogleAnalyticsUsed(boolean googleAnalyticsUsed) 
	{
		this.googleAnalyticsUsed = googleAnalyticsUsed;
	}

	public double getAvgInternalFollowLinks()
	{
		return avgInternalFollowLinks;
	}

	public void setAvgInternalFollowLinks(double avgInternalFollowLinks)
	{
		this.avgInternalFollowLinks = avgInternalFollowLinks;
	}

	public double getMaxInternalLinksThreshold()
	{
		return maxInternalLinksThreshold;
	}

	public void setMaxInternalLinksThreshold(double maxInternalLinksThreshold)
	{
		this.maxInternalLinksThreshold = maxInternalLinksThreshold;
	}

	public double getExternalLinksThreshold()
	{
		return externalLinksThreshold;
	}

	public void setExternalLinksThreshold(double externalLinksThreshold)
	{
		this.externalLinksThreshold = externalLinksThreshold;
	}

	public long getTotalCountOfRelevantKeywords()
	{
		return totalCountOfRelevantKeywords;
	}

	public void setTotalCountOfRelevantKeywords(long totalCountOfRelevantKeywords)
	{
		this.totalCountOfRelevantKeywords = totalCountOfRelevantKeywords;
	}

	public long getTotalCountOfKeywords()
	{
		return totalCountOfKeywords;
	}

	public void setTotalCountOfKeywords(long totalCountOfKeywords)
	{
		this.totalCountOfKeywords = totalCountOfKeywords;
	}

	public double getReadinglevel()
	{
		return readinglevel;
	}

	public void setReadinglevel(double readinglevel)
	{
		this.readinglevel = readinglevel;
	}

	public long getTrailingSlashIssues()
	{
		return trailingSlashIssues;
	}

	public void setTrailingSlashIssues(long trailingSlashIssues)
	{
		this.trailingSlashIssues = trailingSlashIssues;
	}

	public long getGzipIssues()
	{
		return gzipIssues;
	}

	public void setGzipIssues(long gzipIssues)
	{
		this.gzipIssues = gzipIssues;
	}

	public String getDomainBrandName()
	{
		return domainBrandName;
	}

	public void setDomainBrandName(String domainBrandName)
	{
		this.domainBrandName = domainBrandName;
	}

	public long getKeywordOrientationShortTermURLs()
	{
		return keywordOrientationShortTermURLs;
	}

	public void setKeywordOrientationShortTermURLs(long keywordOrientationShortTermURLs)
	{
		this.keywordOrientationShortTermURLs = keywordOrientationShortTermURLs;
	}

	public long getKeywordOrientationTwoTermsURLs()
	{
		return keywordOrientationTwoTermsURLs;
	}

	public void setKeywordOrientationTwoTermsURLs(long keywordOrientationTwoTermsURLs)
	{
		this.keywordOrientationTwoTermsURLs = keywordOrientationTwoTermsURLs;
	}

	public String getHomeDocument()
	{
		return homeDocument;
	}

	public void setHomeDocument(String homeDocument)
	{
		this.homeDocument = homeDocument;
	}
}
