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
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.solidsearch.bot.dao.ProjectManager;
import de.solidsearch.bot.data.Project;
import de.solidsearch.bot.utils.BotConfig;
import de.solidsearch.shared.data.SharedExternalLink;

@Component("ExternalLinkRestManager")
@Scope(value = "prototype")
public class ExternalLinkRestManager implements Serializable
{
	private static final long serialVersionUID = -2212338335000863435L;

	@Autowired
	SessionFactory sessionFactory;
	@Autowired
	BotConfig config;
	@Autowired
	ProjectManager projectManager;
	
	@SuppressWarnings("unchecked")
	public List<SharedExternalLink> getNewestExternalLinksByProjectID(long projectID, int page)
	{
		Session session = null;
		try
		{	
			Project project = projectManager.getProjectByID(projectID);
			if (project == null)
				return new ArrayList<SharedExternalLink>();
			
			Entry<String, Calendar> entry = projectManager.getNewestCrawlingEntry(project);
			
			if (entry == null)
				return new ArrayList<SharedExternalLink>();
			
			session = sessionFactory.openSession();
			StringBuffer queryString = new StringBuffer();
						
			queryString.append("select urlname,foundaturl,externallinkpower from URL_").append(entry.getKey()).append(" where externalLink=true and externallinkpower<>0 order by urlname desc limit 1000 offset ").append(page*1000);
			SQLQuery query = session.createSQLQuery(queryString.toString());
			addScalars(query);
			query.setResultTransformer(Transformers.aliasToBean(SharedExternalLink.class));

			List<SharedExternalLink> links = (List<SharedExternalLink>) query.list();
			session.flush();
			return links;
		} 
		finally
		{
			if (session != null)
				session.close();
		}
	}	
	
	private void addScalars(SQLQuery query)
	{
		query.addScalar("URLName", StringType.INSTANCE);
		query.addScalar("foundAtURL", StringType.INSTANCE);
		query.addScalar("externalLinkPower", ShortType.INSTANCE);
	}
}
