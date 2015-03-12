package de.solidsearch.bot.dao;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
import org.hibernate.engine.spi.SessionImplementor;
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
import org.springframework.util.StringUtils;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

import de.solidsearch.bot.data.ProjectSummary;
import de.solidsearch.bot.data.URL;
import de.solidsearch.bot.data.enumerations.IssueType;
import de.solidsearch.bot.utils.BotConfig;

@Component("URLManager")
@Scope(value = "prototype")
public class URLManager implements Serializable
{
	private static final long serialVersionUID = 2074188517416059945L;

	@Autowired
	SessionFactory sessionFactory;

	private static final Logger logger = Logger.getLogger(URLManager.class.getName());

	protected final String updateRows = "alreadyCrawled=?,"
			+ "canonicalTag=?,"
			+ "canonicalTagHashcode=?,"
			+ "firstFoundAnchorTextToThisURL=?,"
			+ "contentHashcode=?,"
			+ "depthFromDomainRoot=?,"
			+ "externalLink=?,"
			+ "externalHostName=?,"
			+ "externalLinksOnThisPage=?,"
			+ "externalLinksDifferentDomainsOnThisPage=?,"
			+ "foundAtURL=?,"
			+ "foundTimestamp=?,"
			+ "h1=?,"
			+ "h2=?,"
			+ "h3=?,"
			+ "httpStatusCode=?,"
			+ "internalLinksOnThisPage=?,"
			+ "followLinksToThisPage=?,"
			+ "nofollowLinksToThisPage=?,"
			+ "metaDescription=?,"
			+ "metaRobotsFollow=?,"
			+ "metaRobotsIndex=?,"
			+ "pageSize=?,"
			+ "redirectedToURL=?,"
			+ "responseTime=?,"
			+ "timeout=?,"
			+ "title=?,"
			+ "duplicateContent=?,"
			+ "duplicateMetaDescription=?,"
			+ "duplicateTitle=?,"
			+ "duplicateH1=?,"
			+ "canonicalTagIssue=?,"
			+ "headlinesNotInRightOrder=?,"
			+ "relNofollow=?,"
			+ "qualityScore=?,"
			+ "facebookLikes=?,"
			+ "facebookShares=?,"
			+ "differentURLSameAnchor=?,"
			+ "googleAnalyticsCodeFound=?,"
			+ "newPrice=?,"
			+ "oldPrice=?,"
			+ "color=?,"
			+ "readingLevel=?,"
			+ "varietyTopicScore=?,"
			+ "onPageText=?,"
			+ "qwLocale=?,"
			+ "relevantImages=?,"
			+ "adScripts=?,"
			+ "normalizedText = (setweight(to_tsvector(cast('simple' AS regconfig),cast(coalesce(?,'') AS text)), 'A') || setweight(to_tsvector(cast('simple' AS regconfig),cast(coalesce(?,'') AS text)), 'B') || setweight(to_tsvector(cast('simple' AS regconfig),cast(coalesce(?,'') AS text)), 'C') || setweight(to_tsvector(cast('simple' AS regconfig),cast(coalesce(?,'') AS text)), 'D') || setweight(to_tsvector(cast('simple' AS regconfig),cast(coalesce(?,'') AS text)), 'D'))," 
			+ "pagerank=?,"
			+ "trailingSlashIssue=?," 
			+ "gzipIssue=?," 
			+ "externalLinkPower=?," 
			+ "spamScore=?," 
			+ "backgroundId=?," 
			+ "pagination=?," 
			+ "dcPrimary=?,"
			+ "keywordOrientationShortTerm=?,"
			+ "keywordOrientationTwoTerms=?,"
			+ "topicKeywordOneTerm=?,"
			+ "topicKeywordTwoTerms=?," 
			+ "topicKeywordThreeTerms=?,"
			+ "normalizedTopicKeywordOneTerm=?,"
			+ "normalizedTopicKeywordTwoTerms=?," 
			+ "normalizedTopicKeywordThreeTerms=?,"
			+ "topicKeywordOneTermWeight=?,"
			+ "topicKeywordTwoTermsWeight=?,"
			+ "topicKeywordThreeTermsWeight=?,"
			+ "changeCode=?,"
			+ "overwriteFlag=?,"
			+ "protocolRecord=?,"
			+ "blockedByRobotsTxt=?,"
			+ "relevantOnPageText=?";

	protected final String tableRows = " (id char(40) not null, " +
	"parentId char(40) not null, " +
	"partitionkey integer not null, " +
	"URLName varchar(4000) not null, " +
	"alreadyCrawled boolean not null, " +
	"canonicalTag varchar(4000) not null, " +
	"canonicalTagHashcode char(40), " +
	"firstFoundAnchorTextToThisURL varchar(300), " +
	"contentHashcode char(40), " +
	"depthFromDomainRoot integer not null, " +
	"externalLink boolean not null, " +
	"externalHostName text, "+
	"externalLinksOnThisPage integer not null, " +
	"externalLinksDifferentDomainsOnThisPage integer not null, " +
	"foundAtURL varchar(4000) not null, " +
	"foundTimestamp bigint, " + "h1 varchar(500) not null, " + 
	"h2 text not null, " +
	"h3 text not null, " +
	"httpStatusCode integer not null, " +
	"internalLinksOnThisPage integer not null, " +
	"followLinksToThisPage bigint not null, " +
	"nofollowLinksToThisPage bigint not null, " +
	"metaDescription text not null, " +
	"metaRobotsFollow boolean, " +
	"metaRobotsIndex boolean, " +
	"pageSize bigint not null, " +
	"redirectedToURL varchar(4000), " +
	"responseTime integer not null, " +
	"timeout boolean not null, " +
	"title text not null, " +
	"duplicateContent boolean not null, " +
	"duplicateMetaDescription boolean not null, " +
	"duplicateTitle boolean not null, " +
	"duplicateH1 boolean not null, " +
	"canonicalTagIssue boolean not null, " +
	"headlinesNotInRightOrder boolean not null, " +
	"relNofollow boolean not null, " +
	"qualityScore smallint not null, " +
	"facebookLikes integer not null, " +
	"facebookShares integer not null, " +
	"differentURLSameAnchor boolean not null, " +
	"googleAnalyticsCodeFound boolean not null, " +
	"newPrice integer not null," +
	"oldPrice integer not null, " + 
	"color varchar(150) not null, " +
	"readingLevel smallint not null, " +
	"varietyTopicScore smallint not null, " +
	"onPageText text," +
	"qwLocale smallint not null, " +
	"relevantImages boolean not null," +
	"adScripts smallint not null," +
	"normalizedText tsvector," +
	"pagerank double precision not null," +
	"trailingSlashIssue boolean not null," +
	"gzipIssue boolean not null," +
	"externalLinkPower smallint not null," +
	"spamScore smallint not null," + 
	"backgroundId smallint not null," + 
	"pagination boolean," + 
	"dcprimary boolean," +
	"keywordOrientationShortTerm boolean," +
	"keywordOrientationTwoTerms boolean," +
	"topicKeywordOneTerm text not null," +
	"topicKeywordTwoTerms text not null," +
	"topicKeywordThreeTerms text not null," +
	"normalizedTopicKeywordOneTerm text not null," +
	"normalizedTopicKeywordTwoTerms text not null," +
	"normalizedTopicKeywordThreeTerms text not null," +
	"topicKeywordOneTermWeight smallint not null," +
	"topicKeywordTwoTermsWeight smallint not null," +
	"topicKeywordThreeTermsWeight smallint not null," + 
	"changeCode integer not null," +
	"overwriteFlag boolean not null," +
	"protocolRecord boolean not null," +
	"blockedByRobotsTxt boolean not null, " + 
	"relevantOnPageText text)";

	protected final String insertRows = " (id," + 
	"parentId, " + 
	"partitionkey," + 
	"URLName," + 
	"alreadyCrawled," + 
	"canonicalTag," + 
	"canonicalTagHashcode," + 
	"firstFoundAnchorTextToThisURL," + 
	"contentHashcode," + 
	"depthFromDomainRoot," + 
	"externalLink," + "externalHostName," +
	"externalLinksOnThisPage," +
	"externalLinksDifferentDomainsOnThisPage," + 
	"foundAtURL," + 
	"foundTimestamp," + 
	"h1," + 
	"h2," +
	"h3," +
	"httpStatusCode," +
	"internalLinksOnThisPage,"+
	"followLinksToThisPage," +
	"nofollowLinksToThisPage," +
	"metaDescription," +
	"metaRobotsFollow," +
	"metaRobotsIndex," +
	"pageSize," +
	"redirectedToURL," +
	"responseTime," +
	"timeout," +
	"title," +
	"duplicateContent," +
	"duplicateMetaDescription," +
	"duplicateTitle," +
	"duplicateH1," +
	"canonicalTagIssue," +
	"headlinesNotInRightOrder," +
	"relNofollow," +
	"qualityScore," +
	"facebookLikes," +
	"facebookShares," +
	"differentURLSameAnchor," +
	"googleAnalyticsCodeFound," +
	"newPrice," +
	"oldPrice," +
	"color," +
	"readingLevel, " +
	"varietyTopicScore, " +
	"onPageText, " +
	"qwLocale," +
	"relevantImages," +
	"adScripts," +
	"normalizedText," +
	"pagerank," +
	"trailingSlashIssue," +
	"gzipIssue," +
	"externalLinkPower," +
	"spamScore," + 
	"backgroundId, " +
	"pagination, " +
	"dcprimary," +
	"keywordOrientationShortTerm," +
	"keywordOrientationTwoTerms," +
	"topicKeywordOneTerm," +
	"topicKeywordTwoTerms," +
	"topicKeywordThreeTerms," +
	"normalizedTopicKeywordOneTerm," +
	"normalizedTopicKeywordTwoTerms," +
	"normalizedTopicKeywordThreeTerms," +
	"topicKeywordOneTermWeight," +
	"topicKeywordTwoTermsWeight," +
	"topicKeywordThreeTermsWeight," +
	"changeCode," +
	"overwriteFlag," +
	"protocolRecord," +
	"blockedByRobotsTxt," +
	"relevantOnPageText)";

	private String sqlParameter = getSQLParameterList();

	private long projectId;

	private final int PARTITIONS = 16;

	private final int PARTITION_SIZE = -2147483648;
	private final int PARTITION_STEPS = 268435456;

	private final int CACHESIZE = 3000;
	private final int INTERNALLINKSCACHESIZE = 60000;

	private long updateCacheAccessCount = 0;
	private long updateCacheHitCount = 0;

	private ArrayList<Map.Entry<String, String>> linkGraphCache = new ArrayList<Map.Entry<String, String>>();

	private ConcurrentHashMap<String, URL> insertCache = new ConcurrentHashMap<String, URL>();
	private LRUCache updateCache = new LRUCache(INTERNALLINKSCACHESIZE);

	public AtomicInteger allInserts = new AtomicInteger();

	private Locale defaultLocale;

	public List<URL> getInternalAndNotAlreadyCrawledURLs(int maxResults, boolean ignoreNofollow)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();
			String relNofollow = "";
			if (!ignoreNofollow)
				relNofollow = " AND relNofollow=false";

			sql.append("select * from URL_tmp_parent_").append(getProjectId()).append(" where alreadyCrawled=false AND externalLink=false").append(relNofollow).append(" order by depthFromDomainRoot asc, foundTimestamp asc, id limit ?");
			SQLQuery query = session.createSQLQuery(sql.toString());
			addScalars(query);
			query.setParameter(0, maxResults);
			query.setResultTransformer(Transformers.aliasToBean(URL.class));
			@SuppressWarnings("unchecked")
			List<URL> urlList = (List<URL>) query.list();
			session.flush();

			return urlList;
		}
		catch (Exception e)
		{
			logger.error("Exception in getInternalAndNotAlreadyCrawledURLs() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Container getComparedURLResultsForUI(String firstProjectDatabaseId, String secondProjectDatabaseId, String firstDate, String secondDate, IssueType filterCode, String orderBySql, String searchString, int maxResults, long offset)
	{
		Connection conn = null;
		JdbcConnectionAccess jdbc = null;

		try
		{
			String whereFilter = retrieveWhereFilter(filterCode, "", false, 0, 0);
			String where = " ";

			if (searchString.length() > 0 || !filterCode.equals(IssueType.All))
			{
				where = " where ";
				searchString = sqlSearchStringHelper(searchString, whereFilter, filterCode);
			}

			StringBuffer sql = new StringBuffer();
			String offsetFilter = "limit " + maxResults + " offset " + offset;
			sql.append("select a.urlname as ").append("\"").append(firstDate).append("\"").append(",b.urlname as ").append("\"").append(secondDate).append("\"").append(" from (select a.urlname from URL_").append(firstProjectDatabaseId).append(" a ");
			sql.append(where).append(searchString).append(whereFilter).append(") as a FULL OUTER JOIN (select b.urlname from URL_").append(secondProjectDatabaseId).append(" b ");
			sql.append(where).append(searchString).append(whereFilter).append(") as b ON (a.urlname = b.urlname) ").append(orderBySql).append(" ").append(offsetFilter);

			SessionImplementor sfi = (SessionImplementor) sessionFactory.openSession();
			jdbc = sfi.getJdbcConnectionAccess();
			conn = jdbc.obtainConnection();

			PreparedStatement st = conn.prepareStatement(sql.toString());
			ResultSet rs = st.executeQuery();

			ResultSetMetaData md = rs.getMetaData();

			Container c = new IndexedContainer();
			for (int i = 0; i < md.getColumnCount(); ++i)
			{
				String label = md.getColumnLabel(i + 1);
				if (firstDate.equalsIgnoreCase(label) || secondDate.equalsIgnoreCase(label))
				{
					Class<?> clazz = getClassForSqlType(md.getColumnClassName(i + 1));
					c.addContainerProperty(label, clazz, null);
				}
			}

			int i = 0;
			while (rs.next())
			{
				Item item = c.addItem(i++);
				for (Object propertyId : c.getContainerPropertyIds())
				{
					item.getItemProperty(propertyId).setValue(rs.getObject(propertyId.toString()));
				}
			}
			return c;
		}
		catch (SQLException e)
		{
			logger.error("URLManager: SQL Exception in getComparedURLResultsForUI() " + e.getMessage());
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
					logger.error("URLManager: unable to release connection in getURLsForUI() " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public long getTotalCountOfCrawledInternalURLs(String projectDatabaseId, IssueType filterCode, String searchString, int minKeywordWeight)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			String whereFilter = retrieveWhereFilter(filterCode, null, false, 0, 0);
			String where = " ";

			if (!IssueType.KEYWORDS.equals(filterCode))
			{
				if (searchString.length() > 0 || !filterCode.equals(IssueType.All))
				{
					where = " where ";
					searchString = sqlSearchStringHelper(searchString, whereFilter, filterCode);
				}
				
				sql.append("select count(*) from URL_").append(projectDatabaseId).append(where).append(searchString).append(whereFilter);
			}
			else
			{
				if (searchString.length() < 1)
					return 0;

				sql.append("SELECT count(urlname) ");
				sql.append("FROM URL_").append(projectDatabaseId).append(", plainto_tsquery('simple','").append(searchString).append("') query ");
				sql.append("WHERE query @@ (to_tsvector('simple', coalesce(normalizedTopicKeywordOneTerm,'')) || to_tsvector('simple', coalesce(normalizedTopicKeywordTwoTerms,''))) AND externalLink=false AND httpStatusCode = 200 ");
			}
			
			SQLQuery query = session.createSQLQuery(sql.toString());

			Object value = query.uniqueResult();

			if (value == null)
			{
				return 0;
			}
			session.flush();

			return ((BigInteger) value).longValue();
		}
		catch (Exception e)
		{
			logger.error("Exception in getTotalCountOfCrawledInternalURLs() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public Container getURLsForUI(String projectDatabaseId, IssueType filterCode, String orderBySql, String searchString, int minKeywordWeight, int maxResults, long offset)
	{
		Connection conn = null;
		JdbcConnectionAccess jdbc = null;

		try
		{
			List<String> columns = filterCode.getSelectedColumnsSQL(false, true, getDefaultLocale());

			StringBuffer sql = new StringBuffer();
			if (!filterCode.equals(IssueType.KEYWORDS))
			{
				String whereFilter = retrieveWhereFilter(filterCode, orderBySql, true, offset, maxResults);
				String where = " ";

				if (searchString.length() > 0 || !filterCode.equals(IssueType.All))
				{
					where = " where ";
					searchString = sqlSearchStringHelper(searchString, whereFilter, filterCode);
				}

				sql.append("select ").append(getDBColsForIssue(filterCode, false, getDefaultLocale())).append(" from URL_").append(projectDatabaseId).append(where).append(searchString).append(whereFilter);
			}
			else
			{
				if (searchString.length() < 1)
					return null;

				sql.append("SELECT urlname,topicKeywordOneTerm,topicKeywordTwoTerms,topicKeywordThreeTerms ");
				sql.append("FROM  URL_").append(projectDatabaseId).append(", plainto_tsquery('simple','").append(searchString).append("') query ");
				sql.append("WHERE query @@ (to_tsvector('simple', coalesce(normalizedTopicKeywordOneTerm,'')) || to_tsvector('simple', coalesce(normalizedTopicKeywordTwoTerms,''))) AND externalLink=false AND httpStatusCode = 200 ");
				sql.append(orderBySql);
				sql.append(" limit ").append(maxResults).append(" offset ").append(offset);
			}

			SessionImplementor sfi = (SessionImplementor) sessionFactory.openSession();
			jdbc = sfi.getJdbcConnectionAccess();
			conn = jdbc.obtainConnection();

			PreparedStatement st = conn.prepareStatement(sql.toString());

			ResultSet rs = st.executeQuery();

			ResultSetMetaData md = rs.getMetaData();

			Container c = new IndexedContainer();
			for (int i = 0; i < md.getColumnCount(); ++i)
			{
				String label = md.getColumnLabel(i + 1);
				if (columns.contains(label))
				{
					Class<?> clazz = getClassForSqlType(md.getColumnClassName(i + 1));
					c.addContainerProperty(label, clazz, null);
				}
			}

			int i = 0;
			while (rs.next())
			{
				Item item = c.addItem(i++);
				for (Object propertyId : c.getContainerPropertyIds())
				{
					item.getItemProperty(propertyId).setValue(rs.getObject(propertyId.toString()));
				}
			}

			return c;
		}
		catch (SQLException e)
		{
			logger.error("URLManager: SQL Exception in getURLsForUI() " + e.getMessage());
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
					logger.error("URLManager: unable to release connection in getURLsForUI() " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	protected Class<?> getClassForSqlType(String name)
	{
		try
		{
			return Class.forName(name);
		}
		catch (Exception ex)
		{
			logger.warn("URLManager: unable to get class for name " + name);
			return null;
		}
	}

	/**
	 * Create a csv file with the requested URLs. s
	 * 
	 * @return The name of the temporary .csv file.
	 */
	public String getURLsAsCSV(String projectDatabaseId, IssueType filterCode, String orderBySql, String searchString, int minKeywordWeight, int maxResults, long offset, String reportAbsDirName)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			// Create a unique name for the temporary .csv file using the current system timestamp and the template id...
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SS");
			StringBuffer reportFileName = new StringBuffer();
			reportFileName.append(filterCode.getLocalizedUIText());
			reportFileName.append("_");
			reportFileName.append(sdf.format(Calendar.getInstance().getTime()));
			reportFileName.append(".csv");

			// Finalize the query...
			StringBuffer queryString = new StringBuffer();
			queryString.append("COPY");
			queryString.append("(");

			// Select query which retrieves the URLs for the .csv export...

			String whereFilter = retrieveWhereFilter(filterCode, orderBySql, false, offset, maxResults);
			String where = " ";

			if (searchString.length() > 0 || !filterCode.equals(IssueType.All))
			{
				where = " where ";

				if (!filterCode.equals(IssueType.All) && searchString.length() > 0)
				{
					searchString = searchString + " AND ";
				}
			}

			if (!filterCode.equals(IssueType.KEYWORDS))
			{
				queryString.append("select ").append(getDBColsForIssue(filterCode, false, getDefaultLocale())).append(" from URL_").append(projectDatabaseId).append(where).append(searchString).append(whereFilter);
			}
			else
			{
				if (searchString.length() < 1)
					return null;

				queryString.append("SELECT urlname,topicKeywordOneTerm,topicKeywordTwoTerms,topicKeywordThreeTerms ");
				queryString.append("FROM URL_").append(projectDatabaseId).append(", plainto_tsquery('simple','").append(searchString).append("') query ");
				queryString.append("WHERE query @@ (to_tsvector('simple', coalesce(normalizedTopicKeywordOneTerm,'')) || to_tsvector('simple', coalesce(normalizedTopicKeywordTwoTerms,''))) AND externalLink=false AND httpStatusCode = 200 ");
				queryString.append(orderBySql);

			}

			queryString.append(") TO '");

			queryString.append(reportAbsDirName).append(reportFileName.toString());
			// Set the directory...
			queryString.append("' WITH DELIMITER ';' CSV HEADER ENCODING 'UTF8';");
			// System.out.println(queryString.toString());
			// Execute the query...
			SQLQuery query = session.createSQLQuery(queryString.toString());
			query.executeUpdate();

			session.flush();

			return reportFileName.toString();
		}
		catch (Exception e)
		{
			logger.error("Exception in getURLsAsCSV() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return "";
	}// end method...

	/**
	 * Create a csv file with the requested URLs. s
	 * 
	 * @return The name of the temporary .csv file.
	 */
	public String getComparedURLsAsCSV(String firstProjectDatabaseId, String secondProjectDatabaseId, String firstDate, String secondDate, IssueType filterCode, String orderBySql, String searchString, String reportAbsDirName)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			// Create a unique name for the temporary .csv file using the current system timestamp and the template id...
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SS");
			StringBuffer reportFileName = new StringBuffer();
			reportFileName.append(filterCode.getLocalizedUIText());
			reportFileName.append("_");
			reportFileName.append(firstDate);
			reportFileName.append("_vs_");
			reportFileName.append(secondDate);
			reportFileName.append("_");
			reportFileName.append(sdf.format(Calendar.getInstance().getTime()));
			reportFileName.append(".csv");

			// Finalize the query...
			StringBuffer queryString = new StringBuffer();
			queryString.append("COPY");
			queryString.append("(");

			// Select query which retrieves the URLs for the .csv export...

			String whereFilter = retrieveWhereFilter(filterCode, "", false, 0, 0);
			String where = " ";

			if (searchString.length() > 0 || !filterCode.equals(IssueType.All))
			{
				where = " where ";

				if (!filterCode.equals(IssueType.All) && searchString.length() > 0)
				{
					searchString = searchString + " AND ";
				}
			}

			queryString.append("select a.urlname as ").append("\"").append(firstDate).append("\"").append(",b.urlname as ").append("\"").append(secondDate).append("\"").append(" from (select a.urlname from URL_").append(firstProjectDatabaseId)
					.append(" a ").append(where).append(searchString).append(whereFilter).append(") as a FULL OUTER JOIN (select b.urlname from URL_").append(secondProjectDatabaseId).append(" b ").append(where).append(searchString)
					.append(whereFilter).append(") as b ON (a.urlname = b.urlname) ").append(orderBySql);

			queryString.append(") TO '");

			queryString.append(reportAbsDirName).append(reportFileName.toString());
			// Set the directory...
			queryString.append("' WITH DELIMITER ';' CSV HEADER ENCODING 'UTF8';");

			// Execute the query...
			SQLQuery query = session.createSQLQuery(queryString.toString());
			query.executeUpdate();

			session.flush();

			return reportFileName.toString();
		}
		catch (Exception e)
		{
			logger.error("Exception in getComparedURLsAsCSV() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return "";
	}// end method...

	public URL getURLFromInsertCache(String id)
	{
		if (insertCache.containsKey(id))
			return insertCache.get(id);
		else
			return null;
	}

	public URL getURLFromUpdateCache(String id)
	{
		if (updateCache.containsKey(id))
			return updateCache.get(id);
		else
			return null;
	}

	public void saveURL(URL url)
	{
		if (insertCache.containsKey(url.getId()))
		{
			insertCache.replace(url.getId(), url);
		}
		else
		{
			insertCache.putIfAbsent(url.getId(), url);
			allInserts.incrementAndGet();
			if (insertCache.size() >= CACHESIZE)
			{
				emptyInsertCache(true);
			}
		}
	}

	public AtomicInteger updater = new AtomicInteger();

	public void incrementLinkCountsAndCheckRelNofollowchange(URL url)
	{
		if (insertCache.containsKey(url.getId()))
		{
			URL insertCachedURL = insertCache.get(url.getId());

			if (url.isRelNofollow())
				insertCachedURL.setNofollowLinksToThisPage(insertCachedURL.getNofollowLinksToThisPage() + 1);
			else
				insertCachedURL.setFollowLinksToThisPage(insertCachedURL.getFollowLinksToThisPage() + 1);

			if (insertCachedURL.isRelNofollow() != url.isRelNofollow())
			{
				insertCachedURL.setRelNofollow(url.isRelNofollow());
				logger.info("A REL-NOFOLLLOW LINK changed to REL-FOLLOW@TODO: bring this new KPI to dashboard");
			}

			insertCache.replace(url.getId(), insertCachedURL);
			return;
		}

		updateCacheAccessCount++;

		if (updateCache.containsKey(url.getId()))
		{
			updateCacheHitCount++;
			URL updateCachedURL = updateCache.get(url.getId());
			if (url.isRelNofollow())
				updateCachedURL.setNofollowLinksToThisPage(updateCachedURL.getNofollowLinksToThisPage() + 1);
			else
				updateCachedURL.setFollowLinksToThisPage(updateCachedURL.getFollowLinksToThisPage() + 1);

			if (updateCachedURL.isRelNofollow() != url.isRelNofollow())
			{
				updateCachedURL.setRelNofollow(url.isRelNofollow());
				logger.info("A REL-NOFOLLLOW LINK changed to REL-FOLLOW@TODO: bring this new KPI to dashboard");
			}

			updateCache.put(url.getId(), updateCachedURL);
		}
		else
		{
			updateCache.put(url.getId(), url);
		}
	}

	public void emptyUpdateCache()
	{
		if (updateCache.size() == 0)
			return;
		long before = System.currentTimeMillis();
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			SQLQuery query;

			for (Map.Entry<String, URL> entry : updateCache.entrySet())
			{
				updater.incrementAndGet();
				query = session.createSQLQuery(getUpdateInternalLinksSQL(entry.getValue().getFollowLinksToThisPage(), entry.getValue().getNofollowLinksToThisPage(), entry.getKey(), entry.getValue().getPartitionkey()));
				query.executeUpdate();

				session.flush();
				session.clear();
			}
			long after = System.currentTimeMillis();
			int hitRatio = 0;
			if (updateCacheAccessCount != 0)
				hitRatio = (int) (100f / updateCacheAccessCount * updateCacheHitCount);
			logger.info("emptyUpdateCache(): projectId: " + projectId + " duration= " + (after - before) + " ms, hits:" + updateCacheHitCount + ", accesses: " + updateCacheAccessCount + " hitratio: " + hitRatio + "%");
			updateCache.clear();
		}
		catch (Exception e)
		{
			logger.error("Exception in emptyUpdateCache() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public void updateURL(URL newURL, URL oldURL)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();
			SQLQuery query;

			sql.append("update URL_tmp_parent_").append(getProjectId()).append(" set ").append(updateRows).append(" where id ='").append(newURL.getId()).append("' AND partitionkey =").append(newURL.getPartitionkey());
			query = session.createSQLQuery(sql.toString());

			setQueryParams(0, query, newURL, false);
			query.executeUpdate();

			session.flush();
		}
		catch (Exception e)
		{
			logger.error("Problems during updateURL: " + newURL.getURLName());
			e.printStackTrace();
			/*
			 * TODO: hardening
			 * 
			 * 1. print all URL properties 2. update URL with alreadyCrawled = true && error
			 */
		}
		finally
		{
			if (session != null)
				session.close();
		}

	}

	public URL getURLbyId(String id, int urlHashCode)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			sql.append("select * from URL_tmp_parent_").append(getProjectId()).append(" where id ='").append(id).append("'");

			if (urlHashCode != -1)
			{
				sql.append(" AND partitionkey =").append(urlHashCode);
			}

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

	public URL getURLbyURLName(String urlName, String projectDatabaseId)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			sql.append("select * from URL_").append(projectDatabaseId).append(" where urlname ='").append(urlName).append("'");

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
			logger.error("Exception in getURLbyURLName() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return null;
	}

	public long getTotalCountOfComparedURLResults(String firstProjectDatabaseId, String secondProjectDatabaseId, IssueType filterCode)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			String whereFilter = retrieveWhereFilter(filterCode, "", false, 0, 0);
			String where = " ";

			if (whereFilter.length() > 0)
			{
				where = " where ";
			}

			sql.append("select count(a.urlname) from (select a.urlname from URL_").append(firstProjectDatabaseId).append(" a ").append(where).append(whereFilter).append(") as a FULL OUTER JOIN (select b.urlname from URL_")
					.append(secondProjectDatabaseId).append(" b ").append(where).append(whereFilter).append(") as b ON (a.urlname = b.urlname)");

			SQLQuery query = session.createSQLQuery(sql.toString());

			Object value = query.uniqueResult();

			session.flush();

			if (value == null)
			{
				return 0;
			}

			return ((BigInteger) value).longValue();
		}
		catch (Exception e)
		{
			logger.error("Exception in getTotalCountOfComparedURLResults() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return 0;
	}

	public ProjectSummary getAggregationsForProjectSummary(String projectDatabaseId)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();
			sql.append("select avg(responseTime) as avgResponseTimeMills,");
			sql.append(" avg(pageSize) as avgPageSize from URL_").append(projectDatabaseId);
			sql.append(" where externalLink=false AND (httpStatusCode = 200)");
			SQLQuery query = session.createSQLQuery(sql.toString());

			query.addScalar("avgResponseTimeMills", IntegerType.INSTANCE);
			query.addScalar("avgPageSize", LongType.INSTANCE);

			ProjectSummary avgValues = null;
			try
			{
				query.setResultTransformer(Transformers.aliasToBean(ProjectSummary.class));
				avgValues = (ProjectSummary) query.uniqueResult();

				session.flush();
			}
			catch (org.hibernate.PropertyAccessException e)
			{
				logger.info("Error calculating average values... " + e.getMessage());
			}

			return avgValues;
		}
		catch (Exception e)
		{
			logger.error("Exception in getAggregationsForProjectSummary() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return null;
	}

	private String retrieveWhereFilter(IssueType filterCode, String orderBySql, boolean offset, long offsetValue, long limitValue)
	{
		String externalInternalSQL = "";

		String whereFilter = "";
		String offsetFilter = "limit " + limitValue + " offset " + offsetValue;
		String orderByfilter = "order by foundTimestamp asc ";

		if (IssueType.CLIENTERRORS.equals(filterCode))
		{
			whereFilter = " httpStatusCode >= 400 and httpStatusCode < 500 ";
			orderByfilter = "order by httpStatusCode, foundTimestamp asc ";
		}
		else if (IssueType.REDIRECTIONS.equals(filterCode))
		{
			whereFilter = " httpStatusCode >= 300 and httpStatusCode < 400 ";
			orderByfilter = "order by httpStatusCode, foundTimestamp asc ";
		}
		else if (IssueType.SERVERERRORS.equals(filterCode))
		{
			whereFilter = " httpStatusCode >= 500 ";
			orderByfilter = "order by httpStatusCode, foundTimestamp asc ";
		}
		else if (IssueType.ROBOTSINDEX.equals(filterCode))
		{
			whereFilter = " metaRobotsIndex = true ";
		}
		else if (IssueType.ROBOTSNOINDEX.equals(filterCode))
		{
			whereFilter = " metaRobotsIndex =false ";
		}
		else if (IssueType.ROBOTSFOLLOW.equals(filterCode))
		{
			whereFilter = " metaRobotsFollow = true ";
		}
		else if (IssueType.ROBOTSNOFOLLOW.equals(filterCode))
		{
			whereFilter = " metaRobotsFollow = false ";
		}
		else if (IssueType.TIMEOUTS.equals(filterCode))
		{
			whereFilter = " timeout = true ";
		}
		else if (IssueType.LOWCONTENT.equals(filterCode))
		{
			whereFilter = " contentHashcode is null and metaRobotsIndex = true and (httpStatusCode = 200) ";
		}
		else if (IssueType.DUPLICATECONTENT.equals(filterCode))
		{
			whereFilter = " duplicateContent = true ";
			orderByfilter = "order by contentHashcode ";
		}
		else if (IssueType.LONGMETADESCRIPTION.equals(filterCode))
		{
			whereFilter = " char_length(metaDescription) > 160 and metaRobotsIndex = true and (httpStatusCode = 200) ";
		}
		else if (IssueType.MISSINGMETADESCRIPTION.equals(filterCode))
		{
			whereFilter = " char_length(metaDescription) < 3 and metaRobotsIndex = true and (httpStatusCode = 200) ";
		}
		else if (IssueType.LONGTITLE.equals(filterCode))
		{
			whereFilter = " char_length(title) > 57 and metaRobotsIndex = true and (httpStatusCode = 200) ";
		}
		else if (IssueType.MISSINGTITLE.equals(filterCode))
		{
			whereFilter = " (array_length(string_to_array(title, ' '),1) <= 1 or char_length(title) <=0) and metaRobotsIndex = true and (httpStatusCode = 200) ";
		}
		else if (IssueType.CANONICALTOSOURCE.equals(filterCode))
		{
			whereFilter = " URLName = canonicalTag ";
		}
		else if (IssueType.MISSINGH1.equals(filterCode))
		{
			whereFilter = " char_length(h1) <= 2 and metaRobotsIndex = true and (httpStatusCode = 200) ";
		}
		else if (IssueType.CANONICALISSUE.equals(filterCode))
		{
			whereFilter = " canonicalTagIssue = true ";
		}
		else if (IssueType.HEADLINESORDER.equals(filterCode))
		{
			whereFilter = " headlinesNotInRightOrder = true and metaRobotsIndex = true and (httpStatusCode = 200) ";
		}
		else if (IssueType.DUPLICATETITLE.equals(filterCode))
		{
			whereFilter = " duplicateTitle = true ";
			orderByfilter = "order by title ";
		}
		else if (IssueType.DUPLICATEMETA.equals(filterCode))
		{
			whereFilter = " duplicateMetaDescription = true ";
			orderByfilter = "order by metaDescription ";
		}
		else if (IssueType.DUPLICATEH1.equals(filterCode))
		{
			whereFilter = " duplicateH1 = true ";
			orderByfilter = "order by h1 ";
		}
		else if (IssueType.PAGESIZE.equals(filterCode))
		{
			orderByfilter = "order by pageSize desc ";
		}
		else if (IssueType.EXTERNALLINKS.equals(filterCode))
		{
			externalInternalSQL = " externalLink=true ";
		}
		else if (IssueType.INTERNALTARGETS.equals(filterCode))
		{
			orderByfilter = "order by followLinksToThisPage desc ";
		}
		else if (IssueType.QUALITYSCORE.equals(filterCode))
		{
			orderByfilter = "order by qualityScore desc ";
		}
		else if (IssueType.DIFFERENTURLSAMEANCHOR.equals(filterCode))
		{
			whereFilter = " differentURLSameAnchor = true ";
			orderByfilter = "order by firstFoundAnchorTextToThisURL desc ";
		}
		else if (IssueType.MISSINGGOOGLEANALYTICSCODE.equals(filterCode))
		{
			whereFilter = " googleAnalyticsCodeFound = false AND externalLink=false AND httpStatusCode = 200 ";
		}
		else if (IssueType.KEYWORDS.equals(filterCode))
		{
			whereFilter = "";
			externalInternalSQL = " externalLink=false ";
			orderByfilter = " order by urlname desc ";
		}
		else if (IssueType.TRAILINGSLASHISSUES.equals(filterCode))
		{
			whereFilter = " trailingSlashIssue = true ";
			orderByfilter = "order by urlname desc ";
		}
		else if (IssueType.GZIPISSUES.equals(filterCode))
		{
			whereFilter = " gzipIssue = true ";
			orderByfilter = "order by urlname desc ";
		}
		else if (IssueType.KEYWORDORIENTATIONSHORTTERM.equals(filterCode))
		{
			whereFilter = " keywordOrientationShortTerm = true ";
			orderByfilter = "order by normalizedTopicKeywordOneTerm desc ";
		}
		else if (IssueType.KEYWORDORIENTATIONTWOTERMS.equals(filterCode))
		{
			whereFilter = " keywordOrientationTwoTerms = true ";
			orderByfilter = "order by normalizedTopicKeywordTwoTerms desc ";
		}
		if (orderBySql != "")
		{
			orderByfilter = orderBySql;
		}

		whereFilter = externalInternalSQL + whereFilter;

		if (offset)
			whereFilter = whereFilter + orderByfilter + offsetFilter;

		return whereFilter;
	}

	public void emptyInsertCache(boolean innern)
	{
		if (insertCache.size() == 0)
			return;

		Session session = null;

		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();
			SQLQuery query;

			int cachePosition = 0;
			sql.setLength(0);

			int lowerEnd = PARTITION_SIZE;
			int steps = PARTITION_STEPS;

			HashMap<String, URL> tmpMap = new HashMap<String, URL>();

			for (int y = 0; y < PARTITIONS; y++)
			{
				boolean lastPartition = false;
				if (y == (PARTITIONS - 1))
				{
					steps--;
					lastPartition = true;
				}
				for (Map.Entry<String, URL> entry : insertCache.entrySet())
				{
					if (lastPartition)
					{
						if ((entry.getValue().getPartitionkey() >= lowerEnd) && (entry.getValue().getPartitionkey() <= (lowerEnd + steps)))
						{
							tmpMap.put(entry.getKey(), entry.getValue());
						}
					}
					else
					{
						if ((entry.getValue().getPartitionkey() >= lowerEnd) && (entry.getValue().getPartitionkey() < (lowerEnd + steps)))
						{
							tmpMap.put(entry.getKey(), entry.getValue());
						}
					}
				}
				lowerEnd = lowerEnd + steps;

				if (tmpMap.isEmpty())
					continue;

				sql.append("insert into URL_tmp_child_").append(getProjectId()).append("_").append(y).append(insertRows).append(" values ");

				// prepare sql...
				for (cachePosition = 0; cachePosition < tmpMap.size(); cachePosition++)
				{
					sql.append(sqlParameter);
					if (cachePosition < (tmpMap.size() - 1))
						sql.append(",");
				}

				query = session.createSQLQuery(sql.toString());
				
				int i = 0;
				for (Map.Entry<String, URL> entry : tmpMap.entrySet())
				{
					i = setQueryParams(i, query, entry.getValue(), true);
				}

				try
				{
					query.executeUpdate();

					session.flush();
					session.clear();
				}
				catch (Exception ce)
				{
					logger.error("SINGLE INSERT MODE: BULK insert failed! Bulk size was " + tmpMap.size() + ". : " + ce.getMessage());

					for (Map.Entry<String, URL> entry : tmpMap.entrySet())
					{
						logger.warn("SINGLE MODE: This URL could have invalid chars: " + entry.getValue().getURLName());
					}

					// try safety close...
					if (session != null)
					{
						try
						{
							session.close();
						}
						catch (Exception ie)
						{
							logger.error("SINGLE INSERT MODE: Could not close hibernate-session, maybe there was a problem of the BULK length. Check also db-server logs: " + ie.getMessage());
						}
					}

					// get new session for safety reasons...
					session = sessionFactory.openSession();

					for (Map.Entry<String, URL> entry : tmpMap.entrySet())
					{
						sql.setLength(0);

						sql.append("insert into URL_tmp_child_").append(getProjectId()).append("_").append(y).append(insertRows).append(" values ").append(sqlParameter);

						query = session.createSQLQuery(sql.toString());
						setQueryParams(0, query, entry.getValue(), true);

						try
						{
							query.executeUpdate();

							session.flush();
							session.clear();
						}
						catch (Exception ce2)
						{
							logger.error("SINGLE INSERT MODE: Following URL tried to insert:" + entry.getValue().getURLName() + " / " + entry.getValue().getId() + " / " + entry.getValue().getPartitionkey() + " / foundAt: "
									+ entry.getValue().getFoundAtURL());

							URL duplicateURL = getURLbyId(entry.getValue().getId(), -1);
							if (duplicateURL != null)
								logger.error("SINGLE INSERT MODE: Following URL currently in database:" + duplicateURL.getURLName() + " / " + duplicateURL.getId() + " / " + duplicateURL.getPartitionkey());
							else
								logger.error("SINGLE INSERT MODE: Strange! Could not found duplicate in DB... maybe there is a problem with partitions...");
						}
					}

				}

				sql.setLength(0);
				tmpMap.clear();
			}
		}
		catch (Exception e)
		{
			logger.error("Exception in emptyInsertCache() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}

		insertCache.clear();
	}

	public void truncateURLsbyProjectId(String projectDatabaseId)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			String sql = "truncate URL_" + projectDatabaseId;
			session.createSQLQuery(sql).executeUpdate();

			session.flush();
		}
		catch (Exception e)
		{
			logger.error("Exception in truncateURLsbyProjectId() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public void createNewURLTableForProject(String[] projectDatabaseIds)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();

			for (int i = 0; i < projectDatabaseIds.length; i++)
			{
				// postgres
				String sql = "create table URL_" + projectDatabaseIds[i] + tableRows;
				session.createSQLQuery(sql).executeUpdate();

				session.flush();
			}
		}
		catch (Exception e)
		{
			logger.error("Exception in createNewURLTableForProject() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public void createTempURLTables()
	{
		// for safety reasons...
		dropTempURLTables();

		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			String sql = "create table URL_tmp_parent_" + getProjectId() + tableRows;
			session.createSQLQuery(sql).executeUpdate();

			int lowerEnd = PARTITION_SIZE;
			int steps = PARTITION_STEPS;

			for (int i = 0; i < PARTITIONS; i++)
			{
				if (i == (PARTITIONS - 1))
				{
					steps--;
					sql = "create table URL_tmp_child_" + getProjectId() + "_" + i + " (CHECK ( partitionkey >= " + lowerEnd + " AND partitionkey <= " + (lowerEnd + steps) + " )) inherits (URL_tmp_parent_" + getProjectId() + ")";
				}
				else
				{
					sql = "create table URL_tmp_child_" + getProjectId() + "_" + i + " (CHECK ( partitionkey >= " + lowerEnd + " AND partitionkey < " + (lowerEnd + steps) + " )) inherits (URL_tmp_parent_" + getProjectId() + ")";
				}
				lowerEnd = lowerEnd + steps;
				session.createSQLQuery(sql).executeUpdate();

				sql = "create unique index URL_tmp_child_idx_" + getProjectId() + "_" + i + " on URL_tmp_child_" + getProjectId() + "_" + i + " (id)";
				session.createSQLQuery(sql).executeUpdate();

				session.flush();
				session.clear();

			}
			
			if (BotConfig.isPluginAvailable())
			{
				sql = "CREATE UNLOGGED TABLE url_tmp_graph_" + getProjectId() + " (src VARCHAR(4000), des VARCHAR(4000))";
				session.createSQLQuery(sql).executeUpdate();
	
				session.flush();
			}

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

	public void createURLTableIndicies(String projectDatabaseId)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();

			String sql = "create unique index id_IDX_" + projectDatabaseId + " on URL_" + projectDatabaseId + " (id)";
			session.createSQLQuery(sql).executeUpdate();

			sql = "create index canonicalTagHashcode_IDX_" + projectDatabaseId + " on URL_" + projectDatabaseId + " (canonicalTagHashcode)";
			session.createSQLQuery(sql).executeUpdate();

			sql = "create index contentHashcode_IDX_" + projectDatabaseId + " on URL_" + projectDatabaseId + " (contentHashcode)";
			session.createSQLQuery(sql).executeUpdate();

			sql = "create index urlname_IDX_" + projectDatabaseId + " on URL_" + projectDatabaseId + " (URLName)";
			session.createSQLQuery(sql).executeUpdate();

			sql = "create index externallink_IDX_" + projectDatabaseId + " on URL_" + projectDatabaseId + " (externallink)";
			session.createSQLQuery(sql).executeUpdate();

			sql = "create index foundtimestamp_IDX_" + projectDatabaseId + " on URL_" + projectDatabaseId + " (foundtimestamp)";
			session.createSQLQuery(sql).executeUpdate();

			// build statistics
			sql = "analyze URL_" + projectDatabaseId;
			session.createSQLQuery(sql).executeUpdate();

			session.flush();

		}
		catch (Exception e)
		{
			logger.error("Exception in createURLTableIndicies() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public void dropURLTableIndicies(String projectDatabaseId, boolean dropUrlnameIndex)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();

			String sql = "drop index if exists id_IDX_" + projectDatabaseId;
			session.createSQLQuery(sql).executeUpdate();

			sql = "drop index if exists canonicalTagHashcode_IDX_" + projectDatabaseId;
			session.createSQLQuery(sql).executeUpdate();

			sql = "drop index if exists contentHashcode_IDX_" + projectDatabaseId;
			session.createSQLQuery(sql).executeUpdate();

			sql = "drop index if exists textsearch_IDX_" + projectDatabaseId;
			session.createSQLQuery(sql).executeUpdate();

			if (dropUrlnameIndex)
			{
				sql = "drop index if exists urlname_IDX_" + projectDatabaseId;
				session.createSQLQuery(sql).executeUpdate();

				sql = "drop index if exists foundtimestamp_IDX_" + projectDatabaseId;
				session.createSQLQuery(sql).executeUpdate();

				sql = "drop index if exists externallink_IDX_" + projectDatabaseId;
				session.createSQLQuery(sql).executeUpdate();
			}

			session.flush();

		}
		catch (Exception e)
		{
			logger.error("Exception in dropURLTableIndicies() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public void dropTempURLTables()
	{
		Session session = null;
		try
		{
			String sql;
			session = sessionFactory.openSession();
			for (int i = 0; i < PARTITIONS; i++)
			{
				sql = "drop table if exists URL_tmp_child_" + getProjectId() + "_" + i;
				session.createSQLQuery(sql).executeUpdate();
			}

			sql = "drop table if exists URL_tmp_parent_" + getProjectId();
			session.createSQLQuery(sql).executeUpdate();

			sql = "drop table if exists url_tmp_graph_" + projectId;
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

	public void dropURLTableForProject(String[] projectDatabaseIds)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			for (int i = 0; i < projectDatabaseIds.length; i++)
			{
				String sql = "drop table if exists URL_" + projectDatabaseIds[i] + " CASCADE";
				session.createSQLQuery(sql).executeUpdate();

				session.flush();
				session.clear();
			}
		}
		catch (Exception e)
		{
			logger.error("Exception in dropURLTableForProject() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public void copyFromTempToUserTable(String projectDatabaseId)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();

			String sql = "insert into URL_" + projectDatabaseId + " select * from URL_tmp_parent_" + getProjectId() + " where alreadyCrawled = true OR externalLink = true";
			session.createSQLQuery(sql).executeUpdate();

			session.flush();

		}
		catch (Exception e)
		{
			logger.error("Exception in copyFromTempToUserTable() " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public void addLinkToGraph(String source, String destination)
	{
		linkGraphCache.add(new AbstractMap.SimpleEntry<String, String>(source, destination));

		if (linkGraphCache.size() > 15000)
		{
			emptyGraphCache();
		}
	}

	public void emptyGraphCache()
	{
		if (!BotConfig.isPluginAvailable())
		{
			linkGraphCache.clear();
			return;
		}
		
		Connection conn = null;
		JdbcConnectionAccess jdbc = null;
		
		try
		{
			SessionImplementor sfi = (SessionImplementor) sessionFactory.openSession();
			jdbc = sfi.getJdbcConnectionAccess();

			conn = jdbc.obtainConnection();

			PreparedStatement insert = conn.prepareStatement("insert into url_tmp_graph_" + getProjectId() + " values (?,?)");

			for (int i = 0; i < linkGraphCache.size(); i++)
			{
				insert.setString(1, linkGraphCache.get(i).getKey());
				insert.setString(2, linkGraphCache.get(i).getValue());
				insert.addBatch();
			}
			insert.executeBatch();
		}
		catch (Exception e)
		{
			logger.error("Exception in emptyGraphCache() " + e.getMessage());
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
					logger.error("URLManager: unable to release connection in emptyGraphCache() " + e.getMessage());
					e.printStackTrace();
				}
			}

			linkGraphCache.clear();
		}
	}

	protected void addScalars(SQLQuery query)
	{
		query.addScalar("id", StringType.INSTANCE);
		query.addScalar("parentId", StringType.INSTANCE);
		query.addScalar("partitionkey", IntegerType.INSTANCE);
		query.addScalar("URLName", StringType.INSTANCE);
		query.addScalar("canonicalTag", StringType.INSTANCE);
		query.addScalar("canonicalTagHashcode", StringType.INSTANCE);
		query.addScalar("firstFoundAnchorTextToThisURL", StringType.INSTANCE);
		query.addScalar("contentHashcode", StringType.INSTANCE);
		query.addScalar("depthFromDomainRoot", IntegerType.INSTANCE);
		query.addScalar("externalLink", BooleanType.INSTANCE);
		query.addScalar("externalHostName", StringType.INSTANCE);
		query.addScalar("externalLinksOnThisPage", IntegerType.INSTANCE);
		query.addScalar("externalLinksDifferentDomainsOnThisPage", IntegerType.INSTANCE);
		query.addScalar("foundAtURL", StringType.INSTANCE);
		query.addScalar("foundTimestamp", LongType.INSTANCE);
		query.addScalar("h1", StringType.INSTANCE);
		query.addScalar("h2", StringType.INSTANCE);
		query.addScalar("h3", StringType.INSTANCE);
		query.addScalar("httpStatusCode", IntegerType.INSTANCE);
		query.addScalar("internalLinksOnThisPage", IntegerType.INSTANCE);
		query.addScalar("followLinksToThisPage", LongType.INSTANCE);
		query.addScalar("nofollowLinksToThisPage", LongType.INSTANCE);
		query.addScalar("metaDescription", StringType.INSTANCE);
		query.addScalar("metaRobotsFollow", BooleanType.INSTANCE);
		query.addScalar("metaRobotsIndex", BooleanType.INSTANCE);
		query.addScalar("pageSize", IntegerType.INSTANCE);
		query.addScalar("redirectedToURL", StringType.INSTANCE);
		query.addScalar("responseTime", IntegerType.INSTANCE);
		query.addScalar("timeout", BooleanType.INSTANCE);
		query.addScalar("title", StringType.INSTANCE);
		query.addScalar("duplicateContent", BooleanType.INSTANCE);
		query.addScalar("duplicateMetaDescription", BooleanType.INSTANCE);
		query.addScalar("duplicateTitle", BooleanType.INSTANCE);
		query.addScalar("duplicateH1", BooleanType.INSTANCE);
		query.addScalar("canonicalTagIssue", BooleanType.INSTANCE);
		query.addScalar("headlinesNotInRightOrder", BooleanType.INSTANCE);
		query.addScalar("relNofollow", BooleanType.INSTANCE);
		query.addScalar("qualityScore", ShortType.INSTANCE);
		query.addScalar("facebookLikes", IntegerType.INSTANCE);
		query.addScalar("facebookShares", IntegerType.INSTANCE);
		query.addScalar("differentURLSameAnchor", BooleanType.INSTANCE);
		query.addScalar("googleAnalyticsCodeFound", BooleanType.INSTANCE);
		query.addScalar("newPrice", IntegerType.INSTANCE);
		query.addScalar("oldPrice", IntegerType.INSTANCE);
		query.addScalar("color", StringType.INSTANCE);
		query.addScalar("readingLevel", ShortType.INSTANCE);
		query.addScalar("varietyTopicScore", ShortType.INSTANCE);
		query.addScalar("onPageText", StringType.INSTANCE);
		query.addScalar("qwLocale", ShortType.INSTANCE);
		query.addScalar("relevantImages", BooleanType.INSTANCE);
		query.addScalar("adScripts", ShortType.INSTANCE);
		query.addScalar("normalizedText", StringType.INSTANCE);
		query.addScalar("pageRank", DoubleType.INSTANCE);
		query.addScalar("trailingSlashIssue", BooleanType.INSTANCE);
		query.addScalar("gzipIssue", BooleanType.INSTANCE);
		query.addScalar("externalLinkPower", ShortType.INSTANCE);
		query.addScalar("spamScore", ShortType.INSTANCE);
		query.addScalar("backgroundId", ShortType.INSTANCE);
		query.addScalar("pagination", BooleanType.INSTANCE);
		query.addScalar("dcPrimary", BooleanType.INSTANCE);
		query.addScalar("keywordOrientationShortTerm", BooleanType.INSTANCE);
		query.addScalar("keywordOrientationTwoTerms", BooleanType.INSTANCE);
		query.addScalar("topicKeywordOneTerm", StringType.INSTANCE);
		query.addScalar("topicKeywordTwoTerms", StringType.INSTANCE);
		query.addScalar("topicKeywordThreeTerms", StringType.INSTANCE);
		query.addScalar("normalizedTopicKeywordOneTerm", StringType.INSTANCE);
		query.addScalar("normalizedTopicKeywordTwoTerms", StringType.INSTANCE);
		query.addScalar("normalizedTopicKeywordThreeTerms", StringType.INSTANCE);
		query.addScalar("topicKeywordOneTermWeight", ShortType.INSTANCE);
		query.addScalar("topicKeywordTwoTermsWeight", ShortType.INSTANCE);
		query.addScalar("topicKeywordThreeTermsWeight", ShortType.INSTANCE);
		query.addScalar("changeCode", IntegerType.INSTANCE);
		query.addScalar("overwriteFlag", BooleanType.INSTANCE);
		query.addScalar("protocolRecord", BooleanType.INSTANCE);
		query.addScalar("relevantOnPageText", StringType.INSTANCE);
	}

	private String getUpdateInternalLinksSQL(long followLinksToThisPage, long nofollowLinksToThisPage, String urlHash, long partitionKey)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("update URL_tmp_parent_").append(getProjectId()).append(" SET followLinksToThisPage=").append(followLinksToThisPage).append(" , nofollowLinksToThisPage=").append(nofollowLinksToThisPage).append(" where id='").append(urlHash)
				.append("' and partitionkey=").append(partitionKey);
		return sql.toString();
	}

	public long getProjectId()
	{
		return projectId;
	}

	public void setProjectId(long projectId)
	{
		this.projectId = projectId;
	}

	public Locale getDefaultLocale()
	{
		return defaultLocale;
	}

	public void setDefaultLocale(Locale defaultLocale)
	{
		this.defaultLocale = defaultLocale;
	}

	public class LRUCache extends LinkedHashMap<String, URL>
	{

		private static final long serialVersionUID = -3178614849746223993L;

		private final int capacity;

		public LRUCache(int capacity)
		{
			super(capacity + 1, 1.1f, true);
			this.capacity = capacity;
		}

		protected boolean removeEldestEntry(Map.Entry<String, URL> eldest)
		{
			if (size() > capacity)
			{
				Session session = null;
				try
				{
					session = sessionFactory.openSession();
					SQLQuery query;
					updater.incrementAndGet();
					query = session.createSQLQuery(getUpdateInternalLinksSQL(eldest.getValue().getFollowLinksToThisPage(), eldest.getValue().getNofollowLinksToThisPage(), eldest.getKey(), eldest.getValue().getPartitionkey()));
					query.executeUpdate();

					session.flush();

				}
				finally
				{
					if (session != null)
						session.close();
				}
				return true;
			}
			return false;
		}
	}

	/**
	 * Method set query parameter for update or insert-statement
	 * 
	 * @param i
	 *            parameter index
	 * @param query
	 * @param entry
	 *            URL Object
	 * @param insertStatement
	 *            = true , updateStatement = false
	 * @return parameter index for batch-insert calls
	 */
	protected int setQueryParams(int i, SQLQuery query, URL entry, boolean insertStatement)
	{
		if (insertStatement)
		{
			query.setParameter(i++, entry.getId());
			query.setParameter(i++, entry.getParentId());
			query.setParameter(i++, entry.getPartitionkey());
			query.setParameter(i++, entry.getURLName());
		}
		query.setParameter(i++, entry.isAlreadyCrawled());
		query.setParameter(i++, entry.getCanonicalTag());
		query.setParameter(i++, entry.getCanonicalTagHashcode());
		query.setParameter(i++, entry.getFirstFoundAnchorTextToThisURL());
		query.setParameter(i++, entry.getContentHashcode());
		query.setParameter(i++, entry.getDepthFromDomainRoot());
		query.setParameter(i++, entry.isExternalLink());
		query.setParameter(i++, entry.getExternalHostName());
		query.setParameter(i++, entry.getExternalLinksOnThisPage());
		query.setParameter(i++, entry.getExternalLinksDifferentDomainsOnThisPage());
		query.setParameter(i++, entry.getFoundAtURL());
		query.setParameter(i++, entry.getFoundTimestamp());
		query.setParameter(i++, entry.getH1());
		query.setParameter(i++, entry.getH2());
		query.setParameter(i++, entry.getH3());
		query.setParameter(i++, entry.getHttpStatusCode());
		query.setParameter(i++, entry.getInternalOutgoingLinksOnThisPage());
		query.setParameter(i++, entry.getFollowLinksToThisPage());
		query.setParameter(i++, entry.getNofollowLinksToThisPage());
		query.setParameter(i++, entry.getMetaDescription());
		query.setParameter(i++, entry.getMetaRobotsFollow());
		query.setParameter(i++, entry.getMetaRobotsIndex());
		query.setParameter(i++, entry.getPageSize());
		query.setParameter(i++, entry.getRedirectedToURL());
		query.setParameter(i++, entry.getResponseTime());
		query.setParameter(i++, entry.isTimeout());
		query.setParameter(i++, entry.getTitle());
		query.setParameter(i++, entry.isDuplicateContent());
		query.setParameter(i++, entry.isDuplicateMetaDescription());
		query.setParameter(i++, entry.isDuplicateTitle());
		query.setParameter(i++, entry.isDuplicateH1());
		query.setParameter(i++, entry.isCanonicalTagIssue());
		query.setParameter(i++, entry.isHeadlinesNotInRightOrder());
		query.setParameter(i++, entry.isRelNofollow());
		query.setParameter(i++, entry.getQualityScore());
		query.setParameter(i++, entry.getFacebookLikes());
		query.setParameter(i++, entry.getFacebookShares());
		query.setParameter(i++, entry.isDifferentURLSameAnchor());
		query.setParameter(i++, entry.isGoogleAnalyticsCodeFound());
		query.setParameter(i++, entry.getNewPrice());
		query.setParameter(i++, entry.getOldPrice());
		query.setParameter(i++, entry.getColor());
		query.setParameter(i++, entry.getReadingLevel());
		query.setParameter(i++, entry.getVarietyTopicScore());
		query.setParameter(i++, entry.getOnPageText());
		query.setParameter(i++, entry.getQwLocale());
		query.setParameter(i++, entry.isRelevantImages());
		query.setParameter(i++, entry.getAdScripts());
		query.setParameter(i++, entry.getNormalizedTitle());
		query.setParameter(i++, entry.getNormalizedH1());
		query.setParameter(i++, entry.getNormalizedText());
		query.setParameter(i++, entry.getNormalizedH2());
		query.setParameter(i++, entry.getNormalizedH3());
		query.setParameter(i++, entry.getPageRank());
		query.setParameter(i++, entry.isTrailingSlashIssue());
		query.setParameter(i++, entry.isGzipIssue());
		query.setParameter(i++, entry.getExternalLinkPower());
		query.setParameter(i++, entry.getSpamScore());
		query.setParameter(i++, entry.getBackgroundId());
		query.setParameter(i++, entry.isPagination());
		query.setParameter(i++, entry.isDcPrimary());
		query.setParameter(i++, entry.isKeywordOrientationShortTerm());
		query.setParameter(i++, entry.isKeywordOrientationTwoTerms());
		query.setParameter(i++, entry.getTopicKeywordOneTerm());
		query.setParameter(i++, entry.getTopicKeywordTwoTerms());
		query.setParameter(i++, entry.getTopicKeywordThreeTerms());
		query.setParameter(i++, entry.getNormalizedTopicKeywordOneTerm());
		query.setParameter(i++, entry.getNormalizedTopicKeywordTwoTerms());
		query.setParameter(i++, entry.getNormalizedTopicKeywordThreeTerms());
		query.setParameter(i++, entry.getTopicKeywordOneTermWeight());
		query.setParameter(i++, entry.getTopicKeywordTwoTermsWeight());
		query.setParameter(i++, entry.getTopicKeywordThreeTermsWeight());
		query.setParameter(i++, entry.getChangeCode());
		query.setParameter(i++, entry.isOverwriteFlag());
		query.setParameter(i++, entry.isProtocolRecord());
		query.setParameter(i++, entry.isBlockedByRobotsTxt());
		query.setParameter(i++, entry.getRelevantOnPageText());
		
		return i;
	}

	protected String getSQLParameterList()
	{
		int fields = StringUtils.countOccurrencesOf(insertRows, ",");

		StringBuffer sqlParameter = new StringBuffer();

		sqlParameter.append("(");

		for (int i = 0; i <= fields; i++)
		{
			if (i >= fields - 24 && i < fields - 23)
				sqlParameter.append("(setweight(to_tsvector(cast('simple' AS regconfig),cast(coalesce(?,'') AS text)), 'A') || setweight(to_tsvector(cast('simple' AS regconfig),cast(coalesce(?,'') AS text)), 'B') || setweight(to_tsvector(cast('simple' AS regconfig),cast(coalesce(?,'') AS text)), 'C') || setweight(to_tsvector(cast('simple' AS regconfig),cast(coalesce(?,'') AS text)), 'D') || setweight(to_tsvector(cast('simple' AS regconfig),cast(coalesce(?,'') AS text)), 'D'))");
			else
				sqlParameter.append("?");
			if (i < (fields))
			{
				sqlParameter.append(",");
			}
		}
		sqlParameter.append(")");

		return sqlParameter.toString();
	}

	private StringBuffer getDBColsForIssue(IssueType filterCode, boolean csv, Locale locale)
	{
		List<String> columns = filterCode.getSelectedColumnsSQL(csv, false, locale);
		StringBuffer colsBuf = new StringBuffer();

		for (int j = 0; j < columns.size(); j++)
		{
			colsBuf.append(columns.get(j));

			if (j != columns.size() - 1)
			{
				colsBuf.append(",");
			}
			else
			{
				colsBuf.append(" ");
			}
		}
		return colsBuf;
	}

	private String sqlSearchStringHelper(String searchString, String whereFilter, IssueType filterCode)
	{
		if (searchString.length() > 0)
		{
			if (!filterCode.equals(IssueType.All) && !whereFilter.startsWith("order"))
			{
				searchString = searchString + " AND ";
			}
		}
		return searchString;
	}
}
