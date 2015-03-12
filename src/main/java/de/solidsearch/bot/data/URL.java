package de.solidsearch.bot.data;

import java.io.Serializable;

import javax.persistence.Id;

public class URL implements Serializable, Cloneable
{

	private static final long serialVersionUID = 5739276222401103412L;
	@Id
	private String id = "";

	private String parentId = "";

	private int partitionkey = 0;

	private String URLName = "";

	private String foundAtURL = "";

	private String redirectedToURL = "";

	private String canonicalTag = "";

	private String canonicalTagHashcode = null;

	private String firstFoundAnchorTextToThisURL = "";

	private String title = "";

	private String metaDescription = "";

	private Boolean metaRobotsIndex = true;

	private Boolean metaRobotsFollow = true;

	private String h1 = "";

	private String h2 = "";

	private String h3 = "";

	private int responseTime = 0;

	private int httpStatusCode = 0;

	private boolean externalLink = false;

	private String externalHostName = "";

	private boolean alreadyCrawled = false;

	private boolean timeout = false;

	private int internalLinksOnThisPage = 0;

	private long followLinksToThisPage = 0;

	private long nofollowLinksToThisPage = 0;

	private int externalLinksOnThisPage = 0;

	private int externalLinksDifferentDomainsOnThisPage = 0;

	private Long foundTimestamp = 0l;

	private int depthFromDomainRoot = -1;

	private int pageSize = 0;

	private String contentHashcode = null;

	private boolean duplicateContent = false;

	private boolean duplicateMetaDescription = false;

	private boolean duplicateTitle = false;

	private boolean duplicateH1 = false;

	private boolean headlinesNotInRightOrder = false;

	private boolean canonicalTagIssue = false;

	private boolean relNofollow = false;

	private short qualityScore = 0;

	private int facebookLikes = 0;

	private int facebookShares = 0;

	private boolean differentURLSameAnchor = false;

	private boolean googleAnalyticsCodeFound = false;

	private int newPrice = 0;

	private int oldPrice = 0;

	private String color = "";

	private short qwLocale = -1;

	private short readingLevel = -1;

	private short varietyTopicScore = 0;

	private String onPageText = "";

	private boolean relevantImages = false;

	private short adScripts = 0;

	private String normalizedTitle = "";

	private String normalizedText = "";

	private String normalizedH1 = "";

	private String normalizedH2 = "";

	private String normalizedH3 = "";

	private double pageRank = 0;
	
	private boolean trailingSlashIssue = false;
	
	private boolean gzipIssue = false;
	
	private short externalLinkPower = 0;
	
	private String topicKeywordOneTerm = "";
	
	private String topicKeywordTwoTerms = "";
	
	private String topicKeywordThreeTerms = "";
	
	private String normalizedTopicKeywordOneTerm = "";
	
	private String normalizedTopicKeywordTwoTerms = "";
	
	private String normalizedTopicKeywordThreeTerms = "";
	
	private short topicKeywordOneTermWeight = 0;
	
	private short topicKeywordTwoTermsWeight = 0;
	
	private short topicKeywordThreeTermsWeight = 0;
	
	private short spamScore = 0;
	
	private short backgroundId = 0;
	
	private boolean pagination = false;
	
	private boolean dcPrimary = false;
	
	private boolean keywordOrientationShortTerm = false;
	
	private boolean keywordOrientationTwoTerms = false;

	private int changeCode = 0;
	
	private boolean overwriteFlag = true;
	
	private boolean protocolRecord = false;
	
	private boolean blockedByRobotsTxt = false;
	
	private String relevantOnPageText = "";
	
	public URL()
	{

	}

	public URL(String id, int partitionkey, String uRLName, long foundTimestamp)
	{
		this.id = id;
		this.partitionkey = partitionkey;
		this.URLName = uRLName;
		this.foundTimestamp = foundTimestamp;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getParentId()
	{
		return parentId;
	}

	public void setParentId(String parentId)
	{
		this.parentId = parentId;
	}

	public int getPartitionkey()
	{
		return partitionkey;
	}

	public void setPartitionkey(int partitionkey)
	{
		this.partitionkey = partitionkey;
	}

	public String getURLName()
	{
		return URLName;
	}

	public void setURLName(String uRLName)
	{
		URLName = uRLName;
	}

	public String getCanonicalTag()
	{
		return canonicalTag;
	}

	public void setCanonicalTag(String canonicalTag)
	{
		this.canonicalTag = canonicalTag;
	}

	public String getCanonicalTagHashcode()
	{
		return canonicalTagHashcode;
	}

	public void setCanonicalTagHashcode(String canonicalTagHashcode)
	{
		this.canonicalTagHashcode = canonicalTagHashcode;
	}

	public String getFirstFoundAnchorTextToThisURL()
	{
		return firstFoundAnchorTextToThisURL;
	}

	public void setFirstFoundAnchorTextToThisURL(String firstFoundAnchorTextToThisURL)
	{
		this.firstFoundAnchorTextToThisURL = firstFoundAnchorTextToThisURL;
	}

	public Boolean getMetaRobotsIndex()
	{
		return metaRobotsIndex;
	}

	public void setMetaRobotsIndex(Boolean metaRobotsIndex)
	{
		this.metaRobotsIndex = metaRobotsIndex;
	}

	public Boolean getMetaRobotsFollow()
	{
		return metaRobotsFollow;
	}

	public void setMetaRobotsFollow(Boolean metaRobotsFollow)
	{
		this.metaRobotsFollow = metaRobotsFollow;
	}

	public String getH2()
	{
		return h2;
	}

	public void setH2(String h2)
	{
		this.h2 = h2;
	}

	public String getH3()
	{
		return h3;
	}

	public void setH3(String h3)
	{
		this.h3 = h3;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getMetaDescription()
	{
		return metaDescription;
	}

	public void setMetaDescription(String metaDescription)
	{
		this.metaDescription = metaDescription;
	}

	public String getH1()
	{
		return h1;
	}

	public void setH1(String h1)
	{
		this.h1 = h1;
	}

	public int getHttpStatusCode()
	{
		return httpStatusCode;
	}

	public void setHttpStatusCode(int httpStatusCode)
	{
		this.httpStatusCode = httpStatusCode;
	}

	public boolean isExternalLink()
	{
		return externalLink;
	}

	public void setExternalLink(boolean externalLink)
	{
		this.externalLink = externalLink;
	}

	public String getExternalHostName()
	{
		return externalHostName;
	}

	public void setExternalHostName(String externalHostName)
	{
		this.externalHostName = externalHostName;
	}

	public boolean isAlreadyCrawled()
	{
		return alreadyCrawled;
	}

	public void setAlreadyCrawled(boolean alreadyCrawled)
	{
		this.alreadyCrawled = alreadyCrawled;
	}

	public int getInternalOutgoingLinksOnThisPage()
	{
		return internalLinksOnThisPage;
	}

	public void setInternalLinksOnThisPage(int internalLinksOnThisPage)
	{
		this.internalLinksOnThisPage = internalLinksOnThisPage;
	}

	public long getFollowLinksToThisPage()
	{
		return followLinksToThisPage;
	}

	public void setFollowLinksToThisPage(long followLinksToThisPage)
	{
		this.followLinksToThisPage = followLinksToThisPage;
	}

	public long getNofollowLinksToThisPage()
	{
		return nofollowLinksToThisPage;
	}

	public void setNofollowLinksToThisPage(long nofollowLinksToThisPage)
	{
		this.nofollowLinksToThisPage = nofollowLinksToThisPage;
	}

	public int getExternalLinksOnThisPage()
	{
		return externalLinksOnThisPage;
	}

	public void setExternalLinksOnThisPage(int externalLinksOnThisPage)
	{
		this.externalLinksOnThisPage = externalLinksOnThisPage;
	}

	public int getExternalLinksDifferentDomainsOnThisPage()
	{
		return externalLinksDifferentDomainsOnThisPage;
	}

	public void setExternalLinksDifferentDomainsOnThisPage(int externalLinksDifferentDomainsOnThisPage)
	{
		this.externalLinksDifferentDomainsOnThisPage = externalLinksDifferentDomainsOnThisPage;
	}

	public Long getFoundTimestamp()
	{
		return foundTimestamp;
	}

	public void setFoundTimestamp(Long foundTimestamp)
	{
		this.foundTimestamp = foundTimestamp;
	}

	public int getDepthFromDomainRoot()
	{
		return depthFromDomainRoot;
	}

	public void setDepthFromDomainRoot(int depthFromDomainRoot)
	{
		this.depthFromDomainRoot = depthFromDomainRoot;
	}

	public boolean isTimeout()
	{
		return timeout;
	}

	public void setTimeout(boolean timeout)
	{
		this.timeout = timeout;
	}

	public int getResponseTime()
	{
		return responseTime;
	}

	public void setResponseTime(int responseTime)
	{
		this.responseTime = responseTime;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	public String getFoundAtURL()
	{
		return foundAtURL;
	}

	public void setFoundAtURL(String foundAtURL)
	{
		this.foundAtURL = foundAtURL;
	}

	public String getRedirectedToURL()
	{
		return redirectedToURL;
	}

	public void setRedirectedToURL(String redirectedToURL)
	{
		this.redirectedToURL = redirectedToURL;
	}

	public String getContentHashcode()
	{
		return contentHashcode;
	}

	public void setContentHashcode(String contentHashcode)
	{
		this.contentHashcode = contentHashcode;
	}

	public boolean isDuplicateContent()
	{
		return duplicateContent;
	}

	public void setDuplicateContent(boolean duplicateContent)
	{
		this.duplicateContent = duplicateContent;
	}

	public boolean isDuplicateMetaDescription()
	{
		return duplicateMetaDescription;
	}

	public void setDuplicateMetaDescription(boolean duplicateMetaDescription)
	{
		this.duplicateMetaDescription = duplicateMetaDescription;
	}

	public boolean isDuplicateTitle()
	{
		return duplicateTitle;
	}

	public void setDuplicateTitle(boolean duplicateTitle)
	{
		this.duplicateTitle = duplicateTitle;
	}

	public boolean isDuplicateH1()
	{
		return duplicateH1;
	}

	public void setDuplicateH1(boolean duplicateH1)
	{
		this.duplicateH1 = duplicateH1;
	}

	public boolean isHeadlinesNotInRightOrder()
	{
		return headlinesNotInRightOrder;
	}

	public void setHeadlinesNotInRightOrder(boolean headlinesNotInRightOrder)
	{
		this.headlinesNotInRightOrder = headlinesNotInRightOrder;
	}

	public boolean isCanonicalTagIssue()
	{
		return canonicalTagIssue;
	}

	public void setCanonicalTagIssue(boolean canonicalTagIssue)
	{
		this.canonicalTagIssue = canonicalTagIssue;
	}

	public boolean isRelNofollow()
	{
		return relNofollow;
	}

	public void setRelNofollow(boolean relNofollow)
	{
		this.relNofollow = relNofollow;
	}

	public short getQualityScore()
	{
		return qualityScore;
	}

	public void setQualityScore(short qualityScore)
	{
		this.qualityScore = qualityScore;
	}

	public int getFacebookLikes()
	{
		return facebookLikes;
	}

	public void setFacebookLikes(int facebookLikes)
	{
		this.facebookLikes = facebookLikes;
	}

	public int getFacebookShares()
	{
		return facebookShares;
	}

	public void setFacebookShares(int facebookShares)
	{
		this.facebookShares = facebookShares;
	}

	public boolean isDifferentURLSameAnchor()
	{
		return differentURLSameAnchor;
	}

	public void setDifferentURLSameAnchor(boolean differentURLSameAnchor)
	{
		this.differentURLSameAnchor = differentURLSameAnchor;
	}

	public boolean isGoogleAnalyticsCodeFound()
	{
		return googleAnalyticsCodeFound;
	}

	public void setGoogleAnalyticsCodeFound(boolean googleAnalyticsCodeFound)
	{
		this.googleAnalyticsCodeFound = googleAnalyticsCodeFound;
	}

	public double getNewPrice()
	{
		return newPrice;
	}

	public void setNewPrice(int newPrice)
	{
		this.newPrice = newPrice;
	}

	public int getOldPrice()
	{
		return oldPrice;
	}

	public void setOldPrice(int oldPrice)
	{
		this.oldPrice = oldPrice;
	}

	public String getColor()
	{
		return color;
	}

	public void setColor(String color)
	{
		this.color = color;
	}

	public int getInternalLinksOnThisPage()
	{
		return internalLinksOnThisPage;
	}

	public short getQwLocale()
	{
		return qwLocale;
	}

	public void setQwLocale(short qwLocale)
	{
		this.qwLocale = qwLocale;
	}

	public short getReadingLevel()
	{
		return readingLevel;
	}

	public void setReadingLevel(short readerLevel)
	{
		this.readingLevel = readerLevel;
	}

	public short getVarietyTopicScore()
	{
		return varietyTopicScore;
	}

	public void setVarietyTopicScore(short varietyTopicScore)
	{
		this.varietyTopicScore = varietyTopicScore;
	}

	public String getOnPageText()
	{
		return onPageText;
	}

	public void setOnPageText(String onPageText)
	{
		this.onPageText = onPageText;
	}

	public boolean isRelevantImages()
	{
		return relevantImages;
	}

	public void setRelevantImages(boolean relevantImages)
	{
		this.relevantImages = relevantImages;
	}

	public short getAdScripts()
	{
		return adScripts;
	}

	public void setAdScripts(short adScripts)
	{
		this.adScripts = adScripts;
	}

	public String getNormalizedText()
	{
		return normalizedText;
	}

	public void setNormalizedText(String normalizedText)
	{
		this.normalizedText = normalizedText;
	}

	public String getNormalizedTitle()
	{
		return normalizedTitle;
	}

	public void setNormalizedTitle(String normalizedTitle)
	{
		this.normalizedTitle = normalizedTitle;
	}

	public String getNormalizedH1()
	{
		return normalizedH1;
	}

	public void setNormalizedH1(String normalizedH1)
	{
		this.normalizedH1 = normalizedH1;
	}

	public String getNormalizedH2()
	{
		return normalizedH2;
	}

	public void setNormalizedH2(String normalizedH2)
	{
		this.normalizedH2 = normalizedH2;
	}

	public String getNormalizedH3()
	{
		return normalizedH3;
	}

	public void setNormalizedH3(String normalizedH3)
	{
		this.normalizedH3 = normalizedH3;
	}

	public double getPageRank()
	{
		return pageRank;
	}

	public void setPageRank(double pageRank)
	{
		this.pageRank = pageRank;
	}

	public boolean isTrailingSlashIssue()
	{
		return trailingSlashIssue;
	}

	public void setTrailingSlashIssue(boolean trailingSlashIssue)
	{
		this.trailingSlashIssue = trailingSlashIssue;
	}

	public boolean isGzipIssue()
	{
		return gzipIssue;
	}

	public void setGzipIssue(boolean gzipIssue)
	{
		this.gzipIssue = gzipIssue;
	}
	
	public short getExternalLinkPower()
	{
		return externalLinkPower;
	}

	public void setExternalLinkPower(short externalLinkPower)
	{
		this.externalLinkPower = externalLinkPower;
	}
	
	public String getTopicKeywordOneTerm()
	{
		return topicKeywordOneTerm;
	}

	public void setTopicKeywordOneTerm(String topicKeywordOneTerm)
	{
		this.topicKeywordOneTerm = topicKeywordOneTerm;
	}

	public String getTopicKeywordTwoTerms()
	{
		return topicKeywordTwoTerms;
	}

	public void setTopicKeywordTwoTerms(String topicKeywordTwoTerms)
	{
		this.topicKeywordTwoTerms = topicKeywordTwoTerms;
	}

	public String getTopicKeywordThreeTerms()
	{
		return topicKeywordThreeTerms;
	}

	public void setTopicKeywordThreeTerms(String topicKeywordThreeTerms)
	{
		this.topicKeywordThreeTerms = topicKeywordThreeTerms;
	}

	public String getNormalizedTopicKeywordOneTerm()
	{
		return normalizedTopicKeywordOneTerm;
	}

	public void setNormalizedTopicKeywordOneTerm(String normalizedTopicKeywordOneTerm)
	{
		this.normalizedTopicKeywordOneTerm = normalizedTopicKeywordOneTerm;
	}

	public String getNormalizedTopicKeywordTwoTerms()
	{
		return normalizedTopicKeywordTwoTerms;
	}

	public void setNormalizedTopicKeywordTwoTerms(String normalizedTopicKeywordTwoTerms)
	{
		this.normalizedTopicKeywordTwoTerms = normalizedTopicKeywordTwoTerms;
	}

	public String getNormalizedTopicKeywordThreeTerms()
	{
		return normalizedTopicKeywordThreeTerms;
	}

	public void setNormalizedTopicKeywordThreeTerms(String normalizedTopicKeywordThreeTerms)
	{
		this.normalizedTopicKeywordThreeTerms = normalizedTopicKeywordThreeTerms;
	}

	public short getSpamScore()
	{
		return spamScore;
	}

	public void setSpamScore(short spamScore)
	{
		this.spamScore = spamScore;
	}
	
	public short getBackgroundId()
	{
		return backgroundId;
	}

	public void setBackgroundId(short backgroundId)
	{
		this.backgroundId = backgroundId;
	}

	public boolean isPagination()
	{
		return pagination;
	}

	public void setPagination(boolean pagination)
	{
		this.pagination = pagination;
	}

	public boolean isDcPrimary()
	{
		return dcPrimary;
	}

	public void setDcPrimary(boolean dcPrimary)
	{
		this.dcPrimary = dcPrimary;
	}
	
	public short getTopicKeywordOneTermWeight()
	{
		return topicKeywordOneTermWeight;
	}

	public void setTopicKeywordOneTermWeight(short topicKeywordOneTermWeight)
	{
		this.topicKeywordOneTermWeight = topicKeywordOneTermWeight;
	}

	public short getTopicKeywordTwoTermsWeight()
	{
		return topicKeywordTwoTermsWeight;
	}

	public void setTopicKeywordTwoTermsWeight(short topicKeywordTwoTermsWeight)
	{
		this.topicKeywordTwoTermsWeight = topicKeywordTwoTermsWeight;
	}

	public short getTopicKeywordThreeTermsWeight()
	{
		return topicKeywordThreeTermsWeight;
	}

	public void setTopicKeywordThreeTermsWeight(short topicKeywordThreeTermsWeight)
	{
		this.topicKeywordThreeTermsWeight = topicKeywordThreeTermsWeight;
	}

	public boolean isKeywordOrientationShortTerm()
	{
		return keywordOrientationShortTerm;
	}

	public void setKeywordOrientationShortTerm(boolean keywordOrientationShortTerm)
	{
		this.keywordOrientationShortTerm = keywordOrientationShortTerm;
	}

	public boolean isKeywordOrientationTwoTerms()
	{
		return keywordOrientationTwoTerms;
	}

	public void setKeywordOrientationTwoTerms(boolean keywordOrientationTwoTerms)
	{
		this.keywordOrientationTwoTerms = keywordOrientationTwoTerms;
	}

	public int getChangeCode()
	{
		return changeCode;
	}

	public void setChangeCode(int changeCode)
	{
		this.changeCode = changeCode;
	}

	public boolean isOverwriteFlag()
	{
		return overwriteFlag;
	}

	public void setOverwriteFlag(boolean overwriteFlag)
	{
		this.overwriteFlag = overwriteFlag;
	}

	public boolean isProtocolRecord()
	{
		return protocolRecord;
	}

	public void setProtocolRecord(boolean protocolRecord)
	{
		this.protocolRecord = protocolRecord;
	}

	public boolean isBlockedByRobotsTxt()
	{
		return blockedByRobotsTxt;
	}

	public void setBlockedByRobotsTxt(boolean blockedByRobotsTxt)
	{
		this.blockedByRobotsTxt = blockedByRobotsTxt;
	}
	
	public String getRelevantOnPageText()
	{
		return relevantOnPageText;
	}

	public void setRelevantOnPageText(String relevantOnPageText)
	{
		this.relevantOnPageText = relevantOnPageText;
	}

	@Override
	public boolean equals(Object other)
	{
		boolean result = false;

		if (other == null)
			return result;

		if (!(other instanceof URL))
		{
			return result;
		}
		if (this.getURLName().equalsIgnoreCase(((URL) other).getURLName()))
		{
			result = true;
		}
		return result;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((URLName == null) ? 0 : URLName.hashCode());
		return result;
	}
	
    public URL clone() throws CloneNotSupportedException {
        return (URL)super.clone();
    }
}
