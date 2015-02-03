package de.solidsearch.bot.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.solidsearch.bot.utils.BotConfig;
import de.solidsearch.shared.data.KeywordStem;

@Component("ProjectKeywordsManager")
@Scope(value = "prototype")
public class ProjectKeywordsManager implements Serializable
{
	private static final long serialVersionUID = 8108996051148166371L;

	private static final Logger logger = Logger.getLogger(ProjectKeywordsManager.class.getName());

	@Autowired
	SessionFactory sessionFactory;
	@Autowired
	BotConfig config;
	

	public void createKeywordTableIfNotExist(long projectId)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();

			String sql = "CREATE UNLOGGED TABLE IF NOT EXISTS keywords_" + projectId + " (keywords varchar(270) not null, keywordstem varchar(270) not null, maxweight smallint not null, frequency bigint not null, relevantindocuments integer not null)";
			session.createSQLQuery(sql).executeUpdate();

			session.flush();

		}
		catch (Exception e)
		{
			logger.error("Exception in createTempURLTables() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}
	
	public void dropTempURLTables(long projectId)
	{
		Session session = null;
		try
		{
			String sql;
			session = sessionFactory.openSession();
			
			sql = "drop table if exists keywords_" + projectId;
			session.createSQLQuery(sql).executeUpdate();

			session.flush();

		}
		catch (Exception e)
		{
			logger.error("Exception in dropTempURLTables() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	
	public void saveKeywords(ArrayList<KeywordStem> keywords,long projectId)
	{
		Connection conn = null;
		JdbcConnectionAccess jdbc = null;

		try
		{
			SessionImplementor sfi = (SessionImplementor) sessionFactory.openSession();
			jdbc = sfi.getJdbcConnectionAccess();

			conn = jdbc.obtainConnection();
			
			for (int i = 0; i < keywords.size(); i++)
			{
				PreparedStatement insert = conn.prepareStatement("insert into keywords_" + projectId + " values (?,?,?,?,?)");

				while (i % 50 != 0 && i < keywords.size())
				{
					insert.setString(1, keywords.get(i).getMostFrequentTerm());
					insert.setString(2, keywords.get(i).getKeywordStem());
					insert.setShort(3, keywords.get(i).getKeywordWeight());
					insert.setLong(4, keywords.get(i).getFrequency());
					insert.setInt(5, keywords.get(i).getRelevantInDocuments());
					
					insert.addBatch();
					i++;
				}
				insert.executeBatch();
			}

		}
		catch (Exception e)
		{
			logger.error("Exception in saveKeywords() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (jdbc != null && conn != null)
			{
				try
				{
					jdbc.releaseConnection(conn);
				}
				catch (SQLException e)
				{
					logger.error("ProjectKeywordsManager: unable to release connection in saveKeywords() " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	public List<Object[]> getAllKeywordsPaged(int page,long projectId)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();


			sql.append("select keywords,maxweight,frequency,relevantindocuments from keywords_").append(projectId).append(" order by frequency desc limit ").append((page+1) * 5000).append(" offset ").append(page * 5000);
			SQLQuery query = session.createSQLQuery(sql.toString());

			@SuppressWarnings("unchecked")
			List<Object[]> keywords = (List<Object[]>) query.list();
			session.flush();

			return keywords;
		}
		catch (Exception e)
		{
			logger.error("Exception in getAllKeywordsPaged() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return null;

	}

	public void truncateKeywords(long projectId)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();

			String sql = "truncate keywords_" + projectId;
			session.createSQLQuery(sql).executeUpdate();

			session.flush();

		}
		catch (Exception e)
		{
			logger.error("Exception in truncateKeywords() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}
	
}
