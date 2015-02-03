package de.solidsearch.bot.businesslogic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.solidsearch.bot.data.HostnameStatistic;
import de.solidsearch.bot.data.ProjectSummary;
import de.solidsearch.bot.data.URLSegmentStatistic;
import de.solidsearch.bot.data.enumerations.QualityScores;

@Component("PostAnalyzer")
@Scope(value = "prototype")
public class PostAnalyzer implements Serializable
{
	private static final long serialVersionUID = -8413595049569252000L;

	private static final Logger logger = Logger.getLogger(PostAnalyzer.class.getName());

	@Autowired
	SessionFactory sessionFactory;

	public ProjectSummary analyzeCanonicalTagIssuesProjectSummary(String projectDatabaseId)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();
			// postgres
			sql.append("UPDATE URL_")
					.append(projectDatabaseId)
					.append(" SET canonicalTagIssue=true WHERE canonicalTagHashcode in (SELECT b.canonicalTagHashcode FROM URL_")
					.append(projectDatabaseId)
					.append("  a, URL_")
					.append(projectDatabaseId)
					.append(" b WHERE b.canonicalTagHashcode = a.id AND a.externalLink=false AND (a.httpStatusCode <> 200 OR a.metaRobotsIndex=false) AND b.externalLink=false AND b.canonicalTagHashcode is not null)");

			SQLQuery query = session.createSQLQuery(sql.toString());
			query.executeUpdate();

			sql.setLength(0);
			sql.append("select count(*) as canonicalTagIssues from URL_").append(projectDatabaseId).append(" where canonicalTagIssue=true");

			query = session.createSQLQuery(sql.toString());
			query.addScalar("canonicalTagIssues", LongType.INSTANCE);

			ProjectSummary canonicalTagIssues = new ProjectSummary();
			try
			{
				query.setResultTransformer(Transformers.aliasToBean(ProjectSummary.class));
				canonicalTagIssues = (ProjectSummary) query.uniqueResult();
			}
			catch (Exception e)
			{
				logger.info("Error calculating CanonicalTagIssues...");
			}
			if (canonicalTagIssues == null)
				canonicalTagIssues = new ProjectSummary();

			System.out.println("Duration of analyzeCanonicalTagIssuesProjectSummary() : " + (System.currentTimeMillis() - timebefore));
			return canonicalTagIssues;
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public ProjectSummary analyzeDuplicateContentURLsAndGetProjectSummary(String projectDatabaseId)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();
			sql.append("update URL_").append(projectDatabaseId)
					.append(" a set duplicateContent=true where metaRobotsIndex=true AND contentHashcode is not null AND ((id=canonicalTagHashcode) OR canonicalTagHashcode is null) AND ");
			sql.append("a.contentHashcode in (select contentHashcode from (select b.contentHashcode as contentHashcode from URL_")
					.append(projectDatabaseId)
					.append(" b where ((b.id=b.canonicalTagHashcode) OR b.canonicalTagHashcode is null) AND  b.contentHashcode is not null AND b.externalLink=false AND b.metaRobotsIndex=true AND b.trailingSlashIssue = false group by b.contentHashcode having count(b.contentHashcode) > 1) as duplicates)");
			SQLQuery query = session.createSQLQuery(sql.toString());
			query.executeUpdate();

			sql.setLength(0);
			sql.append("select count(*) as duplicateContentURLs from URL_").append(projectDatabaseId).append(" where duplicateContent=true");

			query = session.createSQLQuery(sql.toString());
			query.addScalar("duplicateContentURLs", LongType.INSTANCE);

			ProjectSummary duplicates = new ProjectSummary();
			try
			{
				query.setResultTransformer(Transformers.aliasToBean(ProjectSummary.class));
				duplicates = (ProjectSummary) query.uniqueResult();
			}
			catch (org.hibernate.PropertyAccessException e)
			{
				logger.info("Error calculating duplicate values...");
			}
			if (duplicates == null)
				duplicates = new ProjectSummary();

			sql.setLength(0);
			sql.append("create temp table dcPrimaries").append(projectDatabaseId).append(" as (SELECT DISTINCT ON (contenthashcode) ");
			sql.append("contenthashcode, urlname, followlinkstothispage ");
			sql.append("FROM URL_").append(projectDatabaseId).append(" where duplicatecontent = true ");
			sql.append("ORDER BY contenthashcode,followlinkstothispage DESC, urlname)");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			
			sql.setLength(0);
			sql.append("update URL_").append(projectDatabaseId).append(" set dcPrimary = true where duplicatecontent = true AND ");
			sql.append(" urlname in (select urlname from dcPrimaries").append(projectDatabaseId).append(")");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			
			sql.setLength(0);
			sql.append("drop table dcPrimaries").append(projectDatabaseId);
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			
			System.out.println("Duration of analyzeDuplicateContentURLsAndGetProjectSummary() : " + (System.currentTimeMillis() - timebefore));
			return duplicates;
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public ProjectSummary analyzeDuplicateTitleURLsAndGetProjectSummary(String projectDatabaseId)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();
			sql.append("update URL_").append(projectDatabaseId).append(" a set duplicateTitle=true where metaRobotsIndex=true AND externalLink=false AND ((id=canonicalTagHashcode) OR canonicalTagHashcode is null) AND ");
			sql.append("a.title in (select title from (select b.title as title from URL_")
					.append(projectDatabaseId)
					.append(" b where ((b.id=b.canonicalTagHashcode) OR b.canonicalTagHashcode is null) AND b.externalLink=false AND b.metaRobotsIndex=true AND b.trailingSlashIssue = false AND char_length(b.title) > 3 group by b.title having count(b.title) > 1) as duplicates)");
			SQLQuery query = session.createSQLQuery(sql.toString());
			query.executeUpdate();

			sql.setLength(0);
			sql.append("select count(*) as duplicateTitleURLs from URL_").append(projectDatabaseId).append(" where duplicateTitle=true");

			query = session.createSQLQuery(sql.toString());
			query.addScalar("duplicateTitleURLs", LongType.INSTANCE);

			ProjectSummary duplicates = new ProjectSummary();
			try
			{
				query.setResultTransformer(Transformers.aliasToBean(ProjectSummary.class));
				duplicates = (ProjectSummary) query.uniqueResult();
			}
			catch (org.hibernate.PropertyAccessException e)
			{
				logger.info("Error calculating duplicate title values...");
			}
			if (duplicates == null)
				duplicates = new ProjectSummary();

			System.out.println("Duration of analyzeDuplicateTitleURLsAndGetProjectSummary() : " + (System.currentTimeMillis() - timebefore));
			return duplicates;
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public ProjectSummary analyzeKeywordOrientationShortTerm(String projectDatabaseId)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();
			sql.append("update URL_").append(projectDatabaseId).append(" a set keywordOrientationShortTerm=true where metaRobotsIndex=true AND externalLink=false AND ((id=canonicalTagHashcode) OR canonicalTagHashcode is null) AND ");
			sql.append("a.normalizedtopickeywordoneterm in (select normalizedtopickeywordoneterm from (select b.normalizedtopickeywordoneterm as normalizedtopickeywordoneterm from URL_")
					.append(projectDatabaseId)
					.append(" b where ((b.id=b.canonicalTagHashcode) OR b.canonicalTagHashcode is null) AND b.externalLink=false AND b.metaRobotsIndex=true AND b.trailingSlashIssue = false AND char_length(b.normalizedtopickeywordoneterm) > 3 group by b.normalizedtopickeywordoneterm having count(b.normalizedtopickeywordoneterm) > 1) as duplicates)");
			SQLQuery query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);
			sql.append("select count(*) as keywordOrientationShortTermURLs from URL_").append(projectDatabaseId).append(" where keywordOrientationShortTerm=true");

			query = session.createSQLQuery(sql.toString());
			query.addScalar("keywordOrientationShortTermURLs", LongType.INSTANCE);

			ProjectSummary duplicates = new ProjectSummary();
			try
			{
				query.setResultTransformer(Transformers.aliasToBean(ProjectSummary.class));
				duplicates = (ProjectSummary) query.uniqueResult();
			}
			catch (org.hibernate.PropertyAccessException e)
			{
				logger.info("Error calculating analyzeKeywordOrientationShortTerm values...");
			}
			if (duplicates == null)
				duplicates = new ProjectSummary();

			System.out.println("Duration of analyzeKeywordOrientationShortTerm() : " + (System.currentTimeMillis() - timebefore));
			return duplicates;
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}
	
	public ProjectSummary analyzeKeywordOrientationTwoTerms(String projectDatabaseId)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();
			sql.append("update URL_").append(projectDatabaseId).append(" a set keywordOrientationTwoTerms=true where metaRobotsIndex=true AND externalLink=false AND ((id=canonicalTagHashcode) OR canonicalTagHashcode is null) AND ");
			sql.append("a.normalizedtopickeywordtwoterms in (select normalizedtopickeywordtwoterms from (select b.normalizedtopickeywordtwoterms as normalizedtopickeywordtwoterms from URL_")
					.append(projectDatabaseId)
					.append(" b where ((b.id=b.canonicalTagHashcode) OR b.canonicalTagHashcode is null) AND b.externalLink=false AND b.metaRobotsIndex=true AND b.trailingSlashIssue = false AND char_length(b.normalizedtopickeywordtwoterms) > 3 group by b.normalizedtopickeywordtwoterms having count(b.normalizedtopickeywordtwoterms) > 1) as duplicates)");
			SQLQuery query = session.createSQLQuery(sql.toString());
			query.executeUpdate();

			sql.setLength(0);
			sql.append("select count(*) as keywordOrientationTwoTermsURLs from URL_").append(projectDatabaseId).append(" where keywordOrientationTwoTerms=true");

			query = session.createSQLQuery(sql.toString());
			query.addScalar("keywordOrientationTwoTermsURLs", LongType.INSTANCE);

			ProjectSummary duplicates = new ProjectSummary();
			try
			{
				query.setResultTransformer(Transformers.aliasToBean(ProjectSummary.class));
				duplicates = (ProjectSummary) query.uniqueResult();
			}
			catch (org.hibernate.PropertyAccessException e)
			{
				logger.info("Error calculating analyzeKeywordOrientationTwoTerms values...");
			}
			if (duplicates == null)
				duplicates = new ProjectSummary();

			System.out.println("Duration of analyzeKeywordOrientationTwoTerms() : " + (System.currentTimeMillis() - timebefore));
			return duplicates;
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}
	
	public ProjectSummary analyzeDuplicateH1URLsAndGetProjectSummary(String projectDatabaseId)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();
			sql.append("update URL_").append(projectDatabaseId).append(" a set duplicateH1=true where metaRobotsIndex=true AND externalLink=false AND ((id=canonicalTagHashcode) OR canonicalTagHashcode is null) AND ");
			sql.append("a.h1 in (select h1 from (select b.h1 as h1 from URL_")
					.append(projectDatabaseId)
					.append(" b where ((b.id=b.canonicalTagHashcode) OR b.canonicalTagHashcode is null) AND b.externalLink=false AND b.metaRobotsIndex=true AND b.trailingSlashIssue = false AND char_length(b.h1) >= 2 group by b.h1 having count(b.h1) > 1) as duplicates)");
			SQLQuery query = session.createSQLQuery(sql.toString());
			query.executeUpdate();

			sql.setLength(0);
			sql.append("select count(*) as duplicateH1URLs from URL_").append(projectDatabaseId).append(" where duplicateH1=true");

			query = session.createSQLQuery(sql.toString());
			query.addScalar("duplicateH1URLs", LongType.INSTANCE);

			ProjectSummary duplicates = new ProjectSummary();
			try
			{
				query.setResultTransformer(Transformers.aliasToBean(ProjectSummary.class));
				duplicates = (ProjectSummary) query.uniqueResult();
			}
			catch (org.hibernate.PropertyAccessException e)
			{
				logger.info("Error calculating duplicate H1 values...");
			}
			if (duplicates == null)
				duplicates = new ProjectSummary();

			System.out.println("Duration of analyzeDuplicateH1URLsAndGetProjectSummary() : " + (System.currentTimeMillis() - timebefore));
			return duplicates;
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public ProjectSummary analyzeDuplicateMetaDescriptionURLsAndGetProjectSummary(String projectDatabaseId)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();
			sql.append("update URL_").append(projectDatabaseId)
					.append(" a set duplicateMetaDescription=true where metaRobotsIndex=true AND externalLink=false AND ((id=canonicalTagHashcode) OR canonicalTagHashcode is null) AND ");
			sql.append("a.metaDescription in (select metaDescription from (select b.metaDescription as metaDescription from URL_")
					.append(projectDatabaseId)
					.append(" b where ((b.id=b.canonicalTagHashcode) OR b.canonicalTagHashcode is null) AND b.externalLink=false AND b.metaRobotsIndex=true AND b.trailingSlashIssue = false AND char_length(b.metaDescription) > 3 group by b.metaDescription having count(b.metaDescription) > 1) as duplicates)");
			SQLQuery query = session.createSQLQuery(sql.toString());
			query.executeUpdate();

			sql.setLength(0);
			sql.append("select count(*) as duplicateMetaDescriptionURLs from URL_").append(projectDatabaseId).append(" where duplicateMetaDescription=true");

			query = session.createSQLQuery(sql.toString());
			query.addScalar("duplicateMetaDescriptionURLs", LongType.INSTANCE);

			ProjectSummary duplicates = new ProjectSummary();
			try
			{
				query.setResultTransformer(Transformers.aliasToBean(ProjectSummary.class));
				duplicates = (ProjectSummary) query.uniqueResult();
			}
			catch (Exception e)
			{
				logger.info("Error calculating duplicate metaDescription values...");
			}
			if (duplicates == null)
				duplicates = new ProjectSummary();

			System.out.println("Duration of analyzeDuplicateMetaDescriptionURLsAndGetProjectSummary() : " + (System.currentTimeMillis() - timebefore));
			return duplicates;
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public ProjectSummary countDifferentDomainsOfExternalLinks(String projectDatabaseId)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();
			sql.append("select count(distinct externalHostName) as externalURLsDifferentDomains from  URL_").append(projectDatabaseId).append(" where externalLink=true");

			SQLQuery query = session.createSQLQuery(sql.toString());
			query.addScalar("externalURLsDifferentDomains", LongType.INSTANCE);

			ProjectSummary externalURLsDifferentDomains = new ProjectSummary();
			try
			{
				query.setResultTransformer(Transformers.aliasToBean(ProjectSummary.class));
				externalURLsDifferentDomains = (ProjectSummary) query.uniqueResult();
			}
			catch (Exception e)
			{
				logger.info("Error calculating externalHostName values...");
				e.printStackTrace();
			}

			System.out.println("Duration of countDifferentDomainsOfExternalLinks() : " + (System.currentTimeMillis() - timebefore));
			return externalURLsDifferentDomains;
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	/**
	 * Compute quality sorces for all indexable, internal URLs in DB
	 * 
	 * @param projectDatabaseId
	 * @return avg quality score
	 */
	public ProjectSummary computeQualityScores(String projectDatabaseId)
	{
		ProjectSummary qualityThresholds = new ProjectSummary();
		long timebefore = System.currentTimeMillis();
		Session session = null;

		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			sql.append(
					"select coalesce(avg(followLinksToThisPage),0) AS avgInternal, coalesce(max(followLinksToThisPage),0) AS maxInternal, coalesce(avg(externallinksdifferentdomainsonthispage),0) AS avgExternal, coalesce(max(externallinksdifferentdomainsonthispage),0) AS maxExternal from url_")
					.append(projectDatabaseId).append(" where metaRobotsIndex=true AND externalLink=false AND httpStatusCode = 200");
			SQLQuery query = session.createSQLQuery(sql.toString());
			query.addScalar("avgInternal", DoubleType.INSTANCE);
			query.addScalar("maxInternal", DoubleType.INSTANCE);
			query.addScalar("avgExternal", DoubleType.INSTANCE);
			query.addScalar("maxExternal", DoubleType.INSTANCE);

			double avgInternal = 0;
			double maxInternal = 0;
			double avgExternal = 0;
			double maxExternal = 0;

			@SuppressWarnings("unchecked")
			List<Object[]> averages = query.list();
			sql.setLength(0);

			if (!averages.isEmpty())
			{
				avgInternal = (Double) averages.get(0)[0];
				maxInternal = (Double) averages.get(0)[1];
				avgExternal = (Double) averages.get(0)[2];
				maxExternal = (Double) averages.get(0)[3];
			}

			double maxInternalLinksThreshold = 0;
			if (maxInternal != 0)
			{
				// maxLinks - 15%
				maxInternalLinksThreshold = (maxInternal - (double) (maxInternal / 100d * 15d));
				if (maxInternalLinksThreshold < 0)
					maxInternalLinksThreshold = 0;
			}

			double externalLinksThreshold = 0;
			if (maxInternal != 0)
			{
				// avgExternal + 2
				externalLinksThreshold = Math.round(avgExternal) + 2;
				if (externalLinksThreshold <= 0)
					externalLinksThreshold = 2;
			}

			qualityThresholds.setAvgInternalFollowLinks(avgInternal);
			qualityThresholds.setMaxInternalLinksThreshold(maxInternalLinksThreshold);
			qualityThresholds.setExternalLinksThreshold(externalLinksThreshold);

			System.out.println("maxInternal: " + maxInternal + ", maxInternalLinksThreshold:" + maxInternalLinksThreshold + " ,lowerInternalLinksThreshold:" + ", avgInternal: " + avgInternal);
			System.out.println("avgExternal: " + avgExternal + ", externalLinksThreshold:" + externalLinksThreshold + " ,maxExternal: " + maxExternal);

			sql.append("CREATE OR REPLACE FUNCTION score(t_row url_").append(projectDatabaseId).append(") RETURNS integer AS $$ ");
			sql.append("DECLARE ");
			sql.append("result integer = 0; ");
			sql.append("BEGIN ");
			// DC content

			sql.append("if t_row.duplicateContent = false THEN result = result + " + QualityScores.DUPLICATECONTENT.getScore() + "; ");
			sql.append("END IF;");
			sql.append("if t_row.duplicateTitle = false THEN result = result + " + QualityScores.DUPLICATETITLE.getScore() + "; ");
			sql.append("END IF;");
			sql.append("if t_row.duplicateH1 = false THEN result = result + " + QualityScores.DUPLICATEH1.getScore() + "; ");
			sql.append("END IF;");
			// long content
			sql.append("if char_length(t_row.metaDescription) <= 160 THEN result = result + " + QualityScores.LONGMETADESCRIPTION.getScore() + "; ");
			sql.append("END IF;");
			sql.append("if char_length(t_row.title) <= 57 THEN result = result + " + QualityScores.LONGTITLE.getScore() + "; ");
			sql.append("END IF;");
			// low content
			sql.append("if (array_length(string_to_array(t_row.title, ' '),1) > 1 AND char_length(t_row.title) > 0) THEN result = result + " + QualityScores.MISSINGTITLE.getScore() + "; ");
			sql.append("END IF;");
			sql.append("if char_length(t_row.h1) > 2 THEN result = result + " + QualityScores.MISSINGH1.getScore() + "; ");
			sql.append("END IF;");
			sql.append("if char_length(t_row.metaDescription) >= 3 THEN result = result + " + QualityScores.MISSINGMETADESCRIPTION.getScore() + "; ");
			sql.append("END IF;");
			sql.append("if t_row.contentHashcode is not null THEN result = result + " + QualityScores.LOWCONTENT.getScore() + "; ");
			sql.append("END IF; ");
			sql.append("if t_row.headlinesNotInRightOrder = false THEN result = result + " + QualityScores.HEADLINESORDER.getScore() + "; ");
			sql.append("END IF; ");
			// html structure
			sql.append("if t_row.differentURLSameAnchor = false THEN result = result + " + QualityScores.DIFFERENTURLSAMEANCHOR.getScore() + "; ");
			sql.append("END IF;");
			sql.append("if t_row.canonicalTagIssue = false THEN result = result + " + QualityScores.CANONICALISSUE.getScore() + "; ");
			sql.append("END IF;");
			// links
			sql.append("if t_row.followLinksToThisPage > ").append(avgInternal).append(" THEN result = result + " + QualityScores.FOLLOWLINKSTOHISPAGE_MORE_AVG.getScore() + "; ");
			sql.append("END IF; ");
			sql.append("if t_row.followLinksToThisPage > ").append(maxInternalLinksThreshold).append(" THEN result = result + " + QualityScores.FOLLOWLINKSTOHISPAGE_NEAR_MAX.getScore() + "; ");
			sql.append("END IF; ");
			sql.append("if t_row.externallinksdifferentdomainsonthispage < ").append(externalLinksThreshold).append(" THEN result = result + " + QualityScores.EXTERNALLINKS_BELOW_AVG.getScore() + "; ");
			sql.append("END IF; ");
			sql.append("if t_row.depthFromDomainRoot <= 4 THEN result = result + " + QualityScores.LEVEL4_FROM_ROOT.getScore() + "; ");
			sql.append("END IF; ");
			sql.append("if t_row.depthFromDomainRoot <= 3 THEN result = result + " + QualityScores.LEVEL3_FROM_ROOT.getScore() + "; ");
			sql.append("END IF; ");
			sql.append("if t_row.depthFromDomainRoot <= 2 THEN result = result + " + QualityScores.LEVEL2_FROM_ROOT.getScore() + "; ");
			sql.append("END IF; ");
			sql.append("if t_row.depthFromDomainRoot <= 1 THEN result = result + " + QualityScores.LEVEL1_FROM_ROOT.getScore() + "; ");
			sql.append("END IF; ");
			// tech
			sql.append("if t_row.gzipIssue = true THEN result = result + " + QualityScores.GZIPISSUE.getScore() + "; ");
			sql.append("END IF;");
			sql.append("RETURN result; ");
			sql.append("END; ");
			sql.append("$$ LANGUAGE plpgsql;");

			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			sql.append("update url_").append(projectDatabaseId).append(" set qualityScore = score(url_").append(projectDatabaseId).append(") where metaRobotsIndex=true AND externalLink=false AND httpStatusCode = 200");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();

			sql.setLength(0);
			sql.append("drop function score(url_").append(projectDatabaseId).append(")");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();

			sql.setLength(0);
			sql.append("select avg(qualityScore) from url_").append(projectDatabaseId).append(" where metaRobotsIndex=true AND externalLink=false AND httpStatusCode = 200");
			query = session.createSQLQuery(sql.toString());
			BigDecimal result = (BigDecimal) query.uniqueResult();

			if (result != null)
			{
				qualityThresholds.setQualityScore(result.shortValue());
			}
			else
			{
				qualityThresholds.setQualityScore((short) 0);
			}

			System.out.println("Duration of computeQualityScores() : " + (System.currentTimeMillis() - timebefore));
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return qualityThresholds;
	}

	public ProjectSummary analyzeDuplicateAnchorText(String projectDatabaseId)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();
			sql.append("update URL_").append(projectDatabaseId)
					.append(" a set differentURLSameAnchor=true where metaRobotsIndex=true AND externalLink=false AND ((id=canonicalTagHashcode) OR canonicalTagHashcode is null) AND ");
			sql.append("a.firstFoundAnchorTextToThisURL in (select firstFoundAnchorTextToThisURL from (select b.firstFoundAnchorTextToThisURL as firstFoundAnchorTextToThisURL from URL_")
					.append(projectDatabaseId)
					.append(" b where ((b.id=b.canonicalTagHashcode) OR b.canonicalTagHashcode is null) AND b.externalLink=false AND b.metaRobotsIndex=true AND char_length(b.firstFoundAnchorTextToThisURL) > 2 group by b.firstFoundAnchorTextToThisURL having count(b.firstFoundAnchorTextToThisURL) > 1) as duplicates)");
			SQLQuery query = session.createSQLQuery(sql.toString());
			query.executeUpdate();

			sql.setLength(0);
			sql.append("select count(*) as differentURLSameAnchor from URL_").append(projectDatabaseId).append(" where differentURLSameAnchor=true");

			query = session.createSQLQuery(sql.toString());
			query.addScalar("differentURLSameAnchor", LongType.INSTANCE);

			ProjectSummary duplicates = new ProjectSummary();
			try
			{
				query.setResultTransformer(Transformers.aliasToBean(ProjectSummary.class));
				duplicates = (ProjectSummary) query.uniqueResult();
			}
			catch (Exception e)
			{
				logger.info("Error calculating duplicate anchor text values...");
			}
			if (duplicates == null)
				duplicates = new ProjectSummary();

			System.out.println("Duration of analyzeDuplicateAnchorText() : " + (System.currentTimeMillis() - timebefore));
			return duplicates;
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public ArrayList<HostnameStatistic> getLinkHostnameStatistic(long projectId, ProjectSummary projectSummary)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();
			sql.append("select substring( urlname from '.*://([^/]*)' ) as hostname, count(*) as pages, sum(followlinkstothispage) as followLinksToHostname, sum(nofollowlinkstothispage) as nofollowLinksToHostname from URL_tmp_parent_")
					.append(projectId).append(" group by hostname order by followLinksToHostname desc");
			SQLQuery query = session.createSQLQuery(sql.toString());

			query.addScalar("hostname", StringType.INSTANCE);
			query.addScalar("pages", LongType.INSTANCE);
			query.addScalar("followLinksToHostname", LongType.INSTANCE);
			query.addScalar("nofollowLinksToHostname", LongType.INSTANCE);

			query.setResultTransformer(Transformers.aliasToBean(HostnameStatistic.class));

			ArrayList<HostnameStatistic> statsArrayList = new ArrayList<HostnameStatistic>();

			boolean debug = false;
			@SuppressWarnings("unchecked")
			List<HostnameStatistic> stats = query.list();
			Iterator<HostnameStatistic> iterator = stats.iterator();
			while (iterator.hasNext())
			{
				HostnameStatistic item = iterator.next();
				item.setProjectSummary(projectSummary);
				if (item.getHostname() != null)
					statsArrayList.add(item);
				else
					debug = true;
			}

			StringBuffer hostNames = new StringBuffer();
			for (int i = 0; i < statsArrayList.size(); i++)
			{
				hostNames.append(statsArrayList.get(i).getHostname()).append(" ");
			}
			System.out.println("Duration of getLinkHostnamestatistic() : " + (System.currentTimeMillis() - timebefore) + " null-item found:" + debug + " hostnames#: " + statsArrayList.size() + " hostnames: " + hostNames.toString());

			return statsArrayList;
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	/**
	 * Extract 100 Top followlink-statistcs by urlsegment.
	 * 
	 * @param projectId
	 * @return
	 */
	public ArrayList<URLSegmentStatistic> getURLSegmentStatistics(long projectId, ProjectSummary projectSummary)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			sql.append("select '/' || split_part(urlname,'/',4) as urlsegment, count(urlname) as pages, cast(avg(responsetime) as int) as avgresponsetime, cast(avg(pageSize) as int) as avgpagesize from URL_tmp_parent_").append(projectId)
					.append(" where alreadycrawled=true AND externallink=false group by urlsegment order by pages desc");

			SQLQuery query = session.createSQLQuery(sql.toString());
			query.addScalar("urlsegment", StringType.INSTANCE);
			query.addScalar("pages", LongType.INSTANCE);
			query.addScalar("avgresponsetime", IntegerType.INSTANCE);
			query.addScalar("avgpagesize", IntegerType.INSTANCE);

			query.setResultTransformer(Transformers.aliasToBean(URLSegmentStatistic.class));

			ArrayList<URLSegmentStatistic> statsArrayList_values = new ArrayList<URLSegmentStatistic>();

			@SuppressWarnings("unchecked")
			List<URLSegmentStatistic> stats_values = query.list();
			Iterator<URLSegmentStatistic> iterator = stats_values.iterator();
			while (iterator.hasNext())
			{
				URLSegmentStatistic item = iterator.next();
				statsArrayList_values.add(item);
			}

			sql.setLength(0);

			sql.append(
					"select '/' || split_part(urlname,'/',4) as urlsegment, sum(followlinkstothispage) as followLinksToSegment, sum(nofollowlinkstothispage) as nofollowLinksToSegment, cast(avg(responsetime) as int) as avgresponsetime, cast(avg(pageSize) as int) as avgpagesize from URL_tmp_parent_")
					.append(projectId).append(" where externallink=false group by urlsegment order by followLinksToSegment desc limit 100");

			query = session.createSQLQuery(sql.toString());

			query.addScalar("urlsegment", StringType.INSTANCE);
			query.addScalar("followLinksToSegment", LongType.INSTANCE);
			query.addScalar("nofollowLinksToSegment", LongType.INSTANCE);
			query.setResultTransformer(Transformers.aliasToBean(URLSegmentStatistic.class));

			ArrayList<URLSegmentStatistic> statsArrayList_links = new ArrayList<URLSegmentStatistic>();

			@SuppressWarnings("unchecked")
			List<URLSegmentStatistic> stats_links = query.list();
			iterator = stats_links.iterator();
			while (iterator.hasNext())
			{
				URLSegmentStatistic linksStats = iterator.next();

				for (int i = 0; i < statsArrayList_values.size(); i++)
				{
					URLSegmentStatistic valueStats = statsArrayList_values.get(i);
					
					if (valueStats.getUrlsegment().equalsIgnoreCase(linksStats.getUrlsegment()))
					{
						linksStats.setAvgpagesize(valueStats.getAvgpagesize());
						linksStats.setAvgresponsetime(valueStats.getAvgresponsetime());
						linksStats.setPages(valueStats.getPages());
						break;
					}

				}
				statsArrayList_links.add(linksStats);
			}

			sql.setLength(0);

			sql.append("select urlsegment, count(*) as keywords from (select '/' || split_part(urlname,'/',4) as urlsegment, unnest(string_to_array(trim(cast(strip(normalizedtext) as text), ''''), ''' ''')) as stemmedkeyword from URL_tmp_parent_")
					.append(projectId).append(" where length(contenthashcode)>0 group by stemmedkeyword, urlsegment) as foo group by urlsegment order by keywords desc limit 100");

			query = session.createSQLQuery(sql.toString());

			query.addScalar("urlsegment", StringType.INSTANCE);
			query.addScalar("keywords", LongType.INSTANCE);
			query.setResultTransformer(Transformers.aliasToBean(URLSegmentStatistic.class));

			ArrayList<URLSegmentStatistic> statsArrayList_keywords = new ArrayList<URLSegmentStatistic>();

			@SuppressWarnings("unchecked")
			List<URLSegmentStatistic> stats_keywords = query.list();
			iterator = stats_keywords.iterator();
			while (iterator.hasNext())
			{
				statsArrayList_keywords.add(iterator.next());
			}

			for (int i = 0; i < statsArrayList_links.size(); i++)
			{
				for (int y = 0; y < statsArrayList_keywords.size(); y++)
				{
					if (statsArrayList_links.get(i).getUrlsegment().equalsIgnoreCase(statsArrayList_keywords.get(y).getUrlsegment()))
					{
						statsArrayList_links.get(i).setKeywords(statsArrayList_keywords.get(y).getKeywords());
						break;
					}
				}
				statsArrayList_links.get(i).setProjectSummary(projectSummary);
			}

			System.out.println("Duration of getURLSegmentStatistics() : " + (System.currentTimeMillis() - timebefore));
			return statsArrayList_links;
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public void generateComparisonView(long projectID, String newestProjectDatabaseId, String previousProjectDatabaseId)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			sql.append("DROP VIEW IF EXISTS crawlingdifferences_").append(projectID);

			SQLQuery query = session.createSQLQuery(sql.toString());
			query.executeUpdate();

			sql.setLength(0);

			sql.append("CREATE or REPLACE view crawlingdifferences_").append(projectID).append(" AS select a.urlname as ").append("\"").append("newestcrawling").append("\"").append(",b.urlname as ").append("\"").append("previouscrawling")
					.append("\"").append(" from (select a.urlname from URL_").append(newestProjectDatabaseId).append(" a ) as a FULL OUTER JOIN (select b.urlname from URL_").append(previousProjectDatabaseId)
					.append(" b ) as b ON (a.urlname = b.urlname)");

			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();

			System.out.println("Duration of generateComparisonView() : " + (System.currentTimeMillis() - timebefore));

		}
		finally
		{
			if (session != null)
				session.close();
		}
	}
	
	public void generateLowContentView(long projectID, String newestProjectDatabaseId, String previousProjectDatabaseId)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			sql.append("DROP VIEW IF EXISTS new_lowcontent_").append(projectID);

			SQLQuery query = session.createSQLQuery(sql.toString());
			query.executeUpdate();

			sql.setLength(0);

			sql.append("CREATE OR REPLACE VIEW new_lowcontent_").append(projectID)
			.append(" AS SELECT new.urlname FROM url_").append(newestProjectDatabaseId)
			.append(" new where new.contenthashcode is null and new.httpstatuscode = 200 AND new.urlname not in (SELECT urlname FROM url_")
			.append(previousProjectDatabaseId).append(")");
			
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();

			System.out.println("Duration of generateLowContentView() : " + (System.currentTimeMillis() - timebefore));

		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	/**
	 * Compute quality sorces for all indexable, internal URLs in DB
	 * 
	 * @param projectDatabaseId
	 * @return avg quality score
	 */
	public void computeVarietyTopicScore(long projectID, String projectDatabaseId)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;

		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			sql.append("select COALESCE(max(frequency),0) from keywords_").append(projectID);
			SQLQuery query = session.createSQLQuery(sql.toString());

			BigInteger result = (BigInteger) query.uniqueResult();
			sql.setLength(0);
			long maxCount = result.longValue();

			long less90PercentFromMax = maxCount;

			if (maxCount > 0)
			{
				less90PercentFromMax = (long) (0.90 * maxCount);
			}
			
			sql.append("create temp table tmp_topics").append(projectDatabaseId)
					.append(" as (select to_tsvector('simple',keywordstem) as keyword, 0 as score, relevantindocuments from keywords_").append(projectID).append(" where maxweight > 30 AND relevantindocuments > 0 AND frequency < ").append(less90PercentFromMax).append(")");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);


			sql.append("update tmp_topics").append(projectDatabaseId)
					.append(" set score = (case when relevantindocuments>8 then 100 when relevantindocuments>7 then 85 when relevantindocuments>6 then 70 when relevantindocuments>5 then 60 when relevantindocuments>4 then 50 when relevantindocuments>3 then 40 when relevantindocuments>2 then 30 when relevantindocuments>1 then 20 else 0 end)");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);
			
			sql.append("create temp table tmp_topics_2_").append(projectDatabaseId)
			.append(" as (select * from ").append("tmp_topics").append(projectDatabaseId).append(" where score > 0)");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);
			
			sql.append("drop table tmp_topics").append(projectDatabaseId).append(" ");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			sql.append("CREATE INDEX keyword_idx").append(projectDatabaseId).append(" ON tmp_topics_2_").append(projectDatabaseId).append(" USING gin(keyword)");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			sql.append("analyze tmp_topics_2_").append(projectDatabaseId);
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			sql.append("update url_").append(projectDatabaseId).append(" set varietyTopicScore = ");
			sql.append("(select COALESCE(avg(score),0) from tmp_topics_2_").append(projectDatabaseId).append(" where (plainto_tsquery('simple',normalizedtopickeywordoneterm) @@ keyword)) ");
			sql.append(" where length(normalizedtopickeywordoneterm)>0 AND length(contenthashcode)>0 AND externallink= false AND varietyTopicScore = 0");

			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			sql.append("drop table tmp_topics_2_").append(projectDatabaseId).append(" ");

			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();

			System.out.println("Duration of computeVarietyTopicScore() : " + (System.currentTimeMillis() - timebefore));
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public ProjectSummary analyzeAvgReadingLevel(String projectDatabaseId)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			sql.append("select avg(readinglevel) as readinglevel from URL_").append(projectDatabaseId).append(" where readinglevel>=0");

			SQLQuery query = session.createSQLQuery(sql.toString());
			query.addScalar("readinglevel", DoubleType.INSTANCE);

			ProjectSummary readinglevel = new ProjectSummary();
			try
			{
				query.setResultTransformer(Transformers.aliasToBean(ProjectSummary.class));
				readinglevel = (ProjectSummary) query.uniqueResult();
			}
			catch (Exception e)
			{
				logger.info("Error calculating analyzeAvgReadingLevel...");
			}
			if (readinglevel == null)
				readinglevel = new ProjectSummary();

			System.out.println("Duration of analyzeAvgReadingLevel() : " + (System.currentTimeMillis() - timebefore));
			return readinglevel;
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public ProjectSummary findTrailingSlashIssues(String projectDatabaseId)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			// temp table with canonial urls (key, without trailingslash)
			sql.append("create temp table slashproblems_").append(projectDatabaseId);
			sql.append(" as (select case when right(urlname,1)  = '/' then left(urlname,length(urlname)-1) else urlname end as key from URL_").append(projectDatabaseId);
			sql.append(" where externallink= false group by key having count(*)>1)");
			SQLQuery query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			// temp table with only slashproblems and all interesting values
			sql.append("create temp table detailslashproblems_").append(projectDatabaseId);
			sql.append(" as (select * from (select case when right(urlname,1)  = '/' then left(urlname,length(urlname)-1) else urlname end as key,");
			sql.append("urlname,canonicaltag,followlinkstothispage,");
			sql.append("nofollowlinkstothispage,externallinksonthispage,externallinksdifferentdomainsonthispage,");
			sql.append("internallinksonthispage");
			sql.append(" from URL_").append(projectDatabaseId).append(" where externallink= false) as foo where key in (select key from slashproblems_").append(projectDatabaseId).append("))");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			sql.append("drop table slashproblems_").append(projectDatabaseId);
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			// create index on key
			sql.append("create index key_dsp_idx_").append(projectDatabaseId).append(" on detailslashproblems_").append(projectDatabaseId).append(" (key)");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			sql.append("create index urlname_dsp_idx_").append(projectDatabaseId).append(" on detailslashproblems_").append(projectDatabaseId).append(" (urlname)");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			// analyze table
			sql.append("analyze detailslashproblems_").append(projectDatabaseId);
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			// choose the version with less links or canonical tag, to mark as primary
			sql.append("CREATE TEMP TABLE primaries_").append(projectDatabaseId).append(" as (");
			sql.append("select case when urlname = canonicaltag then urlname when (urlname <> canonicaltag and length(canonicaltag)>0) then canonicaltag else urlname end as urlname, key from ( ");
			sql.append("select a.urlname,a.canonicaltag,a.key from detailslashproblems_").append(projectDatabaseId)
					.append(" as a JOIN (select substring(urlname from 0 for length(urlname)) as urlname, followlinkstothispage from detailslashproblems_").append(projectDatabaseId)
					.append(" as b) as b on a.urlname = b.urlname AND a.followlinkstothispage > b.followlinkstothispage ");
			sql.append(" UNION ");
			sql.append("select a.urlname,a.canonicaltag,a.key from detailslashproblems_").append(projectDatabaseId).append(" as a JOIN (select urlname,canonicaltag, followlinkstothispage from detailslashproblems_").append(projectDatabaseId)
					.append(" as b) as b on substring(a.urlname from 0 for length(a.urlname)) = b.urlname AND a.followlinkstothispage >= b.followlinkstothispage) as foo)");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			// get the opposite of the primary
			sql.append("CREATE TEMP TABLE noneprimaries_").append(projectDatabaseId).append(" as (");
			sql.append("select a.urlname from detailslashproblems_").append(projectDatabaseId).append(" a,primaries_").append(projectDatabaseId).append(" b where a.key=b.key AND a.urlname<>b.urlname)");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			sql.append("drop table primaries_").append(projectDatabaseId);
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			// aggregate values
			sql.append("create temp table aggvalues_").append(projectDatabaseId);
			sql.append(" as (select key,sum(followlinkstothispage) as followlinkstothispage,");
			sql.append("sum(nofollowlinkstothispage) as nofollowlinkstothispage, sum(externallinksonthispage) as externallinksonthispage,");
			sql.append("sum(externallinksdifferentdomainsonthispage) as externallinksdifferentdomainsonthispage,");
			sql.append("sum(internallinksonthispage) as internallinksonthispage from detailslashproblems_").append(projectDatabaseId).append(" group by key)");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			// set trailingSlashissue to true to the version of none primary (less links or non-canonical)
			sql.append("update URL_").append(projectDatabaseId).append(" set trailingSlashIssue = true where externalLink = false and urlname in (select urlname from noneprimaries_").append(projectDatabaseId).append(" )");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			// update url table with aggregated values of both versions
			sql.append("update URL_")
					.append(projectDatabaseId)
					.append(" set followlinkstothispage = sub.followlinkstothispage, nofollowlinkstothispage = sub.nofollowlinkstothispage, externallinksonthispage = sub.externallinksonthispage, externallinksdifferentdomainsonthispage = sub.externallinksdifferentdomainsonthispage, internallinksonthispage = sub.internallinksonthispage from ");
			sql.append("(select * from aggvalues_").append(projectDatabaseId).append(") as sub where sub.key = urlname OR sub.key = left(urlname,length(urlname)-1)");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			sql.append("select count(*) as trailingSlashIssues from URL_").append(projectDatabaseId).append(" where trailingSlashIssue = true");
			query = session.createSQLQuery(sql.toString());
			sql.setLength(0);

			query.addScalar("trailingSlashIssues", LongType.INSTANCE);

			ProjectSummary trailingSlashIssues = new ProjectSummary();
			try
			{
				query.setResultTransformer(Transformers.aliasToBean(ProjectSummary.class));
				trailingSlashIssues = (ProjectSummary) query.uniqueResult();
			}
			catch (Exception e)
			{
				logger.info("Error in findTrailingSlashIssues...");
				e.printStackTrace();
			}

			sql.append("drop table if exists slashproblems_").append(projectDatabaseId);
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			System.out.println("Duration of trailingSlashIssues() : " + (System.currentTimeMillis() - timebefore));
			return trailingSlashIssues;
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	public ProjectSummary computeTotalKeywords(String projectDatabaseId)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			sql.append("select count(*) as totalCountOfKeywords from (select distinct unnest(string_to_array(trim(cast(strip(normalizedtext) as text), ''''), ''' ''')) as stemmedkeyword from URL_").append(projectDatabaseId)
					.append(" where length(contenthashcode)>0) as foo");

			SQLQuery query = session.createSQLQuery(sql.toString());
			query.addScalar("totalCountOfKeywords", LongType.INSTANCE);

			ProjectSummary totalCountOfKeywords = new ProjectSummary();
			try
			{
				query.setResultTransformer(Transformers.aliasToBean(ProjectSummary.class));
				totalCountOfKeywords = (ProjectSummary) query.uniqueResult();
			}
			catch (Exception e)
			{
				logger.info("Error calculating computeTotalKeywords...");
			}
			if (totalCountOfKeywords == null)
				totalCountOfKeywords = new ProjectSummary();

			System.out.println("Duration of computeTotalKeywords() : " + (System.currentTimeMillis() - timebefore));
			return totalCountOfKeywords;
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	/**
	 * Compute computeExternalLinkPower for external URLs in DB
	 * 
	 * @param projectDatabaseId
	 */
	public void computeExternalLinkPower(String projectDatabaseId)
	{
		long timebefore = System.currentTimeMillis();
		Session session = null;

		try
		{
			session = sessionFactory.openSession();
			StringBuffer sql = new StringBuffer();

			sql.append("select coalesce(max(pagerank),0) AS maxPagerank, coalesce(avg(pagerank),0) AS avgPagerank from url_").append(projectDatabaseId).append(" where pagerank <> 0");
			SQLQuery query = session.createSQLQuery(sql.toString());
			query.addScalar("maxPagerank", DoubleType.INSTANCE);
			query.addScalar("avgPagerank", DoubleType.INSTANCE);

			double maxPagerank = 0;
			double avgPagerank = 0;

			@SuppressWarnings("unchecked")
			List<Object[]> averages = query.list();
			sql.setLength(0);

			if (!averages.isEmpty())
			{
				maxPagerank = (Double) averages.get(0)[0];
				avgPagerank = (Double) averages.get(0)[1];
			}

			double max1PRThreshold = 0;
			if (maxPagerank != 0)
			{
				// - 25%
				max1PRThreshold = (maxPagerank - (double) (maxPagerank / 100d * 25d));
				if (max1PRThreshold < 0)
					max1PRThreshold = 0;
			}

			double avg1PRThreshold = 0;
			if (avgPagerank != 0)
			{
				// + 40%
				avg1PRThreshold = (avgPagerank + (double) (avgPagerank / 100d * 40d));
				if (avg1PRThreshold < 0)
					avg1PRThreshold = 0;
			}

			sql.append("create temp table externalsource_").append(projectDatabaseId).append(" as (select urlname,pagerank from url_").append(projectDatabaseId).append(" where urlname in (select foundaturl from url_").append(projectDatabaseId)
					.append(" where externalLink=true))");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			sql.append("update url_")
					.append(projectDatabaseId)
					.append(" as a set externalLinkPower = a.externalLinkPower + sub.externalLinkPower from (select case when pagerank >= " + max1PRThreshold + " then 50 when pagerank >= " + avg1PRThreshold + " then 35 when pagerank >= "
							+ avgPagerank + " then 15 else 0 end as externalLinkPower,urlname from externalsource_").append(projectDatabaseId).append(") as sub where sub.urlname = a.foundaturl AND a.externallink = true AND a.externalLinkPower <> 0");
			query = session.createSQLQuery(sql.toString());
			query.executeUpdate();
			sql.setLength(0);

			System.out.println("Duration of computeExternalLinkPower() : " + (System.currentTimeMillis() - timebefore) + " max1Threshold: " + max1PRThreshold + " avg1PRThreshold: " + avg1PRThreshold + " avgThreshold: " + avgPagerank);
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

}
