package de.solidsearch.bot.data;

import java.util.Locale;
import java.util.ResourceBundle;

import de.solidsearch.bot.i18n.USMessages;

public class URLSQLColumn
{
	ResourceBundle i18n;
	StringBuffer buf = new StringBuffer();
	
	public URLSQLColumn(Locale locale)
	{
		i18n = ResourceBundle.getBundle(USMessages.class.getName(), locale);
	}
	public String getURLName(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("URLName as \"").append(i18n.getString(USMessages.URL_table)).append("\"");
		else
			buf.append("URLName");
		return buf.toString().toLowerCase();
	}
	public String getQualityscore(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("qualityScore as \"").append(i18n.getString(USMessages.Solidsearchscore)).append("\"");
		else
			buf.append("qualityScore");
		return buf.toString().toLowerCase();
	}
	public String getHttpStatusCode(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("httpStatusCode as \"").append(i18n.getString(USMessages.HTTPStatuscode)).append("\"");
		else
			buf.append("httpStatusCode");
		return buf.toString().toLowerCase();
	}
	public String getMetaRobotsIndex(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("metaRobotsIndex as \"").append(i18n.getString(USMessages.MetaRobotsIndex)).append("\"");
		else
			buf.append("metaRobotsIndex");
		return buf.toString().toLowerCase();
	}
	public String getMetaRobotsFollow(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("metaRobotsFollow as \"").append(i18n.getString(USMessages.MetaRobotsFollow)).append("\"");
		else
			buf.append("metaRobotsFollow");
		return buf.toString().toLowerCase();
	}
	public String getInternalLinksOnThisPage(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("internalLinksOnThisPage as \"").append(i18n.getString(USMessages.InternalLinksOnThisPage)).append("\"");
		else
			buf.append("internalLinksOnThisPage");
		return buf.toString().toLowerCase();
	}
	public String getExternalLinksOnThisPage(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("externalLinksOnThisPage as \"").append(i18n.getString(USMessages.ExternalLinksOnThisPage)).append("\"");
		else
			buf.append("externalLinksOnThisPage");
		return buf.toString().toLowerCase();
	}
	public String getDepthFromDomainRoot(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("depthFromDomainRoot as \"").append(i18n.getString(USMessages.DepthFromDomainRoot)).append("\"");
		else
			buf.append("depthFromDomainRoot");
		return buf.toString().toLowerCase();
	}
	public String getResponseTime(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("responseTime as \"").append(i18n.getString(USMessages.ResponseTime_table)).append("\"");
		else
			buf.append("responseTime");
		return buf.toString().toLowerCase();
	}
	public String getCanonicalTag(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("canonicalTag as \"").append(i18n.getString(USMessages.CanonicalTag)).append("\"");
		else
			buf.append("canonicalTag");
		return buf.toString().toLowerCase();
	}
	public String getFoundAtURL(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("foundAtURL as \"").append(i18n.getString(USMessages.FoundAtURL)).append("\"");
		else
			buf.append("foundAtURL");
		return buf.toString().toLowerCase();
	}
	public String getTitle(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("title as \"").append(i18n.getString(USMessages.Title)).append("\"");
		else
			buf.append("title");
		return buf.toString().toLowerCase();
	}
	public String getMetaDescription(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("metaDescription as \"").append(i18n.getString(USMessages.MetaDescription)).append("\"");
		else
			buf.append("metaDescription");
		return buf.toString().toLowerCase();
	}
	public String getH1(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("h1 as \"").append(i18n.getString(USMessages.H1)).append("\"");
		else
			buf.append("h1");
		return buf.toString().toLowerCase();
	}
	public String getH2(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("h2 as \"").append(i18n.getString(USMessages.H2)).append("\"");
		else
			buf.append("h2");
		return buf.toString().toLowerCase();
	}
	public String getH3(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("h3 as \"").append(i18n.getString(USMessages.H3)).append("\"");
		else
			buf.append("h3");
		return buf.toString().toLowerCase();
	}
	public String getTimeout(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("timeout as \"").append(i18n.getString(USMessages.Timeout)).append("\"");
		else
			buf.append("timeout");
		return buf.toString().toLowerCase();
	}
	public String getRedirectedToURL(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("redirectedToURL as \"").append(i18n.getString(USMessages.RedirectedToURL)).append("\"");
		else
			buf.append("redirectedToURL");
		return buf.toString().toLowerCase();
	}
	public String getContentHashcode(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("contentHashcode as \"").append(i18n.getString(USMessages.ContentHashcode)).append("\"");
		else
			buf.append("contentHashcode");
		return buf.toString().toLowerCase();
	}
	public String getPageSize(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("pageSize as \"").append(i18n.getString(USMessages.PageSize)).append("\"");
		else
			buf.append("pageSize");
		return buf.toString().toLowerCase();
	}
	public String getNofollowLinksToThisPage(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("nofollowLinksToThisPage as \"").append(i18n.getString(USMessages.NofollowLinkToThis)).append("\"");
		else
			buf.append("nofollowLinksToThisPage");
		return buf.toString().toLowerCase();
	}
	public String getFollowLinksToThisPage(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("followLinksToThisPage as \"").append(i18n.getString(USMessages.FollowLinkToThis)).append("\"");
		else
			buf.append("followLinksToThisPage");
		return buf.toString().toLowerCase();
	}
	public String getFirstFoundAnchorTextToThisURL(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("firstFoundAnchorTextToThisURL as \"").append(i18n.getString(USMessages.FirstAnchor)).append("\"");
		else
			buf.append("firstFoundAnchorTextToThisURL");
		return buf.toString().toLowerCase();
	}
	public String getRelNofollow(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("relNofollow as \"").append(i18n.getString(USMessages.Nofollow)).append("\"");
		else
			buf.append("relNofollow");
		return buf.toString().toLowerCase();
	}
	public String getExternalHostName(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("externalHostName as \"").append(i18n.getString(USMessages.Host)).append("\"");
		else
			buf.append("externalHostName");
		return buf.toString().toLowerCase();
	}
	
	public String getNewPrice(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("newPrice as \"").append("neu_preis").append("\"");
		else
			buf.append("newPrice");
		return buf.toString().toLowerCase();
	}
	public String getOldPrice(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("oldPrice as \"").append("alt_preis").append("\"");
		else
			buf.append("oldPrice");
		return buf.toString().toLowerCase();
	}
	public String getColor(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("color as \"").append("farbe").append("\"");
		else
			buf.append("color");
		return buf.toString().toLowerCase();
	}
	public String getRank(boolean csv)
	{
		buf.setLength(0);
		buf.append("rank");
		return buf.toString().toLowerCase();
	}
	public String getVarietyTopicScore(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("varietyTopicScore as \"").append(i18n.getString(USMessages.VarietyTopicScore)).append("\"");
		else
			buf.append("varietyTopicScore");
		return buf.toString().toLowerCase();
	}
	public String getReadingLevel(boolean csv)
	{
		buf.setLength(0);
		if (csv)
			buf.append("readingLevel as \"").append(i18n.getString(USMessages.ReadingLevel)).append("\"");
		else
			buf.append("readingLevel");
		return buf.toString().toLowerCase();
	}
	
	public String getFacebookLikes(boolean csv)
	{
		buf.setLength(0);
		return "todo implement getFacebookLikes() ";
	}
	public String getFacebookShares(boolean csv)
	{
		buf.setLength(0);
		return "todo implement getFacebookShares()";
	}
	
	public String getPageRank(boolean csv)
	{
		buf.setLength(0);
		return "pagerank";
	}
	
	public String getAdScripts(boolean csv)
	{
		buf.setLength(0);
		return "adscripts";
	}
	
	public String getRelevantImages(boolean csv)
	{
		buf.setLength(0);
		return "relevantimages";
	}
	
	public String getTrailingSlashIssue(boolean csv)
	{
		buf.setLength(0);
		return "trailingslashissue";
	}			
	
	public String getGzipIssue(boolean csv)
	{
		buf.setLength(0);
		return "gzipissue";
	}
	
	public String getTopicKeywordOneTerm(boolean csv)
	{
		buf.setLength(0);
		return "topickeywordoneterm";
	}
	
	public String getTopicKeywordTwoTerms(boolean csv)
	{
		buf.setLength(0);
		return "topickeywordtwoterms";
	}
	
	public String getTopicKeywordThreeTerms(boolean csv)
	{
		buf.setLength(0);
		return "topickeywordthreeterms";
	}
}
