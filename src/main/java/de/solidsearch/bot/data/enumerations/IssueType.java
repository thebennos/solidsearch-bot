package de.solidsearch.bot.data.enumerations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;

import com.vaadin.ui.UI;

import de.qualitywatch.Qualitywatch;
import de.solidsearch.bot.data.URLSQLColumn;
import de.solidsearch.bot.i18n.USMessages;
import de.solidsearch.bot.utils.BotConfig;

public enum IssueType
{
	All, CLIENTERRORS, SERVERERRORS, REDIRECTIONS, ROBOTSINDEX, ROBOTSNOINDEX, ROBOTSFOLLOW, ROBOTSNOFOLLOW, TIMEOUTS, DUPLICATECONTENT, DUPLICATETITLE, DUPLICATEMETA, DUPLICATEH1, LOWCONTENT, LONGMETADESCRIPTION, MISSINGMETADESCRIPTION, LONGTITLE, MISSINGTITLE, CANONICALTOSOURCE, MISSINGH1, CANONICALISSUE, HEADLINESORDER, EXTERNALLINKS, PAGESIZE, INTERNALTARGETS, QUALITYSCORE, DIFFERENTURLSAMEANCHOR, MISSINGGOOGLEANALYTICSCODE, KEYWORDS, TRAILINGSLASHISSUES, GZIPISSUES, KEYWORDORIENTATIONSHORTTERM, KEYWORDORIENTATIONTWOTERMS;

	public String getLocalizedUIText()
	{
		ResourceBundle i18n;
		
		if (BotConfig.isPluginAvailable())
		{
			i18n = ResourceBundle.getBundle(USMessages.class.getName(), ((Qualitywatch) UI.getCurrent()).getLocale());
		}
		else
		{
			i18n = ResourceBundle.getBundle(USMessages.class.getName(), Locale.ENGLISH);
		}
		String text = "-";
		switch (this)
		{
		case All:
			text = i18n.getString(USMessages.All);
			break;
		case CLIENTERRORS:
			text = i18n.getString(USMessages.ClientErrors4xx);
			break;
		case CANONICALISSUE:
			text = i18n.getString(USMessages.CanonicalIssues);
			break;
		case CANONICALTOSOURCE:
			text = i18n.getString(USMessages.CanonicaltoSource);
			break;
		case DUPLICATECONTENT:
			text = i18n.getString(USMessages.DuplicateContent);
			break;
		case DUPLICATEH1:
			text = i18n.getString(USMessages.DuplicateH1);
			break;
		case DUPLICATEMETA:
			text = i18n.getString(USMessages.DuplicateMetaDescription);
			break;
		case DUPLICATETITLE:
			text = i18n.getString(USMessages.DuplicateTitle);
			break;
		case EXTERNALLINKS:
			text = i18n.getString(USMessages.ExternalLinks);
			break;
		case HEADLINESORDER:
			text = i18n.getString(USMessages.HeadlinesNotInOrder);
			break;
		case INTERNALTARGETS:
			text = i18n.getString(USMessages.LinkTargets);
			break;
		case LONGMETADESCRIPTION:
			text = i18n.getString(USMessages.LongMetaDescription);
			break;
		case LONGTITLE:
			text = i18n.getString(USMessages.LongTitle);
			break;
		case LOWCONTENT:
			text = i18n.getString(USMessages.LowContent);
			break;
		case MISSINGH1:
			text = i18n.getString(USMessages.MissingH1);
			break;
		case MISSINGMETADESCRIPTION:
			text = i18n.getString(USMessages.MissingMetaDescription);
			break;
		case MISSINGTITLE:
			text = i18n.getString(USMessages.MissingTitle);
			break;
		case PAGESIZE:
			text = i18n.getString(USMessages.PageSize);
			break;
		case REDIRECTIONS:
			text = i18n.getString(USMessages.Redirections3xx);
			break;
		case ROBOTSFOLLOW:
			text = i18n.getString(USMessages.RobotsFollow);
			break;
		case ROBOTSINDEX:
			text = i18n.getString(USMessages.RobotsIndex);
			break;
		case ROBOTSNOFOLLOW:
			text = i18n.getString(USMessages.RobotsNofollow);
			break;
		case ROBOTSNOINDEX:
			text = i18n.getString(USMessages.RobotsNoindex);
			break;
		case SERVERERRORS:
			text = i18n.getString(USMessages.ServerErrors5xx);
			break;
		case TIMEOUTS:
			text = i18n.getString(USMessages.Availability);
			break;
		case QUALITYSCORE:
			text = i18n.getString(USMessages.Solidsearchscore);
			break;	
		case DIFFERENTURLSAMEANCHOR:
			text = i18n.getString(USMessages.DifferentURLSameAnchor);
			break;
		case MISSINGGOOGLEANALYTICSCODE:
			text = i18n.getString(USMessages.MissingGoogleAnalyticsCode);
			break;
		case KEYWORDS:
			text = i18n.getString(USMessages.Keywords);
			break;
		case TRAILINGSLASHISSUES:
			text = i18n.getString(USMessages.TrailingSlashIssues);
			break;
		case GZIPISSUES:
			text = i18n.getString(USMessages.GzipIssues);
			break;
		case KEYWORDORIENTATIONSHORTTERM:
			text = i18n.getString(USMessages.KeywordOrientationShortTerm);
			break;
		case KEYWORDORIENTATIONTWOTERMS:
			text = i18n.getString(USMessages.KeywordOrientationTwoTerms);
			break;
		default:
			text = "-";
			break;
		}
		return text;
	}

	public String getLocalizedUIHelpText()
	{
		ResourceBundle i18n = ResourceBundle.getBundle(USMessages.class.getName(), ((Qualitywatch) UI.getCurrent()).getLocale());
		String text = "-";
		switch (this)
		{
		case All:
			text = i18n.getString(USMessages.HelpAll);
			break;
		case CLIENTERRORS:
			text = i18n.getString(USMessages.HelpMissingTitles);
			break;
		case CANONICALISSUE:
			text = i18n.getString(USMessages.HelpCanonicalIssues);
			break;
		case CANONICALTOSOURCE:
			text = i18n.getString(USMessages.HelpCanonicaltoSource);
			break;
		case DUPLICATECONTENT:
			text = i18n.getString(USMessages.HelpDuplicateContent);
			break;
		case DUPLICATEH1:
			text = i18n.getString(USMessages.HelpDuplicateH1);
			break;
		case DUPLICATEMETA:
			text = i18n.getString(USMessages.HelpDuplicateMetaDescription);
			break;
		case DUPLICATETITLE:
			text = i18n.getString(USMessages.HelpDuplicateTitles);
			break;
		case EXTERNALLINKS:
			text = i18n.getString(USMessages.HelpExternalLinks);
			break;
		case HEADLINESORDER:
			text = i18n.getString(USMessages.HelpHeadlinesNotInOrder);
			break;
		case INTERNALTARGETS:
			text = i18n.getString(USMessages.HelpLinkTargets);
			break;
		case LONGMETADESCRIPTION:
			text = i18n.getString(USMessages.HelpLongMetaDescription);
			break;
		case LONGTITLE:
			text = i18n.getString(USMessages.HelpLongTitles);
			break;
		case LOWCONTENT:
			text = i18n.getString(USMessages.HelpLowContent);
			break;
		case MISSINGH1:
			text = i18n.getString(USMessages.HelpMissingH1);
			break;
		case MISSINGMETADESCRIPTION:
			text = i18n.getString(USMessages.HelpMissingMetaDescription);
			break;
		case MISSINGTITLE:
			text = i18n.getString(USMessages.HelpMissingTitles);
			break;
		case PAGESIZE:
			text = i18n.getString(USMessages.HelpPageSize);
			break;
		case REDIRECTIONS:
			text = i18n.getString(USMessages.HelpRedirections3xx);
			break;
		case ROBOTSFOLLOW:
			text = i18n.getString(USMessages.HelpRobotsFollow);
			break;
		case ROBOTSINDEX:
			text = i18n.getString(USMessages.HelpRobotsIndex);
			break;
		case ROBOTSNOFOLLOW:
			text = i18n.getString(USMessages.HelpRobotsNofollow);
			break;
		case ROBOTSNOINDEX:
			text = i18n.getString(USMessages.HelpRobotsNoindex);
			break;
		case SERVERERRORS:
			text = i18n.getString(USMessages.HelpServerErrors5xx);
			break;
		case TIMEOUTS:
			text = i18n.getString(USMessages.HelpAvailability);
			break;
		case QUALITYSCORE:
			text = i18n.getString(USMessages.HelpQualityscore);
			break;
		case DIFFERENTURLSAMEANCHOR:
			text = i18n.getString(USMessages.HelpDifferentURLSameAnchor);
			break;
		case MISSINGGOOGLEANALYTICSCODE:
			text = i18n.getString(USMessages.HelpMissingGoogleAnalyticsCode);
			break;
		default:
			text = "-";
			break;
		}
		return text;
	}

	public List<String> getSelectedColumnsSQL(boolean csv, boolean plain, Locale locale)
	{
		URLSQLColumn s = new URLSQLColumn(locale);
		
		ArrayList<String> columnDefaults = new ArrayList<String>();
		ArrayList<String> columns = null;
		
		int i = 0;
		columnDefaults.add(i++, s.getURLName(csv));
		columnDefaults.add(i++, s.getQualityscore(csv));
		columnDefaults.add(i++, s.getReadingLevel(csv));
		columnDefaults.add(i++, s.getVarietyTopicScore(csv));
		columnDefaults.add(i++, s.getPageRank(csv));
		columnDefaults.add(i++, s.getHttpStatusCode(csv));
		columnDefaults.add(i++, s.getNofollowLinksToThisPage(csv));
		columnDefaults.add(i++, s.getFollowLinksToThisPage(csv));
		columnDefaults.add(i++, s.getInternalLinksOnThisPage(csv));
		columnDefaults.add(i++, s.getExternalLinksOnThisPage(csv));
		columnDefaults.add(i++, s.getDepthFromDomainRoot(csv));
		columnDefaults.add(i++, s.getFoundAtURL(csv));
		columnDefaults.add(i++, s.getFirstFoundAnchorTextToThisURL(csv));
		columnDefaults.add(i++, s.getRelevantImages(csv));
		columnDefaults.add(i++, s.getAdScripts(csv));
		columnDefaults.add(i++, s.getMetaRobotsIndex(csv));
		columnDefaults.add(i++, s.getMetaRobotsFollow(csv));
		columnDefaults.add(i++, s.getRelNofollow(csv));
		columnDefaults.add(i++, s.getResponseTime(csv));
		columnDefaults.add(i++, s.getTitle(csv));
		columnDefaults.add(i++, s.getMetaDescription(csv));
		columnDefaults.add(i++, s.getH1(csv));
		columnDefaults.add(i++, s.getH2(csv));
		columnDefaults.add(i++, s.getH3(csv));
		columnDefaults.add(i++, s.getContentHashcode(csv));
		columnDefaults.add(i++, s.getCanonicalTag(csv));
		columnDefaults.add(i++, s.getRedirectedToURL(csv));
		columnDefaults.add(i++, s.getPageSize(csv));
		columnDefaults.add(i++, s.getTrailingSlashIssue(csv));
		columnDefaults.add(i++, s.getGzipIssue(csv));
		columnDefaults.add(i++, s.getTimeout(csv));

		switch (this)
		{
		case All:
			columns = columnDefaults;
			break;
		case CLIENTERRORS:
			columns = columnDefaults;
			break;
		case CANONICALISSUE:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getCanonicalTag(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			break;
		case CANONICALTOSOURCE:			
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getCanonicalTag(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			break;
		case DUPLICATECONTENT:			
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getContentHashcode(csv));
			columns.add(i++, s.getTitle(csv));
			columns.add(i++, s.getMetaDescription(csv));
			columns.add(i++, s.getH1(csv));
			columns.add(i++, s.getH2(csv));
			columns.add(i++, s.getH3(csv));
			columns.add(i++, s.getReadingLevel(csv));
			columns.add(i++, s.getVarietyTopicScore(csv));
			columns.add(i++, s.getPageRank(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			break;
		case DUPLICATEH1:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getH1(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			break;
		case DUPLICATEMETA:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getMetaDescription(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			break;
		case DUPLICATETITLE:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getTitle(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			break;
		case EXTERNALLINKS:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getExternalHostName(csv));
			columns.add(i++, s.getFirstFoundAnchorTextToThisURL(csv));
			columns.add(i++, s.getRelNofollow(csv));
			break;
		case HEADLINESORDER:
			columns = columnDefaults;
			break;
		case INTERNALTARGETS:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getPageRank(csv));
			columns.add(i++, s.getNofollowLinksToThisPage(csv));
			columns.add(i++, s.getFollowLinksToThisPage(csv));
			columns.add(i++, s.getInternalLinksOnThisPage(csv));
			columns.add(i++, s.getExternalLinksOnThisPage(csv));
			columns.add(i++, s.getDepthFromDomainRoot(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			break;
		case LONGMETADESCRIPTION:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getMetaDescription(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			break;
		case LONGTITLE:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getTitle(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			break;
		case LOWCONTENT:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getContentHashcode(csv));
			columns.add(i++, s.getTitle(csv));
			columns.add(i++, s.getMetaDescription(csv));
			columns.add(i++, s.getH1(csv));
			columns.add(i++, s.getH2(csv));
			columns.add(i++, s.getH3(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			break;
		case MISSINGH1:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getH1(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			break;
		case MISSINGMETADESCRIPTION:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getMetaDescription(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			break;
		case MISSINGTITLE:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getTitle(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			break;
		case PAGESIZE:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getPageSize(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			break;
		case REDIRECTIONS:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getRedirectedToURL(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getFirstFoundAnchorTextToThisURL(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			break;
		case ROBOTSFOLLOW:
			columns = columnDefaults;
			break;
		case ROBOTSINDEX:
			columns = columnDefaults;
			break;
		case ROBOTSNOFOLLOW:
			columns = columnDefaults;
			break;
		case ROBOTSNOINDEX:
			columns = columnDefaults;
			break;
		case SERVERERRORS:
			columns = columnDefaults;
			break;
		case TIMEOUTS:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getTimeout(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getNofollowLinksToThisPage(csv));
			columns.add(i++, s.getFollowLinksToThisPage(csv));
			columns.add(i++, s.getDepthFromDomainRoot(csv));
			columns.add(i++, s.getFirstFoundAnchorTextToThisURL(csv));
			break;
		case QUALITYSCORE:
			columns = columnDefaults;
			break;
		case DIFFERENTURLSAMEANCHOR:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getFirstFoundAnchorTextToThisURL(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getCanonicalTag(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			break;
		case KEYWORDS:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getTopicKeywordOneTerm(csv));
			columns.add(i++, s.getTopicKeywordTwoTerms(csv));
			columns.add(i++, s.getTopicKeywordThreeTerms(csv));
			break;
		case TRAILINGSLASHISSUES:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			columns.add(i++, s.getContentHashcode(csv));
			break;
		case GZIPISSUES:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getFoundAtURL(csv));
			columns.add(i++, s.getQualityscore(csv));
			columns.add(i++, s.getHttpStatusCode(csv));
			break;
		case KEYWORDORIENTATIONSHORTTERM:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getTopicKeywordOneTerm(csv));
			break;
		case KEYWORDORIENTATIONTWOTERMS:
			columns = new ArrayList<String>();
			i = 0;
			columns.add(i++, s.getURLName(csv));
			columns.add(i++, s.getTopicKeywordTwoTerms(csv));
			break;
		default:
			columns = columnDefaults;
			break;
		}
		return columns;
	}
	
	public static Collection<IssueType> getSortedValuesByLocalizedText()
	{
		SortedMap<String, IssueType> map = new TreeMap<String, IssueType>();
		for (IssueType i : IssueType.values())
		{
			map.put(i.getLocalizedUIText(), i);
		}
		return map.values();
	}

	@Override
	public String toString()
	{
		return name().toLowerCase();
	}

}
