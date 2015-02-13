package de.solidsearch.bot.dao.restservices;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
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
import de.solidsearch.shared.data.SharedDocument;

@Component("URLRestManager")
@Scope(value = "prototype")
public class URLRestManager implements Serializable
{
	private static final long serialVersionUID = -4592338335000863435L;

	@Autowired
	SessionFactory sessionFactory;
	@Autowired
	BotConfig config;
	@Autowired
	ProjectManager projectManager;
	
	@SuppressWarnings("unchecked")
	public List<SharedDocument> getNewestURLsByProjectID(long projectID, long fromTimestamp)
	{
		Session session = null;
		try
		{	
			Project project = projectManager.getProjectByID(projectID);
			if (project == null)
				return new ArrayList<SharedDocument>();
			
			Entry<String, Calendar> entry = projectManager.getNewestCrawlingEntry(project);
			
			if (entry == null)
				return new ArrayList<SharedDocument>();
			
			session = sessionFactory.openSession();
			StringBuffer queryString = new StringBuffer();
						
			queryString.append("select urlname as uri, foundtimestamp, canonicalTag, httpstatuscode, cast('").append(entry.getValue().getTimeInMillis()).append("' as bigint) as lastcrawledtimestamp,contenthashcode,backgroundid,qualityscore,spamscore,readinglevel,varietytopicscore,adscripts,relevantimages,responsetime,pagesize,pageRank,qwlocale,onpagetext,title,metadescription,topicKeywordOneTerm as topicKeyword, metarobotsindex as robotsIndex,duplicateTitle,duplicateMetaDescription,duplicateContent,trailingSlashIssue,timeout,pagination,dcprimary from URL_").append(entry.getKey()).append(" where externalLink=false AND foundtimestamp > ").append(fromTimestamp).append(" order by foundtimestamp asc limit 500");
			SQLQuery query = session.createSQLQuery(queryString.toString());
			addScalars(query);
			query.setResultTransformer(Transformers.aliasToBean(SharedDocument.class));

			List<SharedDocument> docs = (List<SharedDocument>) query.list();
			session.flush();
			
			return docs;
		} 
		finally
		{
			if (session != null)
				session.close();
		}
	}	
	
	private void addScalars(SQLQuery query)
	{
		query.addScalar("URI", StringType.INSTANCE);
		query.addScalar("foundTimestamp", LongType.INSTANCE);
		query.addScalar("canonicalTag", StringType.INSTANCE);
		query.addScalar("httpStatusCode", IntegerType.INSTANCE);
		query.addScalar("lastCrawledTimestamp", LongType.INSTANCE);
		query.addScalar("contentHashcode", StringType.INSTANCE);
		query.addScalar("backgroundId", ShortType.INSTANCE);
		query.addScalar("qualityScore", ShortType.INSTANCE);
		query.addScalar("spamScore", ShortType.INSTANCE);
		query.addScalar("readingLevel", ShortType.INSTANCE);
		query.addScalar("varietyTopicScore", ShortType.INSTANCE);
		query.addScalar("adScripts", ShortType.INSTANCE);
		query.addScalar("relevantImages", BooleanType.INSTANCE);
		query.addScalar("responseTime", IntegerType.INSTANCE);
		query.addScalar("pageSize", IntegerType.INSTANCE);
		query.addScalar("pageRank", DoubleType.INSTANCE);
		query.addScalar("qwLocale", StringType.INSTANCE);
		query.addScalar("onPageText", StringType.INSTANCE);
		query.addScalar("title", StringType.INSTANCE);
		query.addScalar("metaDescription", StringType.INSTANCE);
		query.addScalar("topicKeyword", StringType.INSTANCE);
		query.addScalar("robotsIndex", BooleanType.INSTANCE);
		query.addScalar("duplicateTitle", BooleanType.INSTANCE);
		query.addScalar("duplicateMetaDescription", BooleanType.INSTANCE);
		query.addScalar("duplicateContent", BooleanType.INSTANCE);
		query.addScalar("trailingSlashIssue", BooleanType.INSTANCE);
		query.addScalar("timeout", BooleanType.INSTANCE);
		query.addScalar("pagination", BooleanType.INSTANCE);
		query.addScalar("dcPrimary", BooleanType.INSTANCE);
	}
}
