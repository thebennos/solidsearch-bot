package de.solidsearch.bot.i18n;

import java.util.ResourceBundle;

public class USMessages_en extends USMessages {
    private static final long serialVersionUID = -7071215163996244034L;
    
    final static ResourceBundle bundle = ResourceBundle.getBundle("qualitywatch");
    
    @Override
    public Object[][] getContents() {
        return contents_en;
    }
    static final Object[][] contents_en = {
        {OkKey, "OK"},
        {Delete, "Delete"},
        {Edit, "Edit"},
        {New, "New"},
        {CancelKey, "Cancel"},
        {Save, "Save"},
        {Reset, "Reset"},
        {Send, "Send"},
        {Activate, "Activate"},
        {Deactivate, "Deactivate"},
        {Search, "Search"},
        {Compare, "Compare with:"},
        {Verification, "Verification"},
        {VerifyNow, "Verify now"},
        {Close, "Close"},
        
        // Application
        {AppTitle, "Qualitywatch"},
        
        // LoginScreen/RegistrationScreen
        {Username, "Username"},
        {Password, "Password"},
        {Login, "Login"},
        {LoginButton, "Login"},
        {RegisterNewUser, "Register a new account"},
        {ForgotPassword, "Forgot your password?"},
        {InvalidUserOrPassword, "Invalid user or password"},
        {EmailAlreadyExists, "Given email already in use"},
        {ConfirmationMailSent, "<img align=\"middle\" src=\"" + bundle.getString("application.icon.path.medium") + "1352398401_information-balloon_yellow.png \">We have just sent a confirmation email. Please use the link to confirm your email address."},
        {Note, "Note"},
        {ResetPassword, "Reset password"},
        {AccountActivation, "User account activation"},
        {PageNotFound, "Page not found"},

        
        // UserEditor
        {UserNameError, "Invalid username."},
        {FirstName, "First name"},
        {LastName, "Last name"},
        {RealNameError, "Firstname or Name should not be empty."},
        {InvalidPasswordFormat, "Password must be 6 characters long."},
        {PasswordAgain, "Password again"},
        {PasswordsDifferent, "Please check password. Passwords are different."},
        {Email, "Email Address"},
        {InvalidEmail, "Invalid email address"},
        {MustBeGiven, "Input is empty or to long"},
        {InputToLong, "Input is to long"},
        {PleaseAcceptTermsAndConditions, "Please accept terms and conditions for registration."},
        
        // URLTableView
        {ItemCount, "URLs per page:"},
        {Page, "Page"},
        {URL_table, "URL"},
        {HTTPStatuscode, "HTTP status code"},
        {MetaRobotsIndex, "robots INDEX"},
        {MetaRobotsFollow, "robots FOLLOW"},
        {InternalLinksOnThisPage, "Incoming internal links"},
        {ExternalLinksOnThisPage, "Outgoing external links"},
        {DepthFromDomainRoot, "Level"},
        {ResponseTime_table, "Response time"},
        {FoundAtURL, "Found at"},
        {Title, "Title"},
        {MetaDescription, "Meta description"},
        {H1, "H1"},
        {H2, "H2"},
        {H3, "H3"},
        {Timeout, "Timeout"},
        {RedirectedToURL, "Redirected to"},
        {ContentHashcode, "Content key"},
        {CanonicalTag, "Canonical tag"},
        {PageSize, "Page size (KB)"}, 
        {NofollowLinkToThis, "Incoming nofollow links"},   
        {FollowLinkToThis, "Incoming follow links"},
        {FirstAnchor, "1st incoming anchor text"},
        {Host, "Host name"},
        {Nofollow, "Nofollow"},
        {SearchStringInvalid, "Input contains invalid characters or is too long."},
        {Analyses, "Analyses"},
        {LinkTargets, "Link targets"},
        {CrawlingIsRunning, "Crawling is running. \"Deactivate\" interrupts the current analysis."},
        {Open , "Open"},
        {Notations, "Notations"},
        {TotalURLs, "Total URLs: "},
        {DisableComparison, "Disable comparison"},
        {Show_Keywords, "Show keywords"},
        {Show_Solidsearchscore, "Show qualityscore"},
        {Open_URL, "Open URL"},
        
        // ProjectView
        {ProjectName, "Project name:"},
        {ProjectNameInvalid, "Project name must be 2-25 chars or numbers."},
        {CrawlingDepth, "Depth:"},
        {CountToCrawl, "URL limit per day:"},
        {RapidCrawling, "Rapid crawling"},
        {IgnoreImages, "Ignore images"},
        {IgnoreCSS, "Ignore CSS"},
        {IgnoreJS, "Ignore Javascript"},
        {IgnoreRobotsTxt, "Ignore robots.txt"},
        {IgnoreInternalNofollow, "Ignore nofollow"},
        {BotUserAgent, "User agent:"},
        {URL, "URL:"},
        {URLInvaild, "Given URL is not valid. Example: http://www.example.com"},
        {CountToCrawlInvaild, "Input invalid. Only values ​​between 1-1000000 possible."},
        {CrawlingDepthInvaild, "Input invalid. Only values ​​between 1-20 possible."},
        {UseBasicAuth, "HTTP authentication"},
        {SetCookie, "Set cookie"},
        {BasicAuthUser, "Username"},
        {BasicAuthPassword, "Password"},
        {Name, "Name"},
        {Value, "Value"},
        {MondayShort, "Mon"},
        {TuesdayShort, "Tue"},
        {WednesdayShort, "Wed"},
        {ThursdayShort, "Thu"},
        {FridayShort, "Fri"},
        {SaturdayShort, "Sat"},
        {SundayShort, "Sun"},
        {ActiveDays, "Active days:"},
        {PleaseEnableAtLeastOneDays, "Please enable at least one day."},
        {VerificationText1, "<img align=\"middle\" src=\"" + bundle.getString("application.icon.path.small") + "1352398401_information-balloon_yellow.png \"> To use all features of Qualitywatch to, you must check the ownership of your site. <br/><br/> 1. Please upload to the following confirmation-file on your server."},
        {VerificationText2, "2. Please choose \"Verify now\" to finish, if you can request the following link."},
        {VerificationSuccessful, "<img align=\"middle\" src=\"" + bundle.getString("application.icon.path.medium") + "1352398424_button-check_green.png \">We have successfully verified your ownership."},
        {VerificationNotSuccessful, "<img align=\"middle\" src=\"" + bundle.getString("application.icon.path.medium") + "1352398436_exclamation-circle_red.png \">We could not find the verification file."},
        {NeedToVerify, "To use this feature, you need to verify ownership of your site."},
        {Notifications, "Notifications"},
        {EmailInvailidOrToMany, "Invalid or too many email addresses."},
        
    	// NavigationTree
        {Dashboard, "Dashboard"},
        {ProjectSettings, "Project Settings"},
        {ChooseProject, "Choose project:"},
        {Date, "Date:"},
        {MaxProjectsReached, "Maximum number of projects reached"},
        {NewProject, "New project"},
        {NewCrawlingsAvailable, "<img align=\"middle\" src=\"" + bundle.getString("application.icon.path.medium") + "1352398424_button-check_green.png \">New crawl data are available."},
        {Logout, "Logout"},
        {DeleteProject, "Delete project"},
        {ReallyDeleteProject, "<img align=\"middle\" src=\"" + bundle.getString("application.icon.path.medium") + "1352398106_exclamation-circle-frame_red.png \">Do you really want to delete all data of this project?"},
        {NoDataForGivenDate, "<img align=\"middle\" src=\"" + bundle.getString("application.icon.path.medium") + "1352398401_information-balloon_yellow.png \">There are no data available for the selected date!"},
        
        //CSV export & file download
        {DownloadDialogTitle, "CSV report download"},
        {DownloadDialogMsg, "Should the following report be downloaded?"},
        
    	// NavigationTree / Monitoring
        {Monitoring, "Monitoring"},
        {Indexability, "Indexability"},
        {Crawlability, "Crawlability"},
        {Quality, "Quality"},
    	// NavigationTree / Analysis
        {Analysis, "Analysis"},
        {Pages, "Pages"},
    	// NavigationTree / Alarms
        {Alarms, "Alarms"},
         // NavigationTree / Administration
        {Administration, "Administration"},
        {Users, "Users"},
        // NavigationTree / Pagesize
        {PageSizeClean, "Page size"},

        // IndexabilityChartView
        {PageCount, "page count"},
        {RobotsIndex, "robots INDEX"},
        {RobotsNoindex, "robots NOINDEX"},
        {Day, "day"},
        
        // CrawlabilityChartView
        {RobotsFollow, "robots FOLLOW"},
        {RobotsNofollow, "robots NOFOLLOW"},
        
        // Dashboard
        {Optimizations, "Optimization"},
        {MissingTitle, "Missing pagetitle"},
        {LongTitle, "Pagetitle to long"},
        {DuplicateContent, "Duplicate content"},
        {LowContent, "Low content"},
        {MissingContent, "Missing content"},
        {MissingMetaDescription, "Missing meta description"},
        {LongMetaDescription, "Meta description to long"},
        {Statistics, "Statistics"},
        {LastRun, "Last run"},
        {Duration, "Duration"},
        {AnalyzedURLs, "Analyzed internal URLs"},
        {FoundInternalURLs, "Found internal URLs"},
        {BrokenLinks, "Broken links"},
        {ServerErrors, "Server errors"},
        {ClientErrors, "Client errors"},
        {Timeouts, "Timeouts"},
        {Availability, "Availability"},
        {AvgResponseTime, "Average response time"}, 
        {AvgPageSizeSegment, "Average response time in URL Segment:"},
        {AvgPageSize, "Average page size"},
        {AvgPageSize2, "&#216; page size"},
        {URLs, "URLs"},
        {CanonicaltoSource, "Canonical tag to source"},
        {CanonicalIssues, "Canonical tag issues"},
        {MissingH1, "Missing H1"},
        {HeadlinesNotInOrder, "Headlines not in order"},
        {DuplicateTitle, "Duplicate title"},
        {DuplicateMetaDescription, "Duplicate meta description"},
        {DuplicateH1, "Duplicate H1"},
        {ExternalInternalRelation, "External link / pages relation"},
        {ExternalInternalRelationDifferentDomains, "External domains / pages relation"},
        {ExternalLinkRatio, "External link ratio"},
        {IndexablePages, "Indexable pages"},
        {ExternalLinks, "Number of external links"},
        {ExternalLinksDifferentDomains, "External links <br>(different domains)"},
        {Overview, "Overview"},
        {Solidsearchscore, "Qualityscore"},
        {TotalLinks, "Total number of links"},
        {DifferentURLSameAnchor, "Different URLs same anchor text"},
        {All, "All"},
        {FollowLinks, "Follow links"},
        {NofollowLinks, "Nofollow links"},
        {Segments, "Segmente"},
        {URLSegment, "URL segment"},
        {URLSegmentNotFound, "The following URL segments could not be found:"},
        {MissingGoogleAnalyticsCode, "Missing Google Analytics code"},
        {Keywords, "Keywords"},
        {Keywordsearch, "Keywordsearch"},
        {Keywords_short_term, "All keywords (1-t)"},
        {RelevantKeywords_multi_term, "Rel. keywords (n-t)"},
        {Keyword, "Keyword"},
        {Weight, "Weight"},
        {Total, "Total"},
        {No, "No "},
        {ReadingLevel, "Readinglevel"},
        {VarietyTopicScore, "Variety Topic Score"},
        {RelevantKeywords, "Relevant keywords"},
        {TrailingSlashIssues, "Trailing slash issues"},
        {GzipIssues, "GZIP issues"},
        {KeywordOrientationShortTerm, "Keyword Orientation (Short Term)"},
        {KeywordOrientationTwoTerms, "Keyword Orientation (Two Terms)"},
        
        // Structure
        {Structure, "Structure"},
        
        // InternalLinksChart
        {InternalLinks, "Internal links"},
        
        // HTTPStatusChart
        {HTTPStatus, "HTTP status codes"},
        {ClientErrors4xx, "Client errors (4xx)"},
        {ServerErrors5xx, "Server errors (5xx)"},
        {Redirections3xx, "Redirections (3xx)"},
        
        // ResponseTimeChart
        {ResponseTime, "Response time"},
        {AvgResponseTime2, "&#216; response time"},
        {AvgResponseTime3, "&#216; total response time"},
        {AvgResponseTimeSegment, "Average response time in URL segment:"},
     
        // ConfirmationView
        {AccountConfirmation, "Account confirmation"},
        {ActivationSuccess, "<h1><img align=\"top\" src=\"" + bundle.getString("application.icon.path.medium") + "1352398424_button-check_green.png \">Activation successful</h1> Your account has been successfully activated. You can now <a href=\"" + bundle.getString("application.url") +  "#!login\">login</a>."},
        {ActivationFailed, "<h1><img align=\"top\" src=\"" + bundle.getString("application.icon.path.medium") + "1352398436_exclamation-circle_red.png \">Activation key expired</h1> Sorry, your activation key has expired. Please register <a href=\"" + bundle.getString("application.url") +  "#!registration\">again</a> with us."},
   
        // PasswordEditor
        {PasswordChangedMailSent, "<img align=\"middle\" src=\"" + bundle.getString("application.icon.path.medium") + "1352398401_information-balloon_yellow.png \">We have just sent a email to you. Please use the link to change your password."},
        // ResetPassword View
        {PasswordWasChanged, "<img align=\"middle\" src=\"" + bundle.getString("application.icon.path.medium") + "1352398424_button-check_green.png \">Your password was changed successfully. You can now login again."},
        {PasswordCannotChanged, "<h1><img align=\"top\" src=\"" + bundle.getString("application.icon.path.medium") + "1352398436_exclamation-circle_red.png \">Password not changed</h1> Your password could not be reset. Please <a href=\"" + bundle.getString("application.url") +  "#!login\">try</a> again."},
        {PasswordResetInstructions, "Please type in your email address and click \"Send\". We'll send you an email with instructions to reset your password."},
        
        // Registration Email
        {EmailRegistrationHeader, "Thank you for registering with us.\n\nPlease click on the following link to activate your account:"},
        {EmailRegistrationFooter, "If you have not even registered with us, you can ignore this email and delete."},
        {EmailResetPasswordHeader, "You have requested from us a new password. \n\nPlease click on the following link to change your password:"},
        {EmailResetPasswordFooter, "If you have not even requested a new password, you can ignore and delete this email."},
        
        // Project ProjectWarnings
        {InvalidProjectURL, "The project URL is incorrect or unreachable."},
        {RobotsTxtNotFound, "Robots.txt not found."},
        {NotEnougthURLs, "No URLs found. Domain possibly locked."},
        {WaitForShutdown, "Current project will be completed. Please wait."},
        {SiteIsVerySlow, "Page performance is very poor. Please disable any security plugins (e.g. mod_security) if exist."},
        {BotisBlocked, "The robots.txt instructions of the side block our bot."},
        {CouldNotFindVerificationFile, "The verification file was not found. Please lead the verification process again."},
        {MonitoringWasStopped, "The monitoring was stopped. For automatic daily monitoring please verify the ownership."},
        {WeeklyBudgetConsumed, "The weekly budget is consumed. The crawling was finished."},
        {CouldNotResolveHostname, "Could not resolve hostname."},
        {ProjectError, "The project could not be completed due to an error."},
        
        // ErrrorView
        {ErrorPageOccured, "The requested site is not available."},
        
        // HelpText
    	{HelpUrl, "Here you have the entry-level URL, where our crawler has to start from. The URL can be set to any point, but must be accessible and not blocked by the robots.txt. The calculation of the page depth is always based on this URL. It is necessary to specify the protocol. (http://www.example.com)"},
    	{HelpDepth, "Starting with the entry-level URL, this value controls the maximum depth to which our crawler will analyze the page. Limiting the depth helps to focus the crawl budget on relevant pages."},
    	{HelpIgnoreRobotsTxt, "When crawling a page we follow the instructions of the robots.txt. We follow the instructions that are set for all crawlers (*) or Googlebot. With activation of the switch all the robots.txt instructions are ignored. The switch can be activated only if the page has been previously verified."},
    	{HelpActiveDays, "Here you can set on which days the crawl runs. Depending on how many pages are to be analyzed, it may be that it is not possible to start on two consecutive days a crawl. A notice will be shown to you. If the weekly budget is used up, our crawler will automatically stop and terminates the current run. <br><b>Note:</b><br>For automatic daily monitoring, you need to verify the ownership. Otherwise, you can only start the crawling manually with \"Activate\" button."},
    	{HelpURLLimit, "The URL limit per day indicates how many pages the given day our crawler have to analyze. The duration how long it takes, is this influenced by the following factors: 1. Number of pages (URL limit per day), 2. Performance of the analyzed page, 3. Speed of crawl. Because the duration is composed of many individual factors, you should always start a first crawl with smaller values. For example, a first result, with normal speed for 10,000 URLs should already present in about 1 hour."},
    	{HelpRapidCrawling, "Our crawler analyzes the specified page with a speed of about 2 requests per second. With the activation of this switch, the speed is increased to about 4 requests per second. In general ordinary servers will not have problems with the frequency of our requests. Additionally our crawler also monitors your site performance constantly and stops running, if it sees strong performance degradation. Nevertheless, we recommend using the quick test-crawl to ensure that your server can handle the increased load. The switch can be activated only if the page has been previously verified."},
    	{HelpIgnoreNofollow, "The NOFOLLOW instructions for pages and links are used by crawlers such as Google to show them a more efficient path through your site. In this way the valuable and limited crawling budget is focused on important and relevant pages. Nerveless if you want to crawl these less relevant pages, you can tell our crawler here to ignore NOFOLLOW instructions."},
    	{HelpHTTPAuth, "Our crawler is able to perform a \"HTTP Basic Authentication\". This function can be used to crawl sites which are available for public. So you can use this feature to analyze, a test version of your page that should not available for Customers, Google or other search engines."},
    	{HelpSetCookie, "Our crawler is able to send  an individual cookie in the HTTP header with each request. The cookie data you can enter here."},
    	{HelpUserAgent, "With this option, you can set different user agents within the HTTP header. In this way you can check the behavior of mobile devices of your site. You can only use other user agents, if you have previously verified your site."},
    	{HelpNotifications, "With this option, you can disable all email notifications for abnormalities which we have found after crawling."},
    	{HelpNotificationEmailAddresses, "Here you can enter up to 10 email-destinations. The addresses must be separated with a comma."},
    	
    	// help for KPIs
    	{HelpAll, "This view shows all the data of our analysis."},
    	{HelpMissingTitles, "The page title is still a very important criterion for a search engine to classify the content of the page. In addition, the page title will be used to build the snippet of the SERPs. If omitted entirely, it is up to the search engine what is shown here. To achieve the best click-through rates, the page title should always be synchronized to the content of the landing page"},
    	{HelpLongTitles, "An attractive title which is tailored to the target keyword, achieves better values ​​in click-through rate in the SERPs. If the title is too long, it will be automatically cut from the search engine. Depending on where the title is cut off, it can have a negative impact on the click-through rate."},
    	{HelpDuplicateContent, "This indicator is activated when large parts or the whole page text is used on other landing pages. To define landing pages and increase the relevance of each page, it is important to create unique content as possible."},
    	{HelpLowContent, "If a landing page has small or no machine readable text, it is difficult for a search engine to classify the page content. As a result, the page might lose relevance, and similar pages with more content are preferred. The classification is purely quantitative. For not enabling this indicator, the page must have contiguous blocks of text with at least 165 characters, which total yield in the more than 300 characters, and more than two sentences."},
    	{HelpMissingMetaDescription, "A missing meta description leads a search engine to choose independently text, which they somewhere found on the page. This often leads to a text excerpt, which had bad effect on user experience and click-through rates in the SERPs."},
    	{HelpLongMetaDescription, "An appealing meta description achieved better click-through rate values in the SERPs. If the meta description is too long, it will automatically cut from the search engine. Depending on where it is cut off, it can have a negative impact on the click-through rate."},
    	{HelpDuration, " "},
    	{HelpAnalyzedURLs, " "},
    	{HelpFoundInternalURLs, " "},
    	{HelpServerErrors5xx, "This indicator is activated when the server responds with a HTTP status code of 5xx at crawling time. Server errors indicate usually a technical problem and should be corrected immediately. If the number of server errors in relation to the amount of all pages increases, the likelihood increases that users gets a server error. Since search engines want to deliver error-free SERPs destinations, server errors should be kept to a minimum."},
    	{HelpClientErrors, "Dieser Indikator wird aktiviert, wenn der Server beim Crawling einer Seite mit einem HTTP Statuscode von 4xx antwortet. Client Fehler deuten auf einen kaputten oder veralteten Link hin. Bei einer hohen Anzahl von Client Fehlern im Verhältnis zur Seitengröße steigt die Wahrscheinlichkeit, dass auch User irgendwann auf einen Client Fehler treffen. Da Suchmaschinen fehlerfreie SERPs-Ziele für ihre User ausliefern möchten, sollte die Anzahl möglichst gering gehalten werden."},
    	{HelpAvgResponseTime, " "},
    	{HelpAvgPageSize, " "},
    	{HelpCanonicaltoSource, "A canonical tag is considered like a redirect. If the canonical tag show on itself, it’s logically not correct. Additionally, this represents an unnecessary link, which has to deal by the search engine."},
    	{HelpCanonicalIssues, "A canonical tag should only be set to a valid and indexable page. If we determine that a canonical tag points to a page that replies not with an HTTP status code 200 or is on noindex, we posted this note."},
    	{HelpMissingH1, "The H1 headline is a first indication for users to understand what content is on the page. Important keywords of the page should to be focused here. If this information does not exist, it is difficult for users to gather the content quickly and identify with their search. A speaking and simple headline affects overall a positive trend on the bounce rate."},
    	{HelpHeadlinesNotInOrder, "The outline of a page helps the user to understand the structure quickly and easily. Incorrect sequences indicates a problem in the structure of the landing page, which may effect the bounce rate negatively."},
    	{HelpDuplicateTitles, "If the page title is used on a different landing page again, this indicator is activated. To define landing pages and increase the relevance of each page, it is important to create unique content as possible. Since the page title is a very important factor to classify the content, you should minimize duplicates as possible."},
    	{HelpDuplicateMetaDescription, "A meta description which is used on a different landing page again, activates this indicator. To define landing pages and increase the relevance of each page, it is important to create unique content as possible."},
    	{HelpDuplicateH1, "If the text of an H1 headline is used on a different landing page again, this indicator is activated. To define landing pages and increase the relevance of each page, it is important to create unique content as possible. Since the H1 headline is still an important factor to classify the content, you should minimize existing duplicates as possible."},
    	{HelpQualityscore, "The Qualityscore is a rating system developed by us, to find pages with very good onpage factors. So you can quickly find excellent pages, which can be highlighted with advanced measures. The Qualityscore has a value range of 0 to 100 points, where 100 indicates the maximum.<br/> <br/> The Qualityscore is composed as follows:<br/> <br/> Page titles available (+11) <br/> Meta description available (+5)<br/> H1 available (+6)</br> Page title is not too long (+2) <br/> Meta description is not too long (+2)<br/> No low content (+9) <br/> No duplicate title (+9)<br/> No duplicate content (+ 10)<br/> No duplicate H1 (+8)<br/> Headlines are in order (+4)<br/> No canonical tag problems (+2)<br/> More internal links than average (+9)<br/> >85% of max internal links (+9)<br/> 0-1 level after root (+1)<br/> 0-2 level after root (+2)<br/> 0-3 level after root (+3)<br/> 0-4 level after root (+4)<br/> No more than +2 external links from the average (+10)<br/>"},
    	{HelpExternalLinks, "Links to external destinations are a natural way to reference to external content. An external destination, which content expandes the own page content, tends to be a good external link. The pure quantitative analysis of external links on a page can sometimes provide hints where too many non-relevant external links are set."},
    	{HelpLinkTargets, "Internal links are natural way to show, which content of a whole site is relevant. If a page has only a few or no internal links, there is a high probability that users do not find this page. With more internal linking the probability that this page is discovered by users increases, which also affects positive to the page relevance. The pure quantitative approach is a simple approach to identify remaining weaknesses in the site structure. A weighting of the internal linking analogous to PageRank algorithm does not take place."},
    	{HelpPageSize, "The page size refers to the raw HTML code and all embedded elements within, which was delivered by the server. This value does not include reloaded images, scripts or style definitions. (e.g., Javascript or CSS)"},
    	{HelpRedirections3xx, "This indicator is activated when the server responses with a HTTP status code of 3xx at crawling time. Referrals are a common way to redirect users and search engines to a new target, if the old target no longer exists. Redirects are generally not a problem if they are not used in multiple concatenation."},
    	{HelpRobotsFollow, "The robots tag is for search engines is a guide to action, what can be done with the found page. Is there no corresponding robots tag found on the page, the page is always “index�? and “follow�?. “Follow�? means that the search engine analyze (crawl) further links that it finds on the page. An explicit “nofollow�? prevents further analyzing of links on this page. Search engines deal with limited crawl budget, depending on relevance of the whole site. A “nofollow�? can be used to prevent  crawling less relevant sub-pages or variants."},
    	{HelpRobotsIndex, "The robots tag is for search engines a guide to action, what can be done with the found page. Is there no corresponding robots tag found on the page, the page is always “index�? and “follow�?. “Index�? means that the search engine should include this content in their search index. An explicit “noindex�? is used for exclusion for the search index. A “noindex�? can also be used to increase relevance in case of duplicates."},
    	{HelpRobotsNofollow, "The robots tag is for search engines is a guide to action, what can be done with the found page. Is there no corresponding robots tag found on the page, the page is always “index�? and “follow�?. “Follow�? means that the search engine analyze (crawl) further links that it finds on the page. An explicit “nofollow�? prevents further analyzing of links on this page. Search engines deal with limited crawl budget, depending on relevance of the whole site. A “nofollow�? can be used to prevent  crawling less relevant sub-pages or variants."},
    	{HelpRobotsNoindex, "The robots tag is for search engines a guide to action, what can be done with the found page. Is there no corresponding robots tag found on the page, the page is always “index�? and “follow�?. “Index�? means that the search engine should include this content in their search index. An explicit “noindex�? is used for exclusion for the search index. A “noindex�? can also be used to increase relevance in case of duplicates."},
    	{HelpAvailability, "If a page does not respond within the specified response time during crawling, this indicator is activated. Poor availability could be problematic, because search engines want to deliver error-free and available destinations in their SERPs."},
    	{HelpDifferentURLSameAnchor, "The anchor-text of the first link to a page, which our crawler has found is identical to an anchor-text to another page. (Example: link anchor-text (target A) \"shoe\", link anchor-text (target B) \"shoe\") In this example, search engine must deecide which page is more relevant to \"shoe\" keyword. Focusing to a keyword can be problematically."},
    	{HelpMissingGoogleAnalyticsCode, "The site uses Google Analytics. It seems that Google Analytics code does not exist or is not implementert correctly on this page."},    	
    	// ProjectInfo enum
    	{NoInformationOrAdvices , "No important information or advices available"},
    	{SignificantChanges , "Significant changes for: "},
    	{ListChanges , "List changes for:: "},
    	
    	// AlarmEmail
    	{AlarmHeader , "This is an automatic notification.<br/><br/> We would like to inform you that our crawler, has detected the following:"},
    	{AlarmSubject , "Qualitywatch notification for "},
    	{NotificationFor , "Notification for "},
        {NofollowLinksSegment, "Count of nofollow-links in segment:"},
        {FollowLinksSegment, "Count of follow-links in segment:"},
        {ProjectDisabledDueErrors, "Project was stopped due to errors!"},
    	
    	//KeyworddistributionView
    	{AllKeywords , "Keywords by weight"},
    	{KeywordsByDepth , "Keywords by depth"},
    	{TotalKeywords, "Total keywords"},
    	    	
    	//URLMonitoringView
    	{Add , "Add"},
    	{URLMonitoring, "URL Monitoring"},
    	{AddURLs, "Add"},
    	{ShowURLs, "Show"},
    	{AddURLsByLine, "Add URLs line by line:"},
    	{LastCheck, "Time"},
    	{Change, "Changes"},
    	{HttpStatus, "HTTP Status"},
    	{Robots, "Robots"},
    	{URLAlreadyExists, "URL already exisits:"},
    	{CrawlingMode, "Crawling mode"},
    	{Site, "Site"},
    	{List, "List"},
    	{PleaseChooseCrawlingMode, "Please choose crawling mode!"},
    	{URLDoesNotMatchToProjectURL, "URL does not match the project URL!"},
    	{Overwrite, "Overwrite"},
    	{StatusCode, "HTTP Status Code"},
    	{Other, "Other"},
    	{Details, "Details"},
    	{Status, "Status"},
    	{Desired, "Desired"},
    	{Received, "Received"},
    	{ChangeInListCrawling, "Changes during list-crawling detected."},
    	{RobotsTxt, "robots.txt blocked"},
    	{ReallyDeleteList, "<img align=\"middle\" src=\"" + bundle.getString("application.icon.path.medium") + "1352398106_exclamation-circle-frame_red.png \">Do you really want to delete all data of this list?"},
    	{ReallyOverwriteAll , "<img align=\"middle\" src=\"" + bundle.getString("application.icon.path.medium") + "1352398106_exclamation-circle-frame_red.png \">Do you really want to overwrite all data of this list?"},
    	{DeleteList, "Delete list"}

    };
}