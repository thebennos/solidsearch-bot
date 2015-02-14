package de.solidsearch.bot.dao.restservices;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map.Entry;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.solidsearch.bot.dao.ProjectManager;
import de.solidsearch.bot.data.Project;
import de.solidsearch.bot.utils.BotConfig;
import de.solidsearch.shared.data.SharedDomain;

@Component("DomainRestManager")
@Scope(value = "prototype")
public class DomainRestManager implements Serializable
{
	private static final long serialVersionUID = -2112318335100863435L;

	@Autowired
	SessionFactory sessionFactory;
	@Autowired
	BotConfig config;
	@Autowired
	ProjectManager projectManager;
	
	public SharedDomain getNewestDomainDataByProjectID(long projectID)
	{
		Session session = null;
		try
		{	
			Project project = projectManager.getProjectByID(projectID);
			if (project == null)
				return null;
			
			Entry<String, Calendar> entry = projectManager.getNewestCrawlingEntry(project);
			
			if (entry == null)
				return null;
			
			session = sessionFactory.openSession();
			StringBuffer queryString = new StringBuffer();
						
			queryString.append("select resultFromTimestampMills,qualityScore,totalLinks,internalURLs,internalNoindexURLs,internalIndexURLs,externalURLs,externalURLsDifferentDomains,crawledURLs,clientErrorURLs,timeoutURLs,redirectionURLs,serverErrorURLs,avgResponseTimeMills, 0 as medianResponseTimeMills,avgPageSize,totalCountOfRelevantKeywords,totalCountOfKeywords,readinglevel as avgReadingLevel,domainBrandName,homeDocument from projectsummary where projectid=").append(project.getProjectId()).append(" order by resultfromtimestampmills desc limit 1");
			SQLQuery query = session.createSQLQuery(queryString.toString());
			addScalars(query);
			query.setResultTransformer(Transformers.aliasToBean(SharedDomain.class));

			SharedDomain domain = (SharedDomain) query.uniqueResult();
			session.flush();
			
			return domain;
		} 
		finally
		{
			if (session != null)
				session.close();
		}
	}	
	
	private void addScalars(SQLQuery query)
	{
		query.addScalar("resultFromTimestampMills", LongType.INSTANCE);
		query.addScalar("qualityScore", ShortType.INSTANCE);
		query.addScalar("totalLinks", LongType.INSTANCE);
		query.addScalar("internalURLs", LongType.INSTANCE);
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
		query.addScalar("medianResponseTimeMills", IntegerType.INSTANCE);
		query.addScalar("avgPageSize", LongType.INSTANCE);
		query.addScalar("totalCountOfRelevantKeywords", LongType.INSTANCE);
		query.addScalar("totalCountOfKeywords", LongType.INSTANCE);
		query.addScalar("avgReadingLevel", DoubleType.INSTANCE);
		query.addScalar("domainBrandName", StringType.INSTANCE);
		query.addScalar("homeDocument", StringType.INSTANCE);
	}
}
