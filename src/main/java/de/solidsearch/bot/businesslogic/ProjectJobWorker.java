package de.solidsearch.bot.businesslogic;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.vaadin.ui.UI;

import de.qualitywatch.Qualitywatch;
import de.qualitywatch.businesslogic.AlarmAnalyzer;
import de.qualitywatch.businesslogic.PageRankAnalyzer;
import de.solidsearch.bot.dao.ProjectKeywordsManager;
import de.solidsearch.bot.dao.ProjectManager;
import de.solidsearch.bot.dao.ProjectSummaryManager;
import de.solidsearch.bot.dao.URLManager;
import de.solidsearch.bot.dao.UserManager;
import de.solidsearch.bot.data.Project;
import de.solidsearch.bot.data.ProjectSummary;
import de.solidsearch.bot.data.URL;
import de.solidsearch.bot.data.enumerations.ProjectInfo;
import de.solidsearch.bot.i18n.USMessages;
import de.solidsearch.bot.utils.AppContext;
import de.solidsearch.bot.utils.Base64;
import de.solidsearch.bot.utils.BotConfig;
import de.solidsearch.bot.utils.URLChecker;
import de.solidsearch.shared.data.KeywordStem;
import de.solidsearch.shared.utils.MD5Calc;
import de.solidsearch.shared.utils.TimestampHelper;

@Component("ProjectJobWorker")
@Scope(value = "prototype")
public class ProjectJobWorker
{
	protected static final Lock lock = new ReentrantLock();

	private boolean projectWasAlreadyFinalized = false;

	private int lowSpeedCounter = 0;

	private int leftCrawlCount = 0;

	private long lastAvgResponseTime = 0;

	private long discoverLimit = 0;

	private long lastAvgProcessingTime = 0;

	private ArrayList<Integer> processingTimes = new ArrayList<Integer>();

	private ArrayList<Integer> responseTimes = new ArrayList<Integer>();

	private int maxLinkLimitLogCounter = 0;

	protected ProjectManager projectManager;

	protected URLManager urlManager;

	private ProjectSummaryManager projectSummaryManager;

	protected Project project;

	protected ProjectSummary summary;

	private ThreadPoolTaskExecutor taskExecutor;

	protected static final Logger logger = Logger.getLogger(ProjectJobWorker.class.getName());

	protected String thisUserAgent = "";

	private DefaultHttpClient httpClient;

	protected URLChecker urlChecker = null;

	private MD5Calc hashTool = new MD5Calc("SHA-1");

	private String basicAuthHeader = "";

	private String cookieHeader = "";

	private ArrayList<KeywordStem> keywords = new ArrayList<KeywordStem>();

	private ArrayList<KeywordStem> rootKeywords = new ArrayList<KeywordStem>();

	private InetAddress[] resolvedInet;

	@Autowired
	BotConfig config;

	public void checkAndStartInactiveProject()
	{
		try
		{
			projectManager = (ProjectManager) AppContext.getApplicationContext().getBean("ProjectManager");
			boolean finalizedSuccess = false;
			
			boolean stopBecauseRobotsTxtIsNotAvailableOrBlocksOurBot = false;
			boolean projectWasStarted = true;
			List<Project> projectsToProcess;
			DomainAnalyzer da;
			long timeBefore;
			int maxParallelThreads;
			AtomicInteger runningThreads = new AtomicInteger();
			ArrayList<Integer> warnMessages;
			int errorCount = 0;
			long lastRunTimestamp = 0;

			lock.lock();
			try
			{
				projectsToProcess = projectManager.getActiveAndNotRunningExplorativProjects();

				if (projectsToProcess == null || projectsToProcess.isEmpty())
					return;

				boolean anyProjectToStart = checkIfProjectAvailableToStart(projectsToProcess, true);

				// is there a project we can start?
				if (!anyProjectToStart)
					return;

				// initial setup
				// //////////////////////////////////////////////////
				urlManager = (URLManager) AppContext.getApplicationContext().getBean("URLManager");
				urlManager.setDefaultLocale(project.getDefaultLocale());

				projectSummaryManager = (ProjectSummaryManager) AppContext.getApplicationContext().getBean("ProjectSummaryManager");
				taskExecutor = (ThreadPoolTaskExecutor) AppContext.getApplicationContext().getBean("taskExecutor");

				urlManager.setProjectId(project.getProjectId());

				initHttpClient();

				timeBefore = System.currentTimeMillis();

				thisUserAgent = project.getBotUserAgent() + "(" + project.getProjectId() + ")";
				summary = projectSummaryManager.getProjectSummaryByProjectIdAndTimestamp(project.getProjectId(), TimestampHelper.getTimestampDayMillisToZeroForToday(), false);

				leftCrawlCount = project.getCrawlLimit();

				logger.info("Started crawling for project: " + project.getProjectName() + " id: " + project.getProjectId() + " " + project.getRootDomainToCrawlWithoutProtocol());

				lastRunTimestamp = Calendar.getInstance().getTimeInMillis();

				if (summary == null)
				{
					summary = new ProjectSummary(lastRunTimestamp, project.getProjectId());
				}

				// bin with 2 threads
				maxParallelThreads = 2;
				taskExecutor.setCorePoolSize(maxParallelThreads);
				taskExecutor.setMaxPoolSize(maxParallelThreads);

				// do some pre-operations for the given domain...
				da = new DomainAnalyzer(this);

				// remove all warn msg if exists...
				// ///////////////////////////////////////
				warnMessages = project.getInfoMessageCodes();
				warnMessages.clear();
				project.setInfoMessageCodes(warnMessages);
				// set project to running status...
				project.setRunning(true);
				// BEGIN ///////////////////////////
				// robots.txt secton
				errorCount = project.getErrorCount();

				// detect prefered protocol and trailing-slash version...
				if (project.isRemoteProject())
				{
					project = da.testAndSetEntryURLBasesOnRootDomain(project);
				}
				
				// fist at all set brand-name...
				summary.setDomainBrandName(da.extractBrandFromDomain(project.getRootDomainToCrawl()));

				urlChecker = checkRobotsTxt(da, warnMessages, stopBecauseRobotsTxtIsNotAvailableOrBlocksOurBot, errorCount);

				// robots.txt secton
				// END ///////////////////////////
				// prepare URL checker upon robots.txt results...

				if (urlChecker == null) // null in case of upper filenotfoundexception...
					urlChecker = new URLChecker(project.isIgnoreImages(), project.isIgnoreCSS(), project.isIgnoreJS(), project.isIgnoreRobotsTxt(), project.getRobotsRegex());

				projectManager.saveOrUpdateProject(project);
			}
			finally
			{
				lock.unlock();
			}

			if (!stopBecauseRobotsTxtIsNotAvailableOrBlocksOurBot)
			{
				int responseCode = da.getHTTPStatusCode(project.getRootDomainToCrawl());
				if (responseCode >= 200 && responseCode < 400)
				{
					URL urlObj = null;
					SourceCodeAnalyzer sourceCodeAnalyzer = null;

					// create temp tables...
					urlManager.createTempURLTables();

					HashFunction partitionHashTool = Hashing.murmur3_32();
					// analyze entry-point of this URL
					urlObj = new URL(hashTool.generateHashCode(project.getUrlOfDomainToCrawl()), partitionHashTool.hashString(project.getUrlOfDomainToCrawl()).asInt(), project.getUrlOfDomainToCrawl(), Calendar.getInstance().getTimeInMillis());
					urlObj.setDepthFromDomainRoot(0);

					summary.setInternalURLs(1);
					sourceCodeAnalyzer = new SourceCodeAnalyzer(this, urlObj, runningThreads);
					urlObj = sourceCodeAnalyzer.analyzeSourceCode();
					summary = projectSummaryManager.updateProjectSummaryWithoutPersist(summary, urlObj);

					urlManager.saveURL(urlObj);
					urlManager.emptyInsertCache(false);
					urlManager.emptyUpdateCache();

					if (urlManager.getInternalAndNotAlreadyCrawledURLs(10, project.isIgnoreInternalNofollow()).size() < 1)
					{
						warnMessages.add(ProjectInfo.NOTENOUGHTURLS.getInfoMessageCode());
						warnMessages.remove((Integer)ProjectInfo.CRAWLINGISRUNNING.getInfoMessageCode());
						stopDeactivateAndSaveProject(warnMessages);

						urlManager.dropTempURLTables();

						return;
					}

					lock.lock();
					try
					{
						Project reReaded = projectManager.getProjectByID(project.getProjectId());
						// someone external decided to stop project...
						if (!reReaded.isActive())
						{
							project.setActive(false);
							leftCrawlCount = 0;
						}

						// reset error count
						project.setErrorCount(0);
						// set last-run timestamp at the beginning of this run...
						project.setLastRunTimestamp(lastRunTimestamp);
						projectManager.saveOrUpdateProject(project);
					}
					finally
					{
						lock.unlock();
					}

					leftCrawlCount--;

					boolean emptyFetch = false;
					int lowFetchingCounter = 0;
					// take 522 to pretend a real end...
					final int LOWFETCHTRIALS = 522;

					do
					{
						List<URL> urlList = urlManager.getInternalAndNotAlreadyCrawledURLs((project.isRapidCrawling() == true ? 300 : 150), project.isIgnoreInternalNofollow());
						// update project summary to see early values...
						if (!urlList.isEmpty())
						{
							// check if we have found only some new URLs,
							// else
							// reset...
							if (urlList.size() <= 2)
							{
								lowFetchingCounter++;
							}
							else
							{
								lowFetchingCounter = 0;
							}

							for (int i = 0; i < urlList.size(); i++)
							{
								URL u = urlList.get(i);

								if (leftCrawlCount < 1)
									break;

								try
								{
									// execute none blocking task..
									taskExecutor.execute(new SourceCodeAnalyzer(this, u, runningThreads));
									runningThreads.incrementAndGet();
								}
								catch (TaskRejectedException e)
								{
									// catch exception due to threadqueue
									// could
									// be
									// full (this should not happend!)
									logger.info("Thread queue was full! Project: " + project.getProjectId() + " this should not happend! " + e.getMessage());
									i--;
									leftCrawlCount++;
								}
								leftCrawlCount--;

								// check if max parallel threads are reached
								// and
								// wait until some thread gets finished...
								while (runningThreads.get() >= maxParallelThreads)
								{
									try
									{
										Thread.sleep(1);
									}
									catch (InterruptedException e1)
									{
										e1.printStackTrace();
									}
								}
							}
						}
						else
						{
							emptyFetch = true;
						}
						// re-read project
						Project reReaded = projectManager.getProjectByID(project.getProjectId());
						// someone external decided to stop project...
						if (!reReaded.isActive())
						{
							project.setActive(false);
							leftCrawlCount = 0;
						}
						// wait until all threads have finished...
						while (runningThreads.get() > 0)
						{
							try
							{
								Thread.sleep(1);
							}
							catch (InterruptedException e1)
							{
								e1.printStackTrace();
							}
						}

						urlManager.emptyInsertCache(false);

						maxParallelThreads = recalculateCrawlingSpeed(project.isRapidCrawling(), maxParallelThreads);
						if (lowSpeedCounter > 3)
						{
							warnMessages.add(ProjectInfo.SITEISVERYSLOW.getInfoMessageCode());
							project.setInfoMessageCodes(warnMessages);
							project.setActive(false);
							leftCrawlCount = 0;
							logger.error(project.getProjectName() + " id: " + project.getProjectId() + " was stopped. It seems to be that site is using crawling protection like mod_security. lastAvgResponseTime: " + lastAvgResponseTime
									+ "ms / lastThreads:" + maxParallelThreads);
						}

					}
					while (leftCrawlCount > 1 && !emptyFetch && lowFetchingCounter < LOWFETCHTRIALS);

					urlManager.emptyUpdateCache();

					// we cannot get enougth new URLs, so we decide to be
					// finish...
					if (lowFetchingCounter >= LOWFETCHTRIALS)
					{
						logger.info("Project budget reset for for: " + project.getProjectName() + " id: " + project.getProjectId() + " , URL: " + project.getRootDomainToCrawl() + " not enougth new URLs found!");
						leftCrawlCount = 0;
					}
					if (emptyFetch)
					{
						logger.info("No more URLs to crawl for: " + project.getProjectName() + " id: " + project.getProjectId() + " now finalize project!");
						leftCrawlCount = 0;
					}
				}
				else if (responseCode >= 400)
				{
					// put warn msg ...
					projectWasStarted = false;
					warnMessages.add(ProjectInfo.INVALIDPROJECTURL.getInfoMessageCode());
					project.setInfoMessageCodes(warnMessages);
					project.setActive(false);
					project.setDisabledByError(true);
					logger.warn("Domain entry shows strange response code : " + responseCode + " ProjectId: " + project.getProjectId() + " project: " + project.getProjectName() + " , URL: " + project.getRootDomainToCrawl());
				}
				else if (responseCode == 0)
				{
					// timeout: do nothing and try again later...
					projectWasStarted = false;

					errorCount++;
					project.setErrorCount(errorCount);
				}
				lock.lock();
				try
				{
					Project reReaded = projectManager.getProjectByID(project.getProjectId());
					// someone external decided to stop project...
					if (!reReaded.isActive())
					{
						project.setActive(false);
						leftCrawlCount = 0;
					}
					projectManager.saveOrUpdateProject(project);

					if (leftCrawlCount <= 0)
					{
						// calculate aggregations..
						finalizedSuccess = finalizeProject(warnMessages);
					}
					long timeAfter = System.currentTimeMillis();
					summary.setCrawlingDurationMills(summary.getCrawlingDurationMills() + (timeAfter - timeBefore));
				}
				finally
				{
					lock.unlock();
				}
			}

			lock.lock();
			try
			{
				// re-read project
				Project reReaded = projectManager.getProjectByID(project.getProjectId());
				// someone external decided to stop project...
				if (!reReaded.isActive())
				{
					// check if project was already finalized() and project was already started, if not finalize() it
					if (projectWasAlreadyFinalized == false && projectWasStarted == true)
					{
						finalizedSuccess = finalizeProject(warnMessages);
					}
					project.setActive(false);
				}

				// disable remote project if there were not errors...
				if (project.isRemoteProject() && project.getErrorCount() == 0 && projectWasStarted == true)
				{
					project.setActive(false);
				}

				// if there was'nt found any URLs to analyze, set project to
				// inactive/notRunning..
				if (finalizedSuccess)
				{
					projectSummaryManager.saveOrUpdateProjectSummary(summary);
				}
					
				warnMessages.remove((Integer)ProjectInfo.CRAWLINGISRUNNING.getInfoMessageCode());
				
				if (!finalizedSuccess && projectWasStarted == true)
					warnMessages.add(ProjectInfo.PROJECTERROR.getInfoMessageCode());
					
				project.setInfoMessageCodes(warnMessages);
				project.setRunning(false);
				projectManager.saveOrUpdateProject(project);
			}
			finally
			{
				lock.unlock();
			}
			logger.info("Finished crawling for project: " + project.getProjectName() + " id: " + project.getProjectId());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			lock.lock();
			try
			{
				project.setRunning(false);
				project.setErrorCount(project.getErrorCount() + 1);
				projectManager.saveOrUpdateProject(project);
				urlManager.dropTempURLTables();
			}
			finally
			{
				lock.unlock();
			}
		}
		finally
		{
			// clean up resources
			if (taskExecutor != null)
			{
				taskExecutor.shutdown();
			}
			releaseHttpClientResources();
		}
		
		lock.lock();
		try
		{
			// more than 3 times, deactivate project...
			if (project.getErrorCount() > 3)
			{
				project.setActive(false);
				project.setDisabledByError(true);
				projectManager.saveOrUpdateProject(project);
				
				logger.warn("Project disabled due to errors! ProjectId: " + project.getProjectId() + " " + project.getRootDomainToCrawlWithoutProtocol());
	
				if (project.isNotificationEnabled() && !project.isRemoteProject())
				{
					ResourceBundle i18n = ResourceBundle.getBundle(USMessages.class.getName(), project.getDefaultLocale());
					AlarmAnalyzer alarmAnalyzer = (AlarmAnalyzer) AppContext.getApplicationContext().getBean("AlarmAnalyzer");
					alarmAnalyzer.sendAlarmWithSingleMessage(project, i18n.getString(USMessages.ProjectDisabledDueErrors));
				}
			}
		}
		finally
		{
			lock.unlock();
		}
	}

	private void stopDeactivateAndSaveProject(ArrayList<Integer> warnMessages)
	{
		lock.lock();
		try
		{
			project.setInfoMessageCodes(warnMessages);
			project.setDisabledByError(true);
			project.setRunning(false);
			project.setActive(false);
			projectManager.saveOrUpdateProject(project);
		}
		finally
		{
			lock.unlock();
		}
	}

	private boolean finalizeProject(ArrayList<Integer> warnMessages)
	{
		boolean success = true;
		
		try
		{
			PostAnalyzer postAnalyzer = (PostAnalyzer) AppContext.getApplicationContext().getBean("PostAnalyzer");

			System.out.println("Preparing result table for: " + project.getCrawlerDatabaseId() + " project: " + project.getProjectName());
			System.out.println("---------------------------");

			urlManager.emptyGraphCache();

			long before = System.currentTimeMillis();

			// cleanup old data...
			urlManager.truncateURLsbyProjectId(project.getCrawlerDatabaseId());
			urlManager.dropURLTableIndicies(project.getCrawlerDatabaseId(), true);

			// copy new data from temp to final tables...
			urlManager.copyFromTempToUserTable(project.getCrawlerDatabaseId());

			System.out.println("Duration of copyFromTempToUserTable() : " + (System.currentTimeMillis() - before));
			before = System.currentTimeMillis();

			urlManager.createURLTableIndicies(project.getCrawlerDatabaseId());
			System.out.println("Duration of createURLTableIndicies() : " + (System.currentTimeMillis() - before));

			summary.setTrailingSlashIssues(postAnalyzer.findTrailingSlashIssues(project.getCrawlerDatabaseId()).getTrailingSlashIssues());

			summary.setHostnameStatisticList(postAnalyzer.getLinkHostnameStatistic(project.getProjectId(),summary));
			summary.setUrlSegmentStatisticList(postAnalyzer.getURLSegmentStatistics(project.getProjectId(),summary));

			if (BotConfig.isPluginAvailable() && !project.isRemoteProject())
			{
				PageRankAnalyzer pageRankAnalyzer = (PageRankAnalyzer) AppContext.getApplicationContext().getBean("PageRankAnalyzer");
				pageRankAnalyzer.generatePageRankAndCleanup(project.getProjectId(), project.getCrawlerDatabaseId(), summary);
			}
			
			postAnalyzer.computeExternalLinkPower(project.getCrawlerDatabaseId());

			before = System.currentTimeMillis();
			urlManager.dropTempURLTables();
			System.out.println("Duration of dropTempURLTables() : " + (System.currentTimeMillis() - before));

			ProjectSummary summaryAggregation = urlManager.getAggregationsForProjectSummary(project.getCrawlerDatabaseId());

			if (summaryAggregation != null)
			{
				summary.setAvgResponseTimeMills(summaryAggregation.getAvgResponseTimeMills());
				summary.setAvgPageSize(summaryAggregation.getAvgPageSize());

				ProjectSummary summaryDuplicates = postAnalyzer.analyzeDuplicateContentURLsAndGetProjectSummary(project.getCrawlerDatabaseId());
				summary.setDuplicateContentURLs(summaryDuplicates.getDuplicateContentURLs());

				ProjectSummary summaryDuplicateTitle = postAnalyzer.analyzeDuplicateTitleURLsAndGetProjectSummary(project.getCrawlerDatabaseId());
				summary.setDuplicateTitleURLs(summaryDuplicateTitle.getDuplicateTitleURLs());

				ProjectSummary analyzeKeywordOrientationShortTerm = postAnalyzer.analyzeKeywordOrientationShortTerm(project.getCrawlerDatabaseId());
				summary.setKeywordOrientationShortTermURLs(analyzeKeywordOrientationShortTerm.getKeywordOrientationShortTermURLs());

				ProjectSummary analyzeKeywordOrientationTwoTerms = postAnalyzer.analyzeKeywordOrientationTwoTerms(project.getCrawlerDatabaseId());
				summary.setKeywordOrientationTwoTermsURLs(analyzeKeywordOrientationTwoTerms.getKeywordOrientationTwoTermsURLs());

				ProjectSummary summaryDuplicateMeta = postAnalyzer.analyzeDuplicateMetaDescriptionURLsAndGetProjectSummary(project.getCrawlerDatabaseId());
				summary.setDuplicateMetaDescriptionURLs(summaryDuplicateMeta.getDuplicateMetaDescriptionURLs());

				ProjectSummary summaryDuplicateH1 = postAnalyzer.analyzeDuplicateH1URLsAndGetProjectSummary(project.getCrawlerDatabaseId());
				summary.setDuplicateH1URLs(summaryDuplicateH1.getDuplicateH1URLs());

				ProjectSummary canonicalIssues = postAnalyzer.analyzeCanonicalTagIssuesProjectSummary(project.getCrawlerDatabaseId());
				summary.setCanonicalTagIssues(canonicalIssues.getCanonicalTagIssues());

				ProjectSummary externalDomains = postAnalyzer.countDifferentDomainsOfExternalLinks(project.getCrawlerDatabaseId());
				summary.setExternalURLsDifferentDomains(externalDomains.getExternalURLsDifferentDomains());

				ProjectSummary differentURLSameAnchor = postAnalyzer.analyzeDuplicateAnchorText(project.getCrawlerDatabaseId());
				summary.setDifferentURLSameAnchor(differentURLSameAnchor.getDifferentURLSameAnchor());

				ProjectSummary readingLevel = postAnalyzer.analyzeAvgReadingLevel(project.getCrawlerDatabaseId());
				summary.setReadinglevel(readingLevel.getReadinglevel());

				if (BotConfig.isPluginAvailable() && !project.isRemoteProject())
				{
					AlarmAnalyzer alarmAnalyzer = (AlarmAnalyzer) AppContext.getApplicationContext().getBean("AlarmAnalyzer");
					summary = alarmAnalyzer.generateAlarms(summary, project.isNotificationEnabled());
				}
				
				ProjectSummary qualityScores = postAnalyzer.computeQualityScores(project.getCrawlerDatabaseId());
				summary.setQualityScore(qualityScores.getQualityScore());
				summary.setAvgInternalFollowLinks(qualityScores.getAvgInternalFollowLinks());
				summary.setMaxInternalLinksThreshold(qualityScores.getMaxInternalLinksThreshold());
				summary.setExternalLinksThreshold(qualityScores.getExternalLinksThreshold());
				summary.setTotalCountOfRelevantKeywords(keywords.size());

				ProjectKeywordsManager projectKeywordsManager = (ProjectKeywordsManager) AppContext.getApplicationContext().getBean("ProjectKeywordsManager");

				projectKeywordsManager.createKeywordTableIfNotExist(project.getProjectId());
				projectKeywordsManager.truncateKeywords(project.getProjectId());
				projectKeywordsManager.saveKeywords(keywords, project.getProjectId());

				postAnalyzer.computeVarietyTopicScore(project.getProjectId(), project.getCrawlerDatabaseId());

				ProjectSummary totalCountOfKeywords = postAnalyzer.computeTotalKeywords(project.getCrawlerDatabaseId());
				summary.setTotalCountOfKeywords(totalCountOfKeywords.getTotalCountOfKeywords());

				Map.Entry<String, Calendar> previousCrawling = projectManager.getNewestCrawlingEntry(project);
				if (previousCrawling != null)
				{
					if (project.getCrawlerDatabaseId() == previousCrawling.getKey())
					{
						// for debugging ... maybe here could be a problem
						logger.warn("DatabaseIDs should not be the same for project : " + project.getProjectName() + " " + project.getCrawlerDatabaseId());
					}
					else
					{
						postAnalyzer.generateComparisonView(project.getProjectId(), project.getCrawlerDatabaseId(), previousCrawling.getKey());
						postAnalyzer.generateLowContentView(project.getProjectId(), project.getCrawlerDatabaseId(), previousCrawling.getKey());
					}
				}
			}
			before = System.currentTimeMillis();
			// drop unnecessary indices to free HD space
			urlManager.dropURLTableIndicies(project.getCrawlerDatabaseId(), false);
			System.out.println("Duration of dropURLTableIndicies() : " + (System.currentTimeMillis() - before));
			System.out.println("Total links found : " + summary.getTotalLinks());
			System.out.println("Total internal urls : " + summary.getInternalURLs());
			System.out.println("Total crawled urls : " + summary.getCrawledURLs());
			System.out.println("-----------------------------------");

			// now we have finished crawling...
			// switch data basis...
			project = projectManager.rotateDatabaseIdsForURLTableAndSwitch(project);
			project.setUIRefreshNeccessary(true);

			summary.setFinished(true);

			projectWasAlreadyFinalized = true;
		}
		catch (Exception e)
		{
			success = false;
			e.printStackTrace();
			urlManager.dropTempURLTables();
		}
		return success;
	}

	private int recalculateCrawlingSpeed(boolean rapidCrawling, int lastThreadSetting)
	{
		boolean rapid = rapidCrawling;
		long sumProcessingTimes = 0;
		long sumResponseTimes = 0;
		final int MINTHREADS = 1;
		final int MAXTHREADS = 10;

		for (int i = 0; i < processingTimes.size(); i++)
		{
			sumProcessingTimes = sumProcessingTimes + processingTimes.get(i);
		}
		for (int i = 0; i < responseTimes.size(); i++)
		{
			sumResponseTimes = sumResponseTimes + responseTimes.get(i);
		}

		long avgProcessingTime = 0;
		long avgResponseTime = 0;

		if (processingTimes.size() != 0)
			avgProcessingTime = sumProcessingTimes / processingTimes.size();
		if (responseTimes.size() != 0)
			avgResponseTime = sumResponseTimes / responseTimes.size();

		// the last set of url run all in timeouts / errors / 300...
		if (avgResponseTime == 0)
		{
			// do nothing and reduce threads to min to avoid DDOS pattern...
			processingTimes.clear();
			responseTimes.clear();

			logger.warn("ResponseTime sum where zero! Count of times: " + responseTimes.size() + " avgProcessingTime: " + avgProcessingTime + " set threads to 2");
			return 2;
		}

		int reducedThreads = 0;
		if (lastAvgResponseTime != 0 && (lastAvgResponseTime < avgResponseTime))
		{
			// 17%
			long lastAvgResponseTimePlusTFifteenPercent = lastAvgResponseTime + ((lastAvgResponseTime / 100) * 17);
			// we have to reduce speed
			if (avgResponseTime > lastAvgResponseTimePlusTFifteenPercent)
			{
				if (rapid == false)
				{
					reducedThreads = 1;
				}
				else
				{
					rapid = false;
				}
				logger.debug(project.getProjectName() + " id: " + project.getProjectId() + " reduced threads because it seems to be site getting slower. avgResponseTime: " + avgResponseTime + " lastAvgResponseTime: " + lastAvgResponseTime);
			}
		}

		int maxParallelThreads = 0;

		if (avgProcessingTime < 200)
		{
			// 4 per sec
			maxParallelThreads = MINTHREADS;
			// 5-6 per sec
			if (rapid)
				maxParallelThreads = 2;
		}
		else if (avgProcessingTime < 350)
		{
			// 6 per sec
			maxParallelThreads = 2;
			// 5-6 per sec
			if (rapid)
				maxParallelThreads = 3;
		}
		else if (avgProcessingTime < 450)
		{
			// 6,6 per sec
			maxParallelThreads = 3;
			// 5-6 per sec
			if (rapid)
				maxParallelThreads = 5;
		}
		else if (avgProcessingTime < 600)
		{
			// 6,6
			maxParallelThreads = 4;
			if (rapid)
				maxParallelThreads = 6;
		}
		else if (avgProcessingTime < 800)
		{
			maxParallelThreads = 5;
			if (rapid)
				maxParallelThreads = 7;
		}
		else if (avgProcessingTime < 1100)
		{
			maxParallelThreads = 6;
			if (rapid)
				maxParallelThreads = 8;
		}
		else if (avgProcessingTime < 1400)
		{
			maxParallelThreads = 7;
			if (rapid)
				maxParallelThreads = 9;
		}
		else if (avgProcessingTime < 1700)
		{
			maxParallelThreads = 8;
			if (rapid)
				maxParallelThreads = MAXTHREADS;
		}
		else
		{
			maxParallelThreads = 9;
			if (rapid)
				maxParallelThreads = MAXTHREADS;
		}

		if (reducedThreads != 0)
		{
			if (lastThreadSetting > MINTHREADS)
			{
				maxParallelThreads = lastThreadSetting - reducedThreads;
			}
		}

		// avoid big jumps do to internal problems...
		if (rapid)
		{
			if ((maxParallelThreads >= lastThreadSetting + 4) && lastAvgResponseTime != 0)
			{
				maxParallelThreads = lastThreadSetting + 1;
				logger.debug(project.getProjectName() + " id: " + project.getProjectId() + " made a big thread jump. Increase only one thread. " + avgResponseTime + " lastAvgResponseTime: " + lastAvgResponseTime + " threads_before:"
						+ lastThreadSetting + " threads_now:" + maxParallelThreads);
			}
		}
		else
		{
			if ((maxParallelThreads >= lastThreadSetting + 3) && lastAvgResponseTime != 0)
			{
				maxParallelThreads = lastThreadSetting + 1;
				logger.debug(project.getProjectName() + " id: " + project.getProjectId() + " made a big thread jump. Increase only one thread. " + avgResponseTime + " lastAvgResponseTime: " + lastAvgResponseTime + " threads_before:"
						+ lastThreadSetting + " threads_now:" + maxParallelThreads);
			}
		}

		// be sure to run minimum with two thread...
		if (maxParallelThreads < MINTHREADS)
		{
			maxParallelThreads = MINTHREADS;
		}

		// count if we run multiple times with lowest speed... if so, site has got a crawling protection / security
		if (avgProcessingTime > 200 && maxParallelThreads == MINTHREADS)
		{
			lowSpeedCounter++;
		}
		else
		{
			// reset counter
			if (lowSpeedCounter > 0)
			{
				lowSpeedCounter--;
			}
		}

		taskExecutor.setCorePoolSize(maxParallelThreads);
		taskExecutor.setMaxPoolSize(maxParallelThreads);

		logger.debug("Crawlingspeed for project: " + project.getProjectName() + " id: " + project.getProjectId() + " set to " + maxParallelThreads + " threads. avgResponseTime: " + avgResponseTime + " avgProcessingTime: " + avgProcessingTime
				+ " lastAvgResponseTime: " + lastAvgResponseTime + " lastAvgProcessingTime: " + lastAvgProcessingTime);

		lastAvgResponseTime = avgResponseTime;
		lastAvgProcessingTime = avgProcessingTime;

		processingTimes.clear();
		responseTimes.clear();

		return maxParallelThreads;
	}

	protected boolean checkIfProjectAvailableToStart(List<Project> projectsToProcess, boolean isExplorativProject)
	{
		boolean anyProjectToStart = false;

		for (int i = 0; i < projectsToProcess.size(); i++)
		{
			project = projectsToProcess.get(i);
			long lastRunTimestamp = 0;

			if (isExplorativProject)
				lastRunTimestamp = project.getLastRunTimestamp();
			else
				lastRunTimestamp = project.getListJobLastRunTimestamp();

			if (TimestampHelper.resetDayMillisToZero(lastRunTimestamp) < TimestampHelper.getTimestampDayMillisToZeroForToday())
			{
				Calendar now = Calendar.getInstance();
				switch (now.get(Calendar.DAY_OF_WEEK))
				{
				case Calendar.MONDAY:
					if (project.isRunMonday())
						anyProjectToStart = true;
					break;
				case Calendar.TUESDAY:
					if (project.isRunTuesday())
						anyProjectToStart = true;
					break;
				case Calendar.WEDNESDAY:
					if (project.isRunWednesday())
						anyProjectToStart = true;
					break;
				case Calendar.THURSDAY:
					if (project.isRunThursday())
						anyProjectToStart = true;
					break;
				case Calendar.FRIDAY:
					if (project.isRunFriday())
						anyProjectToStart = true;
					break;
				case Calendar.SATURDAY:
					if (project.isRunSaturday())
						anyProjectToStart = true;
					break;
				case Calendar.SUNDAY:
					if (project.isRunSunday())
						anyProjectToStart = true;
					break;
				}
				if (anyProjectToStart == true)
					break;
			}

			if (project.isRemoteProject())
			{
				anyProjectToStart = true;
				break;
			}
		}
		return anyProjectToStart;
	}

	protected void releaseHttpClientResources()
	{
		if (httpClient != null)
		{
			httpClient.getConnectionManager().shutdown();
		}
	}

	public void initHttpClient() throws Exception
	{
		DnsResolver dnsResolver = null;
		// disabled if proxy is used...
		if (config.PROXYHOST != null && config.PROXYHOST.length() <= 0)
		{
			try
			{
				// get ip of host
				SystemDefaultDnsResolver r = new SystemDefaultDnsResolver();
				resolvedInet = r.resolve(project.getRootDomainToCrawlWithoutProtocol());
			}
			catch (Exception e)
			{
				// stop project with warning
				ArrayList<Integer> msg = new ArrayList<Integer>();
				msg.add(ProjectInfo.COULDNOTRESOLVEHOSTNAME.getInfoMessageCode());
				project.setInfoMessageCodes(msg);
				project.setErrorCount(1);
				project.setActive(false);
				project.setDisabledByError(true);
				projectManager.saveOrUpdateProject(project);
				return;
			}
			// create custom dnsResolver to cache DNS ... avoid DNS DDOS...
			dnsResolver = new SystemDefaultDnsResolver()
			{

				@Override
				public InetAddress[] resolve(final String host) throws UnknownHostException
				{
					if (host.equalsIgnoreCase(project.getRootDomainToCrawlWithoutProtocol()))
					{
						return resolvedInet;
					}
					else
					{
						return super.resolve(host);
					}
				}
			};

		}
		
		SSLContext sslContext = SSLContext.getInstance("SSL");

		// set up a TrustManager that trusts everything
		// this means trust also invalid certificates

		// @TODO make a check without trusting everything to test exceptions like this way: javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated
		sslContext.init(null, new TrustManager[]
		{ new X509TrustManager()
		{
			public X509Certificate[] getAcceptedIssuers()
			{
				return null;
			}

			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
			{
			}

			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
			{
			}
		} }, new SecureRandom());

		SSLSocketFactory sf = new SSLSocketFactory(sslContext);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, sf));

		PoolingClientConnectionManager cm;

		if (config.PROXYHOST != null && config.PROXYHOST.length() <= 0)
		{
			cm = new PoolingClientConnectionManager(schemeRegistry, dnsResolver);
		}
		else
		{
			cm = new PoolingClientConnectionManager(schemeRegistry);
		}

		cm.setMaxTotal(30);
		cm.setDefaultMaxPerRoute(15);
		this.httpClient = new DefaultHttpClient(cm);

		if (config.PROXYHOST != null && config.PROXYHOST.length() > 0)
		{
			HttpHost proxy = new HttpHost(config.PROXYHOST, Integer.parseInt(config.PROXYPORT));
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

			if (config.PROXYNTLMDOMAIN != null && config.PROXYNTLMDOMAIN.length() > 0)
			{
				NTCredentials creds = new NTCredentials(config.PROXYUSER, config.PROXYPASSWORD, config.PROXYNTLMWWORSTATION, config.PROXYNTLMDOMAIN);
				httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY, creds);
			}
			else if (config.PROXYUSER != null && config.PROXYUSER.length() > 0)
			{
				httpClient.getCredentialsProvider().setCredentials(new AuthScope(config.PROXYHOST, Integer.parseInt(config.PROXYPORT)), new UsernamePasswordCredentials(config.PROXYUSER, config.PROXYPASSWORD));
			}

		}

		// set this parameter according to this bug: http://code.google.com/p/crawler4j/issues/detail?id=136 (later i set it to ignore to avoid stickyness for jsessid cookies)
		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);

		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");

		// ///////////////////////////
		// init some values
		UserManager userManager = (UserManager) AppContext.getApplicationContext().getBean("UserManager");
		setDiscoverLimit(project.getDiscoverLimit());
		setCookieHeader(getProject().getCookieName() + "=" + getProject().getCookieValue());
		setBasicAuthHeader("Basic " + Base64.encodeToString((getProject().getBasicAuthUser() + ":" + getProject().getBasicAuthPassword()).getBytes(), false));

	}

	public URLChecker checkRobotsTxt(DomainAnalyzer da, ArrayList<Integer> warnMessages, boolean stopBecauseRobotsTxtIsNotAvailableOrBlocksOurBot, int errorCount)
	{
		URLChecker checker = null;
		try
		{
			String robotsString = da.readRobotsTxtAndGetRegex(project.getRootDomainToCrawl());

			checker = new URLChecker(project.isIgnoreImages(), project.isIgnoreCSS(), project.isIgnoreJS(), project.isIgnoreRobotsTxt(), robotsString);

			if (!checker.isURLAllowed(project.getRootDomainToCrawl()))
			{
				// we are blocked by robots.txt
				if (!project.isIgnoreRobotsTxt())
				{
					// stop project with warning
					warnMessages.add(ProjectInfo.BOTISBLOCKED.getInfoMessageCode());
					project.setInfoMessageCodes(warnMessages);
					stopBecauseRobotsTxtIsNotAvailableOrBlocksOurBot = true;
					errorCount++;
					project.setErrorCount(errorCount);
				}
				else
				{
					// set some status-infos and persist this job if regex-data access was successful
					warnMessages.add(ProjectInfo.CRAWLINGISRUNNING.getInfoMessageCode());
				}
			}
			else
			{
				project.setRobotsRegex(robotsString);
				// set some status-infos and persist this job if regex-data access was successful
				warnMessages.add(ProjectInfo.CRAWLINGISRUNNING.getInfoMessageCode());
			}
		}
		catch (FileNotFoundException e)
		{
			logger.warn("No robots.txt found for projectid: " + project.getProjectId() + " " + project.getRootDomainToCrawlWithoutProtocol());
		}

		return checker;
	}

	public ProjectSummary getProjectSummary()
	{
		return summary;
	}

	public void setProjectSummary(ProjectSummary summary)
	{
		this.summary = summary;
	}

	public ProjectManager getProjectManager()
	{
		return projectManager;
	}

	public URLManager getUrlManager()
	{
		return urlManager;
	}

	public ProjectSummaryManager getProjectSummaryManager()
	{
		return projectSummaryManager;
	}

	public Project getProject()
	{
		return project;
	}

	public String getThisUserAgent()
	{
		return thisUserAgent;
	}

	public HttpClient getHttpClient()
	{
		return httpClient;
	}

	public URLChecker getUrlChecker()
	{
		return urlChecker;
	}

	public synchronized MD5Calc getHashTool()
	{
		return hashTool;
	}

	public synchronized void addProcessingTimeToList(int processingTime)
	{
		processingTimes.add(processingTime);
	}

	public synchronized void addResponseTimeToList(int responseTime)
	{
		responseTimes.add(responseTime);
	}

	public long getLastAvgProcessingTime()
	{
		return lastAvgProcessingTime;
	}

	public String getBasicAuthHeader()
	{
		return basicAuthHeader;
	}

	public void setBasicAuthHeader(String basicAuthHeader)
	{
		this.basicAuthHeader = basicAuthHeader;
	}

	public String getCookieHeader()
	{
		return cookieHeader;
	}

	public void setCookieHeader(String cookieHeader)
	{
		this.cookieHeader = cookieHeader;
	}

	public long getDiscoverLimit()
	{
		return discoverLimit;
	}

	public void setDiscoverLimit(long discoverLimit)
	{
		this.discoverLimit = discoverLimit;
	}

	public ArrayList<KeywordStem> getKeywords()
	{
		return keywords;
	}

	public void setKeywords(ArrayList<KeywordStem> keywords)
	{
		this.keywords = keywords;
	}

	public ArrayList<KeywordStem> getRootKeywords()
	{
		return rootKeywords;
	}

	public void setRootKeywords(ArrayList<KeywordStem> rootKeywords)
	{
		this.rootKeywords = rootKeywords;
	}

	public synchronized int getMaxLinkLimitLogCounter()
	{
		return maxLinkLimitLogCounter;
	}

	public synchronized void setMaxLinkLimitLogCounter(int maxLinkLimitLogCounter)
	{
		this.maxLinkLimitLogCounter = maxLinkLimitLogCounter;
	}

	public void setProject(Project project)
	{
		this.project = project;
	}

}
