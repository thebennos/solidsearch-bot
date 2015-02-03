package de.solidsearch.bot.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.solidsearch.bot.data.Project;
import de.solidsearch.bot.data.User;
import de.solidsearch.bot.data.UserGroup;
import de.solidsearch.bot.utils.AppContext;
import de.solidsearch.bot.utils.BotConfig;

@Component("ProjectManager")
@Scope(value = "prototype")
public class ProjectManager implements Serializable
{
	private static final long serialVersionUID = -1988105286088594161L;

	@Autowired
	SessionFactory sessionFactory;
	@Autowired
	BotConfig config;

	private static final Logger logger = Logger.getLogger(ProjectManager.class.getName());

	public Project getProjectByID(long projectId)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(Project.class).add(Restrictions.eq("projectId", projectId));
			@SuppressWarnings("unchecked")
			List<Project> cjL = (List<Project>) crit.list();
			tx.commit();

			if (cjL.isEmpty())
				return null;
			return cjL.get(0);
		} finally
		{
			if (session != null)
				session.close();
		}
	}

	public List<Project> getProjectsByUserGroupId(long userGroupId)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();

			Criteria crit = session.createCriteria(Project.class);
			crit.add(Restrictions.eq("userGroupId", userGroupId));
			crit.addOrder(Order.asc("projectName"));
			@SuppressWarnings("unchecked")
			List<Project> cjL = (List<Project>) crit.list();
			tx.commit();
			if (cjL.isEmpty())
				return null;
			return cjL;
		} finally
		{
			if (session != null)
				session.close();
		}
	}

	public List<Project> getActiveAndNotRunningExplorativProjects()
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Project.class).add(Restrictions.eq("isActive", true));
			crit.add(Restrictions.eq("isRunning", false));
			crit.add(Restrictions.eq("siteCrawlingEnabled", true));
			crit.addOrder(Order.asc("lastRunTimestamp"));
			@SuppressWarnings("unchecked")
			List<Project> cjL = (List<Project>) crit.list();
			return cjL;
		} finally
		{
			if (session != null)
				session.close();
		}
	}
	
	public List<Project> getActiveAndNotRunningURLListProjects()
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Project.class).add(Restrictions.eq("isActive", true));
			crit.add(Restrictions.eq("isRunning", false));
			crit.add(Restrictions.eq("listCrawlingEnabed", true));
			crit.addOrder(Order.asc("listJobLastRunTimestamp"));
			@SuppressWarnings("unchecked")
			List<Project> cjL = (List<Project>) crit.list();
			return cjL;
		} finally
		{
			if (session != null)
				session.close();
		}
	}

	public void saveOrUpdateProject(Project cj)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(cj);
			tx.commit();
		} finally
		{
			if (session != null)
				session.close();
		}
	}

	public HashMap<String, Calendar> getInitialAvailableCrawlings(long projectId, long userGroupId, boolean remoteProject)
	{
		String[] databaseIDs = getProjectDataBaseIDs(projectId, userGroupId, remoteProject);
		HashMap<String, Calendar> availableCrawlings = new HashMap<String, Calendar>();

		Calendar timestamp = Calendar.getInstance();
		timestamp.setTimeInMillis(0);

		for (int i = 0; i < databaseIDs.length; i++)
		{
			availableCrawlings.put(databaseIDs[i], timestamp);
		}
		return availableCrawlings;
	}

	public Project rotateDatabaseIdsForURLTableAndSwitch(Project project)
	{
		HashMap<String, Calendar> availableCrawlings = project.getAvailableCrawlings();

		Calendar projectLastRun = Calendar.getInstance();
		projectLastRun.setTimeInMillis(project.getLastRunTimestamp());
		long oldestEntryTimestamp = project.getLastRunTimestamp();
		// first at all put this new run to HashMap because it should be
		// available for UI...
		availableCrawlings.put(project.getCrawlerDatabaseId(), projectLastRun);

		// now search for last available data
		String oldestDatabaseId = "";
		for (Map.Entry<String, Calendar> entry : availableCrawlings.entrySet())
		{
			long entryTimestamp = entry.getValue().getTimeInMillis();

			if (entryTimestamp < oldestEntryTimestamp)
			{
				oldestEntryTimestamp = entryTimestamp;
				oldestDatabaseId = entry.getKey();
			}
		}

		// reset timestamp of oldest to zero...
		Calendar timestamp = Calendar.getInstance();
		timestamp.setTimeInMillis(0);
		availableCrawlings.put(oldestDatabaseId, timestamp);
		// make this to the new databaseID
		project.setCrawlerDatabaseId(oldestDatabaseId);
		project.setAvailableCrawlings(availableCrawlings);
		return project;
	}
	
	public Map.Entry<String, Calendar> getNewestCrawlingEntry(Project project)
	{
		HashMap<String, Calendar> availableCrawlings = project.getAvailableCrawlings();
		// now search for last available data
		Map.Entry<String, Calendar> crawling = null;
		long timestamp = 0;
		for (Map.Entry<String, Calendar> entry : availableCrawlings.entrySet())
		{
			long entryTimestamp = entry.getValue().getTimeInMillis();

			if (entryTimestamp > timestamp)
			{
				timestamp = entryTimestamp;
				crawling = entry;
			}
		}
		return crawling;
	}

	public User switchUsersettingsToProjectAndNewestDate(User user, Project project)
	{
		HashMap<String, Calendar> availableCrawlings = project.getAvailableCrawlings();

		boolean noDataFound = true;
		String initialDatabaseId = "";
		String newestDatabaseId = "";
		long newestEntryTimestamp = 0;
		Calendar newestTimestampCalendar = Calendar.getInstance();

		// now search for newest available data
		for (Map.Entry<String, Calendar> entry : availableCrawlings.entrySet())
		{
			long entryTimestamp = entry.getValue().getTimeInMillis();

			if (entryTimestamp > newestEntryTimestamp)
			{
				newestEntryTimestamp = entryTimestamp;
				newestDatabaseId = entry.getKey();
				newestTimestampCalendar.setTimeInMillis(newestEntryTimestamp);
				noDataFound = false;
			}
			initialDatabaseId = entry.getKey();
		}

		if (noDataFound)
			user.setUiDatabaseId(initialDatabaseId);
		else
			user.setUiDatabaseId(newestDatabaseId);

		user.setSelectedProjectDate(newestTimestampCalendar);
		user.setSelectedProjectId(project.getProjectId());

		return user;
	}

	public String[] getProjectDataBaseIDs(long projectId, long userGroupId, boolean remoteProject)
	{
		ArrayList<String> ids = new ArrayList<String>();

		int count = 0;
		
		if (remoteProject)
		{
			count = config.detailedCrawlingDataByRemoteProject;
		}
		else
		{
			count = config.detailedCrawlingDataByProject;
		}
		
		for (int i = 0; i < count; i++)
		{
			ids.add(userGroupId + "_" + projectId + "_" + i);
		}
		return ids.toArray(new String[ids.size()]);
	}

	/**
	 * Method deletes given project. If its the last project, method delete it and creates an empty new project.
	 * 
	 * @param projectId
	 * @param user
	 *            updated User with new project settings
	 * @return user updated User with new project settings
	 */
	public User deleteProject(long projectId, User user, boolean remoteProject)
	{
		// remove project from user settings
		UserManager userManager = (UserManager) AppContext.getApplicationContext().getBean("UserManager");
		UserGroup group = user.getUserGroup();

		if (group.getCurrentProjects() <= 0)
		{
			logger.error("deleteProject was called without any projects? projectID:" + projectId);
			return null;
		}

		group.setCurrentProjects(group.getCurrentProjects() - 1);
		user.setUserGroup(group);

		URLManager urlManager = (URLManager) AppContext.getApplicationContext().getBean("URLManager");
		urlManager.dropURLTableForProject(getProjectDataBaseIDs(projectId, group.getId(), remoteProject));

		ProjectKeywordsManager keywordManager = (ProjectKeywordsManager) AppContext.getApplicationContext().getBean("ProjectKeywordsManager");
		keywordManager.dropTempURLTables(projectId);
		
		URLListManager listManager = (URLListManager) AppContext.getApplicationContext().getBean("URLListManager");
		listManager.setProjectId(projectId);
		listManager.dropURLListTableForGivenProject();
	
		// remove project
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();

			Query query = session.createQuery("delete Project where projectId = :projectId");
			query.setParameter("projectId", projectId);
			query.executeUpdate();

			query = session.createQuery("delete ProjectSummary where projectId = :projectId");
			query.setParameter("projectId", projectId);
			query.executeUpdate();
			
			tx.commit();
		} finally
		{
			if (session != null)
				session.close();
		}

		if (group.getCurrentProjects() <= 0)
		{
			createProject(user,false);
		} else
		{
			// switch user setting to the first available project
			user = switchUsersettingsToProjectAndNewestDate(user, getProjectsByUserGroupId(user.getUserGroup().getId()).get(0));
			userManager.saveOrUpdateUser(user);
		}

		return user;
	}

	/**
	 * Method creates a new project belonging to the given user. 
	 * Method also sets new project to default project in user settings.
	 * 
	 * @param user
	 * @return user updated User with new project settings
	 */
	public Project createProject(User user, boolean remoteProject)
	{
		UserManager userManager = (UserManager) AppContext.getApplicationContext().getBean("UserManager");

		UserGroup group = user.getUserGroup();

		// generate new empty project
		Project project = new Project(user.getUserGroup().getId());
		saveOrUpdateProject(project);
		String crawlerDatabaseId = getProjectDataBaseIDs(project.getProjectId(), group.getId(), remoteProject)[0];
		String uiDatebaseID = getProjectDataBaseIDs(project.getProjectId(), group.getId(), remoteProject)[1];
		project.setCrawlerDatabaseId(crawlerDatabaseId);
		user.setUiDatabaseId(uiDatebaseID);

		project.setAvailableCrawlings(getInitialAvailableCrawlings(project.getProjectId(), group.getId(),remoteProject));
		saveOrUpdateProject(project);

		// add this new project to usergroup
		group.setCurrentProjects(group.getCurrentProjects() + 1);
		user.setUserGroup(group);
		userManager.saveOrUpdateUser(user);

		URLManager urlManager = (URLManager) AppContext.getApplicationContext().getBean("URLManager");
		urlManager.dropURLTableForProject(getProjectDataBaseIDs(project.getProjectId(), group.getId(),remoteProject));
		urlManager.createNewURLTableForProject(getProjectDataBaseIDs(project.getProjectId(), group.getId(),remoteProject));

		Calendar lastrunDate = Calendar.getInstance();
		lastrunDate.setTimeInMillis(project.getLastRunTimestamp());

		user.setSelectedProjectId(project.getProjectId());
		user.setSelectedProjectDate(lastrunDate);
		
		ProjectKeywordsManager projectKeywordsManager = (ProjectKeywordsManager) AppContext.getApplicationContext().getBean("ProjectKeywordsManager");
		projectKeywordsManager.createKeywordTableIfNotExist(project.getProjectId());
		
		userManager.saveOrUpdateUser(user);

		URLListManager listManager = (URLListManager) AppContext.getApplicationContext().getBean("URLListManager");
		listManager.setProjectId(project.getProjectId());
		listManager.createListURLTableForGivenProject();
		
		return project;
	}
}
