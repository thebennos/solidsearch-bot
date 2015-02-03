package de.solidsearch.bot.dao;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.solidsearch.bot.data.URL;
import de.solidsearch.bot.data.enumerations.URLField;

@Component("URLListManager")
@Scope(value = "prototype")
public class URLListManager extends URLManager implements Serializable
{
	private static final long serialVersionUID = 4741680309317882432L;
	
	private static final Logger logger = Logger.getLogger(URLListManager.class.getName());

	public void insertNewURL(URL url)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			
			StringBuffer sql = new StringBuffer();
			sql.append("insert into URLList_").append(getProjectId()).append(insertRows).append(" values ").append(getSQLParameterList());

			SQLQuery query = session.createSQLQuery(sql.toString());
			setQueryParams(0, query, url, true);
			
			query.executeUpdate();
			session.flush();
			
		}
		catch (Exception e)
		{
			logger.error("Exception in insertURL() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}
	
	public List<URL> getURLsByName(String urlName, boolean noProtocolRecords)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			sql.append("select * from URLList_").append(getProjectId()).append(" where urlname ='").append(urlName).append("'");

			if (noProtocolRecords)
			{
				sql.append(" AND protocolRecord=false");
			}
			
			SQLQuery query = session.createSQLQuery(sql.toString());

			addScalars(query);

			query.setResultTransformer(Transformers.aliasToBean(URL.class));
			@SuppressWarnings("unchecked")
			List<URL> urlList = (List<URL>) query.list();

			session.flush();

			if (urlList.isEmpty())
				return null;
			return urlList;
		}
		catch (Exception e)
		{
			logger.error("Exception in getURLbyId() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return null;
	}
	
	@Override
	public URL getURLbyId(String id, int urlHashCode)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			sql.append("select * from URLList_").append(getProjectId()).append(" where protocolRecord=false AND id ='").append(id).append("'");

			SQLQuery query = session.createSQLQuery(sql.toString());

			addScalars(query);

			query.setResultTransformer(Transformers.aliasToBean(URL.class));
			@SuppressWarnings("unchecked")
			List<URL> urlList = (List<URL>) query.list();

			session.flush();

			if (urlList.isEmpty())
				return null;
			return urlList.get(0);
		}
		catch (Exception e)
		{
			logger.error("Exception in getURLbyId() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return null;
	}
	

	public long getCountOfChangesInProjectTable()
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			sql.append("select count(*) from URLList_").append(getProjectId()).append(" where protocolRecord=false AND changeCode<>0");

			SQLQuery query = session.createSQLQuery(sql.toString());

			BigInteger result = (BigInteger) query.uniqueResult();

			session.flush();

			return result.longValue();
		}
		catch (Exception e)
		{
			logger.error("Exception in isThereAnyChange() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return 0;
	}
	/*
	 * Method returns all URLs where a change was detected
	 * 
	 * returns null if no result
	 */
	public List<URL> getChangedURLs()
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			sql.append("select * from URLList_").append(getProjectId());
			sql.append(" where changeCode <> 0");
			
			SQLQuery query = session.createSQLQuery(sql.toString());

			addScalars(query);

			query.setResultTransformer(Transformers.aliasToBean(URL.class));
			@SuppressWarnings("unchecked")
			List<URL> urlList = (List<URL>) query.list();

			session.flush();

			if (urlList.isEmpty())
				return null;
			return urlList;
		}
		catch (Exception e)
		{
			logger.error("Exception in getChangedURLs() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return null;
	}
	/*
	 * Method returns all URLs if searchStringInName is null or empty
	 * 
	 * If searchStringInName is given, URLName is liked searched with given input.
	 * 
	 * returns null if no result
	 */
	public List<URL> getAllURLs(String searchStringInName, boolean noProtocolRecords)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			sql.append("select * from URLList_").append(getProjectId());
			
			if (noProtocolRecords)
			{
				sql.append(" where protocolRecord=false");
			}
			
			if (searchStringInName != null && searchStringInName.length() > 1)
			{
				if (!noProtocolRecords)
					sql.append(" where");
				else
					sql.append(" and");
				
				sql.append(" urlname like '%").append(searchStringInName).append("%'");
			}
			sql.append(" order by changeCode desc");
			
			SQLQuery query = session.createSQLQuery(sql.toString());

			addScalars(query);

			query.setResultTransformer(Transformers.aliasToBean(URL.class));
			@SuppressWarnings("unchecked")
			List<URL> urlList = (List<URL>) query.list();

			session.flush();

			if (urlList.isEmpty())
				return null;
			return urlList;
		}
		catch (Exception e)
		{
			logger.error("Exception in getAllURLsToCheck() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return null;
	}
	
	@Override
	public void updateURL(URL newURL, URL oldURL)
	{
		URL url = null;
		
		if (oldURL == null)
		{
			url = newURL;
		}
		else if (oldURL.isOverwriteFlag())
		{
			url = newURL;
			url.setOverwriteFlag(false);
			url.setChangeCode(0);
		}
		else
		{
			url = oldURL;
			url.setFoundTimestamp(newURL.getFoundTimestamp());
			url.setChangeCode(getChangeCode(newURL, oldURL));
			
			if (url.getChangeCode() != 0)
			{
				newURL.setProtocolRecord(true);
				insertNewURL(newURL);
			}
		}
		
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();
			SQLQuery query;

			sql.append("update URLList_").append(getProjectId()).append(" set ").append(updateRows).append(" where protocolRecord=false AND urlname ='").append(url.getURLName()).append("'");
			query = session.createSQLQuery(sql.toString());

			setQueryParams(0, query, url, false);
			query.executeUpdate();

			session.flush();
		}
		catch (Exception e)
		{
			logger.error("Problems during updateURL: " + url.getURLName());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}

	}
	
	/**
	 * Delete method deletes URL by name, also protocolRecords.
	 * @param urlname
	 */
	public void deleteURL(String urlname)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			
			StringBuffer sql = new StringBuffer();
			sql.append("delete from URLList_").append(getProjectId()).append(" where urlname = '").append(urlname).append("'");;

			SQLQuery query = session.createSQLQuery(sql.toString());
			
			query.executeUpdate();
			session.flush();
			
		}
		catch (Exception e)
		{
			logger.error("Exception in deleteURL() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}
	
	public void deleteRecords(boolean onlyProtocolRecords)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			
			StringBuffer sql = new StringBuffer();
			
			sql.append("delete from URLList_").append(getProjectId());
			
			if (onlyProtocolRecords)
				sql.append(" where protocolRecord=true");

			SQLQuery query = session.createSQLQuery(sql.toString());
			
			query.executeUpdate();
			session.flush();
			
		}
		catch (Exception e)
		{
			logger.error("Exception in deleteProtocolRecords() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}
	
	// TODO: remove creatListTable after migration from different places...
	public void createListURLTableForGivenProject()
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();

			String sql = "create table IF NOT EXISTS URLList_" + getProjectId() + tableRows;
			session.createSQLQuery(sql).executeUpdate();
			session.flush();
			
		}
		catch (Exception e)
		{
			logger.error("Exception in createListURLTableForGivenProject() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public void dropURLListTableForGivenProject()
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();

			String sql = "drop table if exists URLList_" + getProjectId();
			session.createSQLQuery(sql).executeUpdate();

			session.flush();
			session.clear();
		}
		catch (Exception e)
		{
			logger.error("Exception in dropURLListTableForGivenProject() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}
	
	public void emptyURLListTableForGivenProject()
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();

			String sql = "truncate URLList_" + getProjectId();
			session.createSQLQuery(sql).executeUpdate();

			session.flush();
			session.clear();
		}
		catch (Exception e)
		{
			logger.error("Exception in emptyURLListTableForGivenProject() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}
	
	private int getChangeCode(URL newURL, URL oldURL)
	{
		int changeCode = 0;
		
		if (!newURL.isBlockedByRobotsTxt() == oldURL.isBlockedByRobotsTxt())
			changeCode = changeCode | URLField.BLOCKEDBYROBOTSTXT.getVector();
		if (!newURL.getTitle().equals(oldURL.getTitle()))
			changeCode = changeCode | URLField.TITLE.getVector();
		if (!newURL.getMetaDescription().equals(oldURL.getMetaDescription()))
			changeCode = changeCode | URLField.METADESCRIPTION.getVector();
		if (newURL.getCanonicalTagHashcode() != null && newURL.getCanonicalTagHashcode() != null)
		{
			if (!newURL.getCanonicalTagHashcode().equals(oldURL.getCanonicalTagHashcode()))
				changeCode = changeCode | URLField.ONPAGETEXT.getVector();
		}
		else if ((newURL.getCanonicalTagHashcode() == null && oldURL.getCanonicalTagHashcode() != null) || (newURL.getCanonicalTagHashcode() != null && oldURL.getCanonicalTagHashcode() == null))
		{
			changeCode = changeCode | URLField.ONPAGETEXT.getVector();
		}

		if (newURL.getMetaRobotsIndex() != oldURL.getMetaRobotsIndex())
			changeCode = changeCode | URLField.INDEXNOINDEX.getVector();
		if (!newURL.getTopicKeywordOneTerm().equals(newURL.getTopicKeywordOneTerm()) || !newURL.getTopicKeywordTwoTerms().equals(newURL.getTopicKeywordTwoTerms()))
		{
			changeCode = changeCode | URLField.MAINKEYWORD.getVector();
		}
		if (newURL.getHttpStatusCode() != oldURL.getHttpStatusCode())
			changeCode = changeCode | URLField.HTTPSTATUSCODE.getVector();

		int upperPS = (int)(oldURL.getPageSize() * 1.1f);
		int lowerPS = (int)(oldURL.getPageSize() * 0.9f);
		
		if (newURL.getPageSize() > upperPS || newURL.getPageSize() < lowerPS)
			changeCode = changeCode & URLField.PAGESIZE.getVector();
		
		return changeCode;
	}
	
	@Override
	public void saveURL(URL url)
	{
		// do nothing
	}
	@Override
	public void addLinkToGraph(String source, String destination)
	{
		// do nothing
	}
	@Override
	public URL getURLFromInsertCache(String id)
	{
		return null;
	}
	@Override
	public URL getURLFromUpdateCache(String id)
	{
		return null;
	}

}
