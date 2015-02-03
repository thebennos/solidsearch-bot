package de.solidsearch.bot.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Project implements Serializable
{
	private static final long serialVersionUID = 21263129347128649L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long projectId;

	private long userGroupId;
    
	private int crawlingDepth = 15;
    
	private int crawlLimit = 10000;
	
	private int discoverLimit = 1000000;
	    
	private boolean ignoreImages = true;
    
	private boolean ignoreCSS = true;
    
	private boolean ignoreJS = true;
        
	private boolean ignoreRobotsTxt = false;
    
	private boolean ignoreInternalNofollow = false;
    
	@Column(name = "robotsRegex", length = 20000)
	private String robotsRegex = "";
    
	private String botUserAgent = "Qualitywatch bot by bonprix.de";
    
	private String urlOfDomainToCrawl = "http://www.bonprix.de/";
	
	private String rootDomainToCrawl = "http://www.bonprix.de";
	
	private String rootDomainToCrawlWithoutProtocol = "www.bonprix.de";
    
	private boolean rapidCrawling = false;
	
	private boolean isRunning = false;
	
	private boolean isActive = false;
		
	private boolean useBasicAuth = false;
    
	private String basicAuthUser = "";
    
	private String basicAuthPassword = "";
	
	private long lastRunTimestamp = 0;
    
	private String projectName = "-";
	
	private String crawlerDatabaseId = "";
	
	private ArrayList<Integer> infoMessageCodes = new ArrayList<Integer>();
	
	private HashMap<String,Calendar> availableCrawlings  = new HashMap<String,Calendar>();
	
	private boolean isUIRefreshNeccessary = false; 
	
	private boolean runMonday = false;
	
	private boolean runTuesday = false;
	
	private boolean runWednesday = false;
	
	private boolean runThursday = false;
	
	private boolean runFriday = false;
	
	private boolean runSaturday = false;
	
	private boolean runSunday = false;
	
	@Column(name = "notificationEmailAddresses", length = 1000)
	private String notificationEmailAddresses = null;
	
	@Column(columnDefinition = "boolean NOT NULL default true")
	private boolean notificationEnabled = true;
	
	private Locale defaultLocale = Locale.GERMAN;
	
	@Column(columnDefinition = "varchar(300) NOT NULL default ''")
	private String cookieName = "";
	
	@Column(columnDefinition = "varchar(500) NOT NULL default ''")
	private String cookieValue = "";
	
	@Column(columnDefinition = "boolean NOT NULL default false")
	private boolean useSetCookie = false;
	
	@Column(columnDefinition = "smallint NOT NULL default 0")
	private int errorCount = 0;
	
	private boolean disabledByError = false;
	
	private boolean remoteProject = false;
	
	private boolean listCrawlingEnabed = true;
	
	private boolean siteCrawlingEnabled = true;
	
	private long listJobLastRunTimestamp = 0;
	
	private boolean newProject = true;
	
	private boolean changesInListCrawling = false;
	
	public Project()
	{
		
	}
	
	@Override
	public String toString()
	{
		return projectName;
	}
	
	public Project(long userGroupId)
	{
		this.userGroupId = userGroupId;
	}
	
	public int getCrawlingDepth()
	{
		return crawlingDepth;
	}

	public void setCrawlingDepth(int crawlingDepth)
	{
		this.crawlingDepth = crawlingDepth;
	}

	public int getCrawlLimit()
	{
		return crawlLimit;
	}

	public void setCrawlLimit(int crawlLimit)
	{
		this.crawlLimit = crawlLimit;
	}

	public boolean isIgnoreImages()
	{
		return ignoreImages;
	}

	public void setIgnoreImages(boolean ignoreImages)
	{
		this.ignoreImages = ignoreImages;
	}

	public boolean isIgnoreCSS()
	{
		return ignoreCSS;
	}

	public void setIgnoreCSS(boolean ignoreCSS)
	{
		this.ignoreCSS = ignoreCSS;
	}

	public boolean isIgnoreJS()
	{
		return ignoreJS;
	}

	public void setIgnoreJS(boolean ignoreJS)
	{
		this.ignoreJS = ignoreJS;
	}

	public boolean isIgnoreRobotsTxt()
	{
		return ignoreRobotsTxt;
	}

	public void setIgnoreRobotsTxt(boolean ignoreRobotsTxt)
	{
		this.ignoreRobotsTxt = ignoreRobotsTxt;
	}

	public boolean isIgnoreInternalNofollow()
	{
		return ignoreInternalNofollow;
	}

	public void setIgnoreInternalNofollow(boolean ignoreInternalNofollow)
	{
		this.ignoreInternalNofollow = ignoreInternalNofollow;
	}

	public String getRobotsRegex()
	{
		return robotsRegex;
	}

	public void setRobotsRegex(String robotsRegex)
	{
		this.robotsRegex = robotsRegex;
	}

	public String getBotUserAgent()
	{
		return botUserAgent;
	}

	public void setBotUserAgent(String botUserAgent)
	{
		this.botUserAgent = botUserAgent;
	}
	
	public String getUrlOfDomainToCrawl()
	{	
		return urlOfDomainToCrawl;
	}

	public void setUrlOfDomainToCrawl(String urlOfDomainToCrawl)
	{
		this.urlOfDomainToCrawl = urlOfDomainToCrawl;
	}

	public String getRootDomainToCrawl()
	{
		return rootDomainToCrawl;
	}

	public void setRootDomainToCrawl(String rootDomainToCrawl)
	{
		this.rootDomainToCrawl = rootDomainToCrawl;
	}
		
	public String getRootDomainToCrawlWithoutProtocol()
	{
		return rootDomainToCrawlWithoutProtocol;
	}

	public void setRootDomainToCrawlWithoutProtocol(String rootDomainToCrawlWithoutProtocol)
	{
		this.rootDomainToCrawlWithoutProtocol = rootDomainToCrawlWithoutProtocol;
	}

	public boolean isRunning()
	{
		return isRunning;
	}

	public void setRunning(boolean isRunning)
	{
		this.isRunning = isRunning;
	}

	public Long getProjectId()
	{
		return projectId;
	}

	public void setProjectId(Long projectId)
	{
		this.projectId = projectId;
	}

	public long getUserGroupId()
	{
		return userGroupId;
	}

	public void setUserGroupId(long userGroupId)
	{
		this.userGroupId = userGroupId;
	}

	public boolean isUseBasicAuth()
	{
		return useBasicAuth;
	}

	public void setUseBasicAuth(boolean useBasicAuth)
	{
		this.useBasicAuth = useBasicAuth;
	}

	public String getBasicAuthUser()
	{
		return basicAuthUser;
	}

	public void setBasicAuthUser(String basicAuthUser)
	{
		this.basicAuthUser = basicAuthUser;
	}

	public String getBasicAuthPassword()
	{
		return basicAuthPassword;
	}

	public void setBasicAuthPassword(String basicAuthPassword)
	{
		this.basicAuthPassword = basicAuthPassword;
	}

	public long getLastRunTimestamp()
	{
		return lastRunTimestamp;
	}

	public void setLastRunTimestamp(long lastRunTimestamp)
	{
		this.lastRunTimestamp = lastRunTimestamp;
	}

	public boolean isActive()
	{
		return isActive;
	}

	public void setActive(boolean isActive)
	{
		this.isActive = isActive;
	}

	public boolean isRapidCrawling()
	{
		return rapidCrawling;
	}

	public void setRapidCrawling(boolean rapidCrawling)
	{
		this.rapidCrawling = rapidCrawling;
	}

	public String getProjectName()
	{
		return projectName;
	}

	public void setProjectName(String projectName)
	{
		this.projectName = projectName;
	}

	public String getCrawlerDatabaseId()
	{
		return crawlerDatabaseId;
	}

	public void setCrawlerDatabaseId(String crawlerDatabaseId)
	{
		this.crawlerDatabaseId = crawlerDatabaseId;
	}

	public ArrayList<Integer> getInfoMessageCodes()
	{
		return infoMessageCodes;
	}

	public void setInfoMessageCodes(ArrayList<Integer> infoMessageCodes)
	{
		this.infoMessageCodes = infoMessageCodes;
	}
	/**
	 * HashMap contains databaseId (String) and timestamp (Calendar) of available crawlings.
	 * @return
	 */
	public HashMap<String,Calendar> getAvailableCrawlings()
	{
		return availableCrawlings;
	}
	/**
	 * Set HashMap of databaseId (String) and timestamp (Calendar) of available crawlings.
	 * @return
	 */
	public void setAvailableCrawlings(HashMap<String,Calendar> availableCrawlings)
	{
		this.availableCrawlings = availableCrawlings;
	}

	public boolean isUIRefreshNeccessary()
	{
		return isUIRefreshNeccessary;
	}

	public void setUIRefreshNeccessary(boolean isUIRefreshNeccessary)
	{
		this.isUIRefreshNeccessary = isUIRefreshNeccessary;
	}

	public boolean isRunMonday()
	{
		return runMonday;
	}

	public void setRunMonday(boolean runMonday)
	{
		this.runMonday = runMonday;
	}

	public boolean isRunTuesday()
	{
		return runTuesday;
	}

	public void setRunTuesday(boolean runTuesday)
	{
		this.runTuesday = runTuesday;
	}

	public boolean isRunWednesday()
	{
		return runWednesday;
	}

	public void setRunWednesday(boolean runWednesday)
	{
		this.runWednesday = runWednesday;
	}

	public boolean isRunThursday()
	{
		return runThursday;
	}

	public void setRunThursday(boolean runThursday)
	{
		this.runThursday = runThursday;
	}

	public boolean isRunFriday()
	{
		return runFriday;
	}

	public void setRunFriday(boolean runFriday)
	{
		this.runFriday = runFriday;
	}

	public boolean isRunSaturday()
	{
		return runSaturday;
	}

	public void setRunSaturday(boolean runSaturday)
	{
		this.runSaturday = runSaturday;
	}

	public boolean isRunSunday()
	{
		return runSunday;
	}

	public void setRunSunday(boolean runSunday)
	{
		this.runSunday = runSunday;
	}

	/**
	 * Returns a list of email addresses separated via comma
	 * @return list of email addresses
	 */
	public String getNotificationEmailAddresses()
	{
		return notificationEmailAddresses;
	}

	/**
	 * Sets a list of email addresses, comma separated.
	 * @param notificationEmailAddresses
	 */
	public void setNotificationEmailAddresses(String notificationEmailAddressesCommaSeparated)
	{
		this.notificationEmailAddresses = notificationEmailAddressesCommaSeparated;
	}

	public boolean isNotificationEnabled()
	{
		return notificationEnabled;
	}

	public void setNotificationEnabled(boolean notificationEnabled)
	{
		this.notificationEnabled = notificationEnabled;
	}

	public Locale getDefaultLocale()
	{
		return defaultLocale;
	}

	public void setDefaultLocale(Locale defaultLocale)
	{
		this.defaultLocale = defaultLocale;
	}

	public String getCookieName()
	{
		return cookieName;
	}

	public void setCookieName(String cookieName)
	{
		this.cookieName = cookieName;
	}

	public String getCookieValue()
	{
		return cookieValue;
	}

	public void setCookieValue(String cookieValue)
	{
		this.cookieValue = cookieValue;
	}

	public boolean isUseSetCookie()
	{
		return useSetCookie;
	}

	public void setUseSetCookie(boolean useSetCookie)
	{
		this.useSetCookie = useSetCookie;
	}

	public int getErrorCount()
	{
		return errorCount;
	}

	public void setErrorCount(int errorCount)
	{
		this.errorCount = errorCount;
	}
	
	public boolean isDisabledByError()
	{
		return disabledByError;
	}

	public void setDisabledByError(boolean disabledByError)
	{
		this.disabledByError = disabledByError;
	}

	public boolean isRemoteProject()
	{
		return remoteProject;
	}

	public void setRemoteProject(boolean remoteProject)
	{
		this.remoteProject = remoteProject;
	}

	public long getListJobLastRunTimestamp()
	{
		return listJobLastRunTimestamp;
	}

	public void setListJobLastRunTimestamp(long listJobLastRunTimestamp)
	{
		this.listJobLastRunTimestamp = listJobLastRunTimestamp;
	}

	public boolean isListCrawlingEnabed()
	{
		return listCrawlingEnabed;
	}

	public void setListCrawlingEnabed(boolean listCrawlingEnabed)
	{
		this.listCrawlingEnabed = listCrawlingEnabed;
	}

	public boolean isSiteCrawlingEnabled()
	{
		return siteCrawlingEnabled;
	}

	public void setSiteCrawlingEnabled(boolean siteCrawlingEnabled)
	{
		this.siteCrawlingEnabled = siteCrawlingEnabled;
	}

	public boolean isNewProject()
	{
		return newProject;
	}

	public void setNewProject(boolean newProject)
	{
		this.newProject = newProject;
	}

	public boolean isChangesInListCrawling()
	{
		return changesInListCrawling;
	}

	public void setChangesInListCrawling(boolean changesInListCrawling)
	{
		this.changesInListCrawling = changesInListCrawling;
	}

	public int getDiscoverLimit()
	{
		return discoverLimit;
	}

	public void setDiscoverLimit(int discoverLimit)
	{
		this.discoverLimit = discoverLimit;
	}	
}
