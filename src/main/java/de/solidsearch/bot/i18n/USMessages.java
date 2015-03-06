package de.solidsearch.bot.i18n;

import java.io.Serializable;
import java.util.ListResourceBundle;

public class USMessages extends ListResourceBundle implements Serializable
{
	private static final long serialVersionUID = -1381196948880320212L;

	public static final String OkKey = generateId();
	public static final String Edit = generateId();
	public static final String Delete = generateId();
	public static final String New = generateId();
	public static final String CancelKey = generateId();
	public static final String Save = generateId();
	public static final String Reset = generateId();
	public static final String Send = generateId();
	public static final String Activate = generateId();
	public static final String Deactivate = generateId();
	public static final String Search = generateId();
	public static final String Compare = generateId();
	public static final String Verification = generateId();
	public static final String VerifyNow = generateId();
	public static final String Close = generateId();
	
	// Application
	public static final String AppTitle = generateId();

	// LoginScreen/RegistrationScreen
	public static final String Username = generateId();
	public static final String Password = generateId();
	public static final String Login = generateId();
	public static final String LoginButton = generateId();
	public static final String RegisterNewUser = generateId();
	public static final String ForgotPassword = generateId();
	public static final String InvalidUserOrPassword = generateId();
	public static final String EmailAlreadyExists = generateId();
	public static final String ConfirmationMailSent = generateId();
	public static final String Note = generateId();
	public static final String ResetPassword = generateId();
	public static final String AccountActivation = generateId();
	public static final String PageNotFound = generateId();
	
    // UserEditor
    public static final String UserNameError = generateId();
    public static final String FirstName = generateId();
    public static final String LastName = generateId();
    public static final String RealNameError = generateId();
    public static final String InvalidPasswordFormat = generateId();
    public static final String PasswordAgain = generateId();
    public static final String PasswordsDifferent = generateId();
    public static final String Email = generateId();
    public static final String InvalidEmail = generateId();
    public static final String MustBeGiven = generateId();
    public static final String InputToLong = generateId();
	public static final String PleaseAcceptTermsAndConditions = generateId();
        
    // URLTableView
    public static final String ItemCount = generateId();
    public static final String Page = generateId();
    
    public static final String URL_table = generateId();
    public static final String HTTPStatuscode = generateId();
    public static final String MetaRobotsIndex = generateId();
    public static final String MetaRobotsFollow= generateId();
    public static final String InternalLinksOnThisPage = generateId();
    public static final String ExternalLinksOnThisPage = generateId();
    public static final String DepthFromDomainRoot = generateId();
    public static final String ResponseTime_table = generateId();
    public static final String FoundAtURL = generateId();
    public static final String Title = generateId();
    public static final String MetaDescription = generateId();
    public static final String H1 = generateId();
    public static final String H2 = generateId();
    public static final String H3 = generateId();
    public static final String Timeout = generateId();
    public static final String RedirectedToURL = generateId();
    public static final String ContentHashcode = generateId();
    public static final String CanonicalTag = generateId();
    public static final String PageSize = generateId();
    public static final String NofollowLinkToThis = generateId();
    public static final String FollowLinkToThis = generateId();
    public static final String FirstAnchor = generateId();
    public static final String Host = generateId();
    public static final String Nofollow = generateId();
    public static final String SearchStringInvalid = generateId();
    public static final String Analyses = generateId();
    public static final String LinkTargets = generateId();
    public static final String Open = generateId();
	public static final String Notations = generateId();
	public static final String TotalURLs = generateId();
	public static final String DisableComparison = generateId();
	public static final String Show_Keywords = generateId();
	public static final String Show_Solidsearchscore = generateId();
	public static final String Open_URL = generateId();
	
	// ProjectView
    public static final String ProjectName = generateId();
    public static final String ProjectNameInvalid = generateId();
	public static final String CrawlingDepth = generateId();
	public static final String CountToCrawl = generateId();
	public static final String RapidCrawling = generateId();
	public static final String IgnoreImages = generateId();
	public static final String IgnoreCSS = generateId();
	public static final String IgnoreJS = generateId();
	public static final String IgnoreRobotsTxt = generateId();
	public static final String IgnoreInternalNofollow = generateId();
	public static final String BotUserAgent = generateId();
	public static final String URL = generateId();
	public static final String URLInvaild = generateId();
	public static final String CountToCrawlInvaild = generateId();
	public static final String CrawlingDepthInvaild = generateId();
	public static final String UseBasicAuth = generateId();
	public static final String SetCookie = generateId();
	public static final String BasicAuthUser = generateId();
	public static final String BasicAuthPassword = generateId();
	public static final String Name = generateId();
	public static final String Value = generateId();
	public static final String MondayShort = generateId();
	public static final String TuesdayShort = generateId();
	public static final String WednesdayShort = generateId();
	public static final String ThursdayShort = generateId();
	public static final String FridayShort = generateId();
	public static final String SaturdayShort = generateId();
	public static final String SundayShort = generateId();
	public static final String ActiveDays = generateId();
	public static final String PleaseEnableAtLeastOneDays = generateId();
	public static final String CrawlingIsRunning = generateId();
	public static final String VerificationText1 = generateId();
	public static final String VerificationText2 = generateId();
	public static final String VerificationSuccessful = generateId();
	public static final String VerificationNotSuccessful = generateId();
	public static final String NeedToVerify = generateId();
	public static final String Notifications = generateId();
	public static final String EmailInvailidOrToMany = generateId();
	
	// NavigationTree
	public static final String Dashboard = generateId();
	public static final String ProjectSettings = generateId();
	public static final String ChooseProject = generateId();
	public static final String Date = generateId();
	public static final String MaxProjectsReached = generateId();
	public static final String NewProject = generateId();
	public static final String NewCrawlingsAvailable = generateId();
	public static final String Logout = generateId();
	public static final String DeleteProject = generateId();
	public static final String ReallyDeleteProject = generateId();
	public static final String NoDataForGivenDate = generateId();
	
	//Download Dialog
	public static final String DownloadDialogTitle = generateId();
	public static final String DownloadDialogMsg = generateId();
	
	
	// NavigationTree / Monitoring
	public static final String Monitoring = generateId();
	public static final String Indexability = generateId();
	public static final String Crawlability = generateId();
	public static final String Quality = generateId();
	
	// NavigationTree / Analysis
	public static final String Analysis = generateId();
	public static final String Pages = generateId();
	// NavigationTree / Alarms
	public static final String Alarms = generateId();
	public static final String Administration = generateId();
	public static final String Users = generateId();
    // NavigationTree / Pagesize
	public static final String PageSizeClean = generateId();
	
	// IndexabilityChartView
	public static final String PageCount = generateId();
	public static final String RobotsIndex = generateId();
	public static final String RobotsNoindex = generateId();
	public static final String Day = generateId();
	
	// CrawlabilityChartView
	public static final String RobotsFollow = generateId();
	public static final String RobotsNofollow = generateId();
	
	//Dashboard
	public static final String Optimizations = generateId();
	public static final String MissingTitle = generateId();
	public static final String LongTitle = generateId();
	public static final String DuplicateContent = generateId();
	public static final String LowContent = generateId();
	public static final String MissingContent = generateId();
	public static final String MissingMetaDescription = generateId();
	public static final String LongMetaDescription = generateId();
	public static final String Statistics = generateId();
	public static final String LastRun = generateId();
	public static final String Duration = generateId();
	public static final String AnalyzedURLs = generateId();
	public static final String FoundInternalURLs = generateId();
	public static final String ServerErrors = generateId();
	public static final String ClientErrors = generateId();
	public static final String Timeouts = generateId();
	public static final String BrokenLinks = generateId();
	public static final String Availability = generateId();
	public static final String AvgResponseTime = generateId();
	public static final String AvgPageSize = generateId();
	public static final String AvgPageSizeSegment = generateId();
	public static final String AvgPageSize2 = generateId();
	public static final String URLs = generateId();
	public static final String CanonicaltoSource = generateId();
	public static final String CanonicalIssues = generateId();
	public static final String MissingH1 = generateId();
	public static final String HeadlinesNotInOrder = generateId();
	public static final String DuplicateTitle = generateId();
	public static final String DuplicateMetaDescription = generateId();
	public static final String DuplicateH1 = generateId();
	public static final String ExternalInternalRelation = generateId();
	public static final String ExternalInternalRelationDifferentDomains = generateId();
	public static final String ExternalLinkRatio = generateId();
	public static final String IndexablePages = generateId();
	public static final String ExternalLinks = generateId();
	public static final String ExternalLinksDifferentDomains = generateId();
	public static final String Overview = generateId();
	public static final String Solidsearchscore = generateId();
	public static final String TotalLinks = generateId();
	public static final String DifferentURLSameAnchor = generateId();
	public static final String All = generateId();
	public static final String FollowLinks = generateId();
	public static final String NofollowLinks = generateId();
	public static final String Segments = generateId();
	public static final String URLSegment = generateId();
	public static final String URLSegmentNotFound = generateId();
	public static final String MissingGoogleAnalyticsCode = generateId();
	public static final String Keywords = generateId();
	public static final String Keywordsearch = generateId();
	public static final String Keywords_short_term = generateId();
	public static final String RelevantKeywords_multi_term = generateId();
	public static final String Keyword = generateId();
	public static final String Weight = generateId();
	public static final String Total = generateId();
	public static final String No = generateId();
	public static final String ReadingLevel = generateId();
	public static final String VarietyTopicScore = generateId();
	public static final String RelevantKeywords = generateId();
	public static final String TrailingSlashIssues = generateId();
	public static final String GzipIssues = generateId();
	public static final String KeywordOrientationShortTerm = generateId();
	public static final String KeywordOrientationTwoTerms = generateId();
	
	// StructureChart
	public static final String Structure = generateId();
	
	// InternalLinksChart
	public static final String InternalLinks = generateId();
	
	// HTTPStatusChart
	public static final String HTTPStatus = generateId();
	public static final String ClientErrors4xx = generateId();
	public static final String ServerErrors5xx = generateId();
	public static final String Redirections3xx = generateId();
	
	// ResponseTimeChart
	public static final String ResponseTime = generateId();
	public static final String AvgResponseTime2 = generateId();
	public static final String AvgResponseTime3 = generateId();
	public static final String AvgResponseTimeSegment = generateId();
	
	// ConfirmationView
	public static final String AccountConfirmation = generateId();
	public static final String ActivationSuccess = generateId();
	public static final String ActivationFailed = generateId();
	
	// PasswordEditor
	public static final String PasswordChangedMailSent = generateId();
	
	// ResetPasswordView
	public static final String PasswordWasChanged = generateId();
	public static final String PasswordCannotChanged = generateId();
	public static final String PasswordResetInstructions = generateId();
	
	// Emails
	public static final String EmailRegistrationHeader = generateId();
	public static final String EmailRegistrationFooter = generateId();
	public static final String EmailResetPasswordHeader = generateId();
	public static final String EmailResetPasswordFooter = generateId();
	
	// ProjectWarnings
	public static final String InvalidProjectURL = generateId();
	public static final String RobotsTxtNotFound = generateId();
	public static final String NotEnougthURLs = generateId();
	public static final String WaitForShutdown = generateId();
	public static final String SiteIsVerySlow = generateId();
	public static final String BotisBlocked = generateId();
	public static final String CouldNotFindVerificationFile = generateId();
	public static final String MonitoringWasStopped = generateId();
	public static final String WeeklyBudgetConsumed = generateId();
	public static final String CouldNotResolveHostname = generateId();
	public static final String ProjectError = generateId();
	
	// ErrorView
	public static final String ErrorPageOccured = generateId();
	
	// HelpText
	public static final String HelpUrl = generateId();
	public static final String HelpDepth = generateId();
	public static final String HelpIgnoreRobotsTxt = generateId();
	public static final String HelpActiveDays = generateId();
	public static final String HelpURLLimit = generateId();
	public static final String HelpRapidCrawling = generateId();
	public static final String HelpIgnoreNofollow = generateId();
	public static final String HelpHTTPAuth = generateId();
	public static final String HelpSetCookie = generateId();
	public static final String HelpUserAgent = generateId();
	public static final String HelpNotifications = generateId();
	public static final String HelpNotificationEmailAddresses = generateId();
	// help for KPIs
	public static final String HelpAll = generateId();
	public static final String HelpMissingTitles = generateId();
	public static final String HelpLongTitles = generateId();
	public static final String HelpDuplicateContent = generateId();
	public static final String HelpLowContent = generateId();
	public static final String HelpMissingMetaDescription = generateId();
	public static final String HelpLongMetaDescription = generateId();
	public static final String HelpDuration = generateId();
	public static final String HelpAnalyzedURLs = generateId();
	public static final String HelpFoundInternalURLs = generateId();
	public static final String HelpClientErrors = generateId();
	public static final String HelpAvgResponseTime = generateId();
	public static final String HelpAvgPageSize = generateId();
	public static final String HelpCanonicaltoSource = generateId();
	public static final String HelpCanonicalIssues = generateId();
	public static final String HelpMissingH1 = generateId();
	public static final String HelpHeadlinesNotInOrder = generateId();
	public static final String HelpDuplicateTitles = generateId();
	public static final String HelpDuplicateMetaDescription = generateId();
	public static final String HelpDuplicateH1 = generateId();
	public static final String HelpQualityscore = generateId();
	public static final String HelpExternalLinks = generateId();
	public static final String HelpLinkTargets = generateId();
	public static final String HelpPageSize = generateId();
	public static final String HelpRedirections3xx = generateId();
	public static final String HelpRobotsFollow = generateId();
	public static final String HelpRobotsIndex = generateId();
	public static final String HelpRobotsNofollow = generateId();
	public static final String HelpRobotsNoindex = generateId();
	public static final String HelpServerErrors5xx = generateId();
	public static final String HelpAvailability = generateId();
	public static final String HelpDifferentURLSameAnchor = generateId();
	public static final String HelpMissingGoogleAnalyticsCode = generateId();
	
	// ProjectInfo enum
	public static final String NoInformationOrAdvices = generateId();
	public static final String SignificantChanges = generateId();
	public static final String ListChanges = generateId();
	
	// AlarmEmail
	public static final String AlarmHeader = generateId();
	public static final String AlarmSubject = generateId();
	public static final String NotificationFor = generateId();
	public static final String NofollowLinksSegment = generateId();
	public static final String FollowLinksSegment = generateId();
	public static final String ProjectDisabledDueErrors = generateId();
	
	//KeyworddistributionView
	public static final String AllKeywords = generateId();
	public static final String KeywordsByDepth = generateId();
	public static final String TotalKeywords = generateId();
	
	//URLMonitoringView
	public static final String Add = generateId();
	public static final String URLMonitoring = generateId();
	public static final String AddURLs = generateId();
	public static final String ShowURLs = generateId();
	public static final String AddURLsByLine = generateId();
	public static final String LastCheck = generateId();
	public static final String Change = generateId();
	public static final String HttpStatus = generateId();
	public static final String Robots = generateId();
	public static final String URLAlreadyExists = generateId();
	public static final String CrawlingMode = generateId();
	public static final String Site = generateId();
	public static final String List = generateId();
	public static final String PleaseChooseCrawlingMode = generateId();
	public static final String URLDoesNotMatchToProjectURL = generateId();
	public static final String Overwrite = generateId();
	public static final String StatusCode = generateId();
	public static final String Other = generateId();
	public static final String Details = generateId();
	public static final String Status = generateId();
	public static final String Desired = generateId();
	public static final String Received = generateId();
	public static final String ChangeInListCrawling = generateId();
	public static final String RobotsTxt = generateId();
	public static final String ReallyDeleteList = generateId();
	public static final String ReallyOverwriteAll = generateId();
	public static final String DeleteList = generateId();
	
	public static String generateId()
	{
		return new Integer(ids++).toString();
	}

	static int ids = 0;

	@Override
	public Object[][] getContents()
	{
		return null;
	}
}
