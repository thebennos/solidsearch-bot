package de.solidsearch.bot.businesslogic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import de.qualitywatch.businesslogic.AlarmAnalyzer;
import de.solidsearch.bot.dao.ProjectManager;
import de.solidsearch.bot.dao.URLListManager;
import de.solidsearch.bot.data.Project;
import de.solidsearch.bot.data.ProjectSummary;
import de.solidsearch.bot.data.URL;
import de.solidsearch.bot.data.enumerations.ProjectInfo;
import de.solidsearch.bot.utils.AppContext;
import de.solidsearch.bot.utils.URLChecker;

@Component("ListJobWorker")
@Scope(value = "prototype")
public class ListJobWorker extends ProjectJobWorker
{
	private ThreadPoolTaskExecutor taskExecutor;
	
	@Override
	public void checkAndStartInactiveProject()
	{
		List<Project> projectsToProcess;
		List<URL> urlList;
		long lastRunTimeStamp = 0;
		ArrayList<Integer> warnMessages = null;
		
		projectManager = (ProjectManager) AppContext.getApplicationContext().getBean("ProjectManager");

		lock.lock();
		try
		{
			projectsToProcess = projectManager.getActiveAndNotRunningURLListProjects();

			if (projectsToProcess == null || projectsToProcess.isEmpty())
				return;
			
			boolean anyProjectToStart = checkIfProjectAvailableToStart(projectsToProcess, false);

			// is there a project we can start?
			if (!anyProjectToStart)
				return;
			
			urlManager = (URLListManager) AppContext.getApplicationContext().getBean("URLListManager");
			urlManager.setProjectId(project.getProjectId());
			urlManager.setDefaultLocale(project.getDefaultLocale());
			((URLListManager)urlManager).createListURLTableForGivenProject();
			
			urlList = ((URLListManager)urlManager).getAllURLs(null,true);
			
			// check if nothing to do
			if (urlList == null)
				return;
			
			((URLListManager)urlManager).deleteRecords(true);
			
			lastRunTimeStamp = Calendar.getInstance().getTimeInMillis();
			
			project.setRunning(true);
			projectManager.saveOrUpdateProject(project);
		}		
		finally
		{
			lock.unlock();
		}
		
		try
		{
			logger.info("Started list-crawling for project: " + project.getProjectName() + " id: " + project.getProjectId() + " " + project.getRootDomainToCrawlWithoutProtocol());
			
			AtomicInteger runningThreads = new AtomicInteger();
			int maxParallelThreads = 2;
			initHttpClient();

			thisUserAgent = project.getBotUserAgent() + "(" + project.getProjectId() + ")";
						
			taskExecutor = (ThreadPoolTaskExecutor) AppContext.getApplicationContext().getBean("taskExecutor");
			
			DomainAnalyzer da = new DomainAnalyzer(this);
			
			summary = new ProjectSummary(lastRunTimeStamp, project.getProjectId());
			summary.setDomainBrandName(da.extractBrandFromDomain(project.getRootDomainToCrawl()));
			
			warnMessages = project.getInfoMessageCodes();
			
			boolean stopBecauseRobotsTxtIsNotAvailableOrBlocksOurBot = false;
			
			urlChecker = checkRobotsTxt(da,warnMessages,stopBecauseRobotsTxtIsNotAvailableOrBlocksOurBot,project.getErrorCount());
			
			if (stopBecauseRobotsTxtIsNotAvailableOrBlocksOurBot == true)
				return;
			
			if (urlChecker == null) // null in case of upper filenotfoundexception...
				urlChecker = new URLChecker(project.isIgnoreImages(), project.isIgnoreCSS(), project.isIgnoreJS(), project.isIgnoreRobotsTxt(), project.getRobotsRegex());

			taskExecutor.setCorePoolSize(maxParallelThreads);
			taskExecutor.setMaxPoolSize(maxParallelThreads);
		
			
			for (int i = 0; i < urlList.size(); i++)
			{
				try
				{
					// execute none blocking task..
					
					if (!urlChecker.isURLAllowed(urlList.get(i).getURLName()))
					{
						// check if url is blocked by robots.txt
						URL newURL = urlList.get(i);
						URL oldURL = null; 
						try
						{
							oldURL = urlList.get(i).clone();
						}
						catch (CloneNotSupportedException e)
						{
							e.printStackTrace();
						}

						newURL.setBlockedByRobotsTxt(true);
						urlManager.updateURL(newURL, oldURL);
						continue;
					}
					
					taskExecutor.execute(new SourceCodeAnalyzer(this, urlList.get(i), runningThreads));
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
				}
				
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
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			List<URL> changedUrls =((URLListManager)urlManager).getChangedURLs();
			boolean anyChanges = false;
			
			if (changedUrls != null)
				anyChanges = true;
			
			warnMessages.remove((Integer)ProjectInfo.CRAWLINGISRUNNING.getInfoMessageCode());
			project.setInfoMessageCodes(warnMessages);
			
			project.setRunning(false);
			project.setListJobLastRunTimestamp(lastRunTimeStamp);
			project.setChangesInListCrawling(anyChanges);
			projectManager.saveOrUpdateProject(project);
			
			if (anyChanges && project.isNotificationEnabled())
			{
				AlarmAnalyzer alarmAnalyzer = (AlarmAnalyzer) AppContext.getApplicationContext().getBean("AlarmAnalyzer");
				alarmAnalyzer.generateListAlarm(project, changedUrls);
			}
			logger.info("Finished list-crawling for project: " + project.getProjectName() + " id: " + project.getProjectId() + " ,# of crawled urls: " + urlList.size());

		}		
		finally
		{
			lock.unlock();
		}
	}
}
