package de.solidsearch.bot.dao;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.solidsearch.bot.data.ProjectSummary;
import de.solidsearch.bot.data.URL;
import de.solidsearch.bot.utils.BotConfig;

@Component("ProjectSummaryManager")
@Scope(value = "prototype")
public class ProjectSummaryManager implements Serializable
{
	private static final long serialVersionUID = 8209996053148166373L;

	private static final Logger logger = Logger.getLogger(ProjectSummaryManager.class.getName());

	@Autowired
	SessionFactory sessionFactory;
	@Autowired
	BotConfig config;
	
	/**
	 * Methode liefert die erste oder letzte verfügbare ProjectSummary für das angegebene Projekt.
	 * @param projectId
	 * @param first true=die erste ProjectSummary, false=die letzte ProjectSummary
	 * @return ProjectSummary
	 */
	public ProjectSummary getFirstOrLastProjectSummaryByID(long projectId, boolean first)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(ProjectSummary.class).add(Restrictions.eq("projectId", projectId));
						
			crit.add(Restrictions.eq("finished", true));
			if (first)
			{
				crit.addOrder(Order.asc("resultFromTimestampMills"));
			}
			else
			{
				crit.addOrder(Order.desc("resultFromTimestampMills"));
			}
			crit.setMaxResults(1);
			@SuppressWarnings("unchecked")
			List<ProjectSummary> cjL = (List<ProjectSummary>) crit.list();
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
	
	public ProjectSummary getProjectSummaryByProjectIdAndTimestamp(long projectId, long timestamp, boolean onlyFinishedSummarys)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(ProjectSummary.class).add(Restrictions.eq("projectId", projectId));
			crit.add(Restrictions.eq("resultFromTimestampMills", timestamp));
			if (onlyFinishedSummarys)
			{
				crit.add(Restrictions.eq("finished", true));
			}
			@SuppressWarnings("unchecked")
			List<ProjectSummary> cjL = (List<ProjectSummary>) crit.list();
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
	
	public ProjectSummary getNextProjectSummaryByProjectIdBeforeTimestamp(long projectId, long timestamp)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(ProjectSummary.class).add(Restrictions.eq("projectId", projectId));
			crit.add(Restrictions.lt("resultFromTimestampMills", timestamp));
			crit.add(Restrictions.eq("finished", true));
			crit.addOrder(Order.desc("resultFromTimestampMills"));
			crit.setMaxResults(1);
			
			@SuppressWarnings("unchecked")
			List<ProjectSummary> cjL = (List<ProjectSummary>) crit.list();
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

	public void saveOrUpdateProjectSummary(ProjectSummary cs)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(cs);
			tx.commit();
		} finally
		{
			if (session != null)
				session.close();
		}

	}

	public List<ProjectSummary> getAllProjectSummarys(long projectId)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(ProjectSummary.class).add(Restrictions.eq("projectId", projectId));
			crit.add(Restrictions.eq("finished", true));
			crit.addOrder(Order.asc("resultFromTimestampMills"));
			@SuppressWarnings("unchecked")
			List<ProjectSummary> cjL = (List<ProjectSummary>) crit.list();
			tx.commit();
			return cjL;
		} finally
		{
			if (session != null)
				session.close();
		}

	}

	public void deleteSummaryDataOlderThan(int days)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, (days * (-1)));

			Query query = session.createQuery("delete ProjectSummary where resultFromTimestampMills < :timestamp");
			query.setParameter("timestamp", cal.getTimeInMillis());

			query.executeUpdate();
			
			tx.commit();
		} finally
		{
			if (session != null)
				session.close();
		}

	}
	
	public void deleteAllSummarysForProject(long projectId)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();

			Query query = session.createQuery("delete ProjectSummary where projectId = :projectId");
			query.setParameter("projectId", projectId);

			query.executeUpdate();
			
			tx.commit();
		} finally
		{
			if (session != null)
				session.close();
		}

	}

	public ProjectSummary getAverageValuesOfProjectSummarys(long projectId, long timestampInMills, int daysBack)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			SQLQuery query = session.createSQLQuery("select avg(crawlingDurationMills) as crawlingDurationMills, avg(internalURLs) as internalURLs, avg(internalNofollowURLs) as internalNofollowURLs, "
					+ "avg(internalFollowURLs) as internalFollowURLs, " + "avg(internalNoindexURLs) as internalNoindexURLs, avg(internalIndexURLs) as internalIndexURLs, avg(externalURLs) as externalURLs, "
					+ "avg(externalURLsDifferentDomains) as externalURLsDifferentDomains, avg(crawledURLs) as crawledURLs, avg(clientErrorURLs) as clientErrorURLs, "
					+ "avg(timeoutURLs) as timeoutURLs, avg(redirectionURLs) as redirectionURLs, avg(serverErrorURLs) as serverErrorURLs, avg(avgResponseTimeMills) as avgResponseTimeMills, "
					+ "avg(minResponseTimeMills) as minResponseTimeMills, " + "avg(standardDeviationResponseTimeMills) as standardDeviationResponseTimeMills, avg(avgPageSize) as avgPageSize, avg(minPageSize) as minPageSize, "
					+ "avg(maxPageSize) as maxPageSize, avg(missingTitleURLs) as missingTitleURLs, avg(longTitleURLs) as longTitleURLs, avg(missingMetaDescriptionURLs) as missingMetaDescriptionURLs, "
					+ "avg(longMetaDescriptionURLs) as longMetaDescriptionURLs, avg(missingOnPageTextURLs) as missingOnPageTextURLs, "
					+ "avg(duplicateContentURLs) as duplicateContentURLs, avg(canonicaltagToSource) as canonicaltagToSource, avg(canonicalTagIssues) as canonicalTagIssues, avg(missingH1) as missingH1, "
					+ "avg(headlinesNotInRightOrderURLs) as headlinesNotInRightOrderURLs, avg(duplicateTitleURLs) as duplicateTitleURLs, avg(duplicateMetaDescriptionURLs) as duplicateMetaDescriptionURLs, "
					+ "avg(duplicateH1URLs) as duplicateH1URLs, avg(differentURLSameAnchor) as differentURLSameAnchor, avg(keywordOrientationShortTermURLs) as keywordOrientationShortTermURLs, avg(keywordOrientationTwoTermsURLs) as keywordOrientationTwoTermsURLs "
					+ " from (select * from ProjectSummary where finished=true AND projectId=:myprojectid and resultFromTimestampMills<:fromtime order by resultFromTimestampMills desc limit :numOfDays) as foo");
			query.setParameter("myprojectid", projectId);
			query.setParameter("fromtime", timestampInMills);
			query.setParameter("numOfDays", daysBack);

			query.addScalar("crawlingDurationMills", LongType.INSTANCE);
			query.addScalar("internalURLs", LongType.INSTANCE);
			query.addScalar("internalNofollowURLs", LongType.INSTANCE);
			query.addScalar("internalFollowURLs", LongType.INSTANCE);
			query.addScalar("internalNoindexURLs", LongType.INSTANCE);
			query.addScalar("internalIndexURLs", LongType.INSTANCE);
			query.addScalar("externalURLs", LongType.INSTANCE);
			query.addScalar("externalURLsDifferentDomains", LongType.INSTANCE);
			query.addScalar("crawledURLs", LongType.INSTANCE);
			query.addScalar("clientErrorURLs", LongType.INSTANCE);
			query.addScalar("timeoutURLs", LongType.INSTANCE);
			query.addScalar("redirectionURLs", LongType.INSTANCE);
			query.addScalar("serverErrorURLs", LongType.INSTANCE);
			query.addScalar("avgResponseTimeMills", IntegerType.INSTANCE);
			query.addScalar("minResponseTimeMills", IntegerType.INSTANCE);
			query.addScalar("standardDeviationResponseTimeMills", IntegerType.INSTANCE);
			query.addScalar("avgPageSize", LongType.INSTANCE);
			query.addScalar("minPageSize", LongType.INSTANCE);
			query.addScalar("maxPageSize", LongType.INSTANCE);
			query.addScalar("missingTitleURLs", LongType.INSTANCE);
			query.addScalar("longTitleURLs", LongType.INSTANCE);
			query.addScalar("missingMetaDescriptionURLs", LongType.INSTANCE);
			query.addScalar("longMetaDescriptionURLs", LongType.INSTANCE);
			query.addScalar("missingOnPageTextURLs", LongType.INSTANCE);
			query.addScalar("duplicateContentURLs", LongType.INSTANCE);
			query.addScalar("canonicaltagToSource", LongType.INSTANCE);
			query.addScalar("canonicalTagIssues", LongType.INSTANCE);
			query.addScalar("missingH1", LongType.INSTANCE);
			query.addScalar("headlinesNotInRightOrderURLs", LongType.INSTANCE);
			query.addScalar("duplicateTitleURLs", LongType.INSTANCE);
			query.addScalar("duplicateMetaDescriptionURLs", LongType.INSTANCE);
			query.addScalar("duplicateH1URLs", LongType.INSTANCE);
			query.addScalar("differentURLSameAnchor", LongType.INSTANCE);
			query.addScalar("keywordOrientationShortTermURLs", LongType.INSTANCE);
			query.addScalar("keywordOrientationTwoTermsURLs", LongType.INSTANCE);
			
			ProjectSummary avgValues = null;
			try
			{
				query.setResultTransformer(Transformers.aliasToBean(ProjectSummary.class));
				avgValues = (ProjectSummary) query.uniqueResult();
			} catch (org.hibernate.PropertyAccessException e)
			{
				// if we get this exception we have not history of summarys...
				// hibernate ties now to convert null result to a object which
				// fails...
				logger.info("Try to calculate average values without history of projectSummarys...");
			}
			tx.commit();
			return avgValues;
		} finally
		{
			if (session != null)
				session.close();
		}

	}

	public ProjectSummary updateProjectSummaryWithoutPersist(ProjectSummary projectSummary, URL url)
	{
		projectSummary.setCrawledURLs(projectSummary.getCrawledURLs() + 1);

		if (url.isTimeout() == true)
		{
			projectSummary.setTimeoutURLs(projectSummary.getTimeoutURLs() + 1);
			return projectSummary;
		} else if (url.getHttpStatusCode() >= 500)
		{
			projectSummary.setServerErrorURLs(projectSummary.getServerErrorURLs() + 1);
			return projectSummary;
		} else if (url.getHttpStatusCode() >= 400)
		{
			projectSummary.setClientErrorURLs(projectSummary.getClientErrorURLs() + 1);
			return projectSummary;
		} else if (url.getHttpStatusCode() >= 300)
		{
			projectSummary.setRedirectionURLs(projectSummary.getRedirectionURLs() + 1);
			return projectSummary;
		}

		if (!url.getMetaRobotsFollow())
			projectSummary.setInternalNofollowURLs(projectSummary.getInternalNofollowURLs() + 1);
		else
			projectSummary.setInternalFollowURLs(projectSummary.getInternalFollowURLs() + 1);

		if (!url.getMetaRobotsIndex())
			projectSummary.setInternalNoindexURLs(projectSummary.getInternalNoindexURLs() + 1);
		else if (url.getMetaRobotsIndex())
			projectSummary.setInternalIndexURLs(projectSummary.getInternalIndexURLs() + 1);

		String title = url.getTitle();
		if ((title.length() <= 0 || title.split("\\s+").length <= 1) && url.getMetaRobotsIndex())
			projectSummary.setMissingTitleURLs(projectSummary.getMissingTitleURLs() + 1);
		if (title.length() > 57 && url.getMetaRobotsIndex())
			projectSummary.setLongTitleURLs(projectSummary.getLongTitleURLs() + 1);

		String description = url.getMetaDescription();
		if (description.length() < 3 && url.getMetaRobotsIndex())
			projectSummary.setMissingMetaDescriptionURLs(projectSummary.getMissingMetaDescriptionURLs() + 1);
		if (description.length() > 160 && url.getMetaRobotsIndex())
			projectSummary.setLongMetaDescriptionURLs(projectSummary.getLongMetaDescriptionURLs() + 1);

		if (url.getContentHashcode() == null && url.getMetaRobotsIndex())
			projectSummary.setMissingOnPageTextURLs(projectSummary.getMissingOnPageTextURLs() + 1);

		if (url.getURLName().equalsIgnoreCase(url.getCanonicalTag()))
			projectSummary.setCanonicaltagToSource(projectSummary.getCanonicaltagToSource() + 1);

		if (url.getH1().length() <= 2 && url.getMetaRobotsIndex())
			projectSummary.setMissingH1(projectSummary.getMissingH1() + 1);

		if (url.isHeadlinesNotInRightOrder() && url.getMetaRobotsIndex())
			projectSummary.setHeadlinesNotInRightOrderURLs(projectSummary.getHeadlinesNotInRightOrderURLs() + 1);

		if (!url.isGoogleAnalyticsCodeFound())
		{
			projectSummary.setGoogleAnalyticsUsed(true);
			projectSummary.setMissingGoogleAnalyticsCode(projectSummary.getMissingGoogleAnalyticsCode() + 1);
		}
		
		return projectSummary;
	}
}
