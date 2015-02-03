package de.solidsearch.bot.dao.restservices;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.solidsearch.bot.dao.ProjectKeywordsManager;
import de.solidsearch.bot.utils.BotConfig;

@Component("KeywordRestManager")
@Scope(value = "prototype")
public class KeywordRestManager implements Serializable
{
	private static final long serialVersionUID = 1014128347416055945L;

	@Autowired
	SessionFactory sessionFactory;
	@Autowired
	BotConfig config;
	@Autowired
	ProjectKeywordsManager projectKeywordsManager;
	

	public List<Object[]> getKeywordsByProject(long projectID, int page)
	{
		Session session = null;
		try
		{						
			return projectKeywordsManager.getAllKeywordsPaged(page, projectID);
		} 
		finally
		{
			if (session != null)
				session.close();
		}

	}//end method...
	
	

}
