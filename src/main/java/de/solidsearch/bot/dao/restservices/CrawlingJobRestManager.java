package de.solidsearch.bot.dao.restservices;

import java.io.Serializable;
import java.util.ArrayList;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.solidsearch.bot.dao.ProjectManager;
import de.solidsearch.bot.dao.UserManager;
import de.solidsearch.bot.data.Project;
import de.solidsearch.bot.data.User;
import de.solidsearch.bot.data.enumerations.ProjectInfo;
import de.solidsearch.bot.utils.BotConfig;
import de.solidsearch.shared.data.CrawlingJob;

@Component("ProjectRestManager")
@Scope(value = "prototype")
public class CrawlingJobRestManager implements Serializable
{
	private static final long serialVersionUID = 2113128347416054945L;

	@Autowired
	SessionFactory sessionFactory;
	@Autowired
	BotConfig config;
	@Autowired
	ProjectManager projectManager;
	@Autowired
	UserManager userManager;
	

	/**
	 * Method places a new crawling job to queue. 
	 * If method returns -1 something went wrong in job definition or job cannot be created.
	 * @param job
	 * @return projectId of bot project or -1 if an error occurs
	 */
	public long placeCrawlingJob(CrawlingJob job)
	{
		if (job.getDomainName() == null || job.getDomainName().length() < 1)
			return -1;
		if (job.getUrlLimit() < 1 || job.getUrlLimit() > config.MAX_URLS)
			return -1;
		if (job.getDiscoverLimit() < 100 || job.getDiscoverLimit() > config.MAX_URLS)
			return -1;
		if (job.getUserAgent() == null || job.getUserAgent().length() < 1)
			return -1;
	
		User admin = userManager.getUserByEmail(config.ADMIN_USER);
		
		Project project = projectManager.createProject(admin,true);
		
		project.setNotificationEmailAddresses(config.ADMIN_USER);
		project.setNotificationEnabled(false);
		
		project.setBotUserAgent(job.getUserAgent());
		project.setProjectName(job.getDomainName() + "_remote_" + project.getProjectId());
		project.setRootDomainToCrawlWithoutProtocol(job.getDomainName());
		project.setCrawlLimit(job.getUrlLimit());
		project.setDiscoverLimit(job.getDiscoverLimit());
		project.setActive(true);
		project.setNewProject(false);
		project.setRemoteProject(true);
		
		projectManager.saveOrUpdateProject(project);
		
		return project.getProjectId();
	}//end method...
	
	/**
	 * Checks the status of the crawling.
	 *  0 = already working
	 *  1 = not started until now
	 *  3 = crawling finished successfully
	 *  4 = error occured, crawling stopped
	 * @param job
	 * @return
	 */
	public short checkCrawlingJobStatus(CrawlingJob job)
	{
		Project project = projectManager.getProjectByID(job.getCrawlerProjectId());
		
		if (project.isDisabledByError())
		{		
			return 4;
		}
		else if (project.isRunning() == false)
		{
			if (project.getLastRunTimestamp() > job.getStartTimestamp())
			{
				return 3;
			}
			else
			{
				return 1;
			}
		}
		else
		{
			return 0;
		}
	}//end method...
	
	
	/**
	 * Method returns sting array of human readable information of project status.
	 * @param crawlerProjectId
	 * @return String array
	 */
	public String[] getInfoMessages(long crawlerProjectId)
	{
		Project project = projectManager.getProjectByID(crawlerProjectId);
		
		if (project == null)
			return null;
		
		ArrayList<Integer> codes = project.getInfoMessageCodes();
		
		if (codes.size() == 0)
			return new String[]{};
		
		ArrayList<String> messages = new ArrayList<String>(); 
		
		for (int i = 0; i < codes.size(); i++)
		{
			messages.add(ProjectInfo.getLocalizedUITextByMessageCode(codes.get(i)));
		}
		return messages.toArray(new String[messages.size()]);
	}
	
	/**
	 * Method checks if project exist.
	 * @param projectId
	 * @return
	 */
	public boolean checkIfProjectExists(long projectId)
	{	
		Project project = projectManager.getProjectByID(projectId);
		
		if (project != null)
			return true;
		else
			return false;
	}//end method...
	
	
	public boolean deleteCrawlingJobProject(long projectId)
	{
		User admin = userManager.getUserByEmail(config.ADMIN_USER);
		
		if (projectManager.getProjectByID(projectId).isRunning())
			return false;		
		if (projectManager.deleteProject(projectId,admin,true) == null)
			return false;
		else		
			return true;
	}

}
