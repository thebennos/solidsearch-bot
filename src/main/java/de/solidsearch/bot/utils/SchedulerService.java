package de.solidsearch.bot.utils;


import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.solidsearch.bot.businesslogic.ListJobWorker;
import de.solidsearch.bot.businesslogic.ProjectJobWorker;
import de.solidsearch.bot.dao.ProjectSummaryManager;

@Service
@EnableScheduling
public class SchedulerService
{
	private static AtomicInteger parallelRunningProjects = new AtomicInteger();
	private static boolean budgetAlreadyReset = false;
	private static int lastDayOfSummaryDelete = -1;
	
	@Async
	@Scheduled(fixedDelay = 15000)
	public void doSchedule()
	{
		Calendar now = Calendar.getInstance();
		
		// on monday, we want to reset project budgets... 
		if (now.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
		{
			if (budgetAlreadyReset == false)
			{
				// wait for stopping all crawlers
				BotConfig.setAllProjectsPauseSignal(true);
				while (parallelRunningProjects.get() != 0)
				{
					try
					{
						Thread.sleep(10);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}	
				}

				BotConfig.setAllProjectsPauseSignal(false);
				budgetAlreadyReset = true;
			}
		}
		else
		{
			budgetAlreadyReset = false;
		}
		
		if (BotConfig.isAllProjectsPauseSignal() == false)
		{
			parallelRunningProjects.incrementAndGet();
			ProjectJobWorker projectWorker = (ProjectJobWorker) AppContext.getApplicationContext().getBean("ProjectJobWorker");
			projectWorker.checkAndStartInactiveProject();
			
			ListJobWorker listJobWorker = (ListJobWorker) AppContext.getApplicationContext().getBean("ListJobWorker");
			listJobWorker.checkAndStartInactiveProject();
			
			parallelRunningProjects.decrementAndGet();
		}
		
		// delete old project summarys...
		synchronized (this)
		{
			int currentDay = now.get(Calendar.DAY_OF_WEEK);
			if (lastDayOfSummaryDelete != currentDay)
			{
				ProjectSummaryManager projectSummaryManager = (ProjectSummaryManager) AppContext.getApplicationContext().getBean("ProjectSummaryManager");
				projectSummaryManager.deleteSummaryDataOlderThan(90);
				lastDayOfSummaryDelete = currentDay;
			}
		}
	}
}
