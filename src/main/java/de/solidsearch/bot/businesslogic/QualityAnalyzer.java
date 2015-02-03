package de.solidsearch.bot.businesslogic;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.vaadin.ui.UI;

import de.qualitywatch.Qualitywatch;
import de.solidsearch.bot.data.ProjectSummary;
import de.solidsearch.bot.data.URL;
import de.solidsearch.bot.data.enumerations.QualityScores;
import de.solidsearch.bot.i18n.USMessages;
import de.solidsearch.bot.utils.BotConfig;

public class QualityAnalyzer
{

	public Map<String, Short> getScoresForURL(ProjectSummary projectSum, URL url)
	{
		HashMap<String, Short> scores = new HashMap<String, Short>();
		
		Short l4 = 0;
		Short l3 = 0;
		Short l2 = 0;
		Short l1 = 0;
		Short e = 0;
		Short fa = 0;
		Short fm = 0;
		Short ci = 0;
		Short dc = 0;
		Short dh = 0;
		Short dt = 0;
		Short ho = 0;
		Short lc = 0;
		Short sh = 0;
		Short lm = 0;
		Short lt = 0;
		Short sm = 0;
		Short mt = 0;
		Short da = 0;
		
		
		if (url.getDepthFromDomainRoot() <= 4)
		{
			l4 = QualityScores.LEVEL4_FROM_ROOT.getScore();
		}
		if (url.getDepthFromDomainRoot() <= 3)
		{
			l3 = QualityScores.LEVEL3_FROM_ROOT.getScore();
		}
		if (url.getDepthFromDomainRoot() <= 2)
		{
			l2 = QualityScores.LEVEL2_FROM_ROOT.getScore();
		}	
		if (url.getDepthFromDomainRoot() <= 1)
		{
			l1 = QualityScores.LEVEL1_FROM_ROOT.getScore();
		}
		scores.put(QualityScores.LEVEL4_FROM_ROOT.getLocalizedUIText(), (short)(l4 + l3 + l2 + l1));
		
		if (url.getExternalLinksDifferentDomainsOnThisPage() < projectSum.getExternalLinksThreshold())
		{
			e = QualityScores.EXTERNALLINKS_BELOW_AVG.getScore();
		}
		scores.put(QualityScores.EXTERNALLINKS_BELOW_AVG.getLocalizedUIText(), e);
		
		if (url.getFollowLinksToThisPage() > projectSum.getAvgInternalFollowLinks())
		{
			fa = QualityScores.FOLLOWLINKSTOHISPAGE_MORE_AVG.getScore();
		}
		if (url.getFollowLinksToThisPage() > projectSum.getMaxInternalLinksThreshold())
		{
			fm = QualityScores.FOLLOWLINKSTOHISPAGE_NEAR_MAX.getScore();
		}
		scores.put(QualityScores.FOLLOWLINKSTOHISPAGE_NEAR_MAX.getLocalizedUIText(), (short)(fm + fa));
		
		if (!url.isCanonicalTagIssue())
		{
			ci = QualityScores.CANONICALISSUE.getScore();
		}
		scores.put(QualityScores.CANONICALISSUE.getLocalizedUIText(), ci);
		if (!url.isDuplicateContent())
		{
			dc = QualityScores.DUPLICATECONTENT.getScore();
		}
		scores.put(QualityScores.DUPLICATECONTENT.getLocalizedUIText(), dc);
		if (!url.isDuplicateH1())
		{
			dh = QualityScores.DUPLICATEH1.getScore();
		}
		scores.put(QualityScores.DUPLICATEH1.getLocalizedUIText(), dh);
		if (!url.isDuplicateTitle())
		{
			dt = QualityScores.DUPLICATETITLE.getScore();
		}
		scores.put(QualityScores.DUPLICATETITLE.getLocalizedUIText(), dt);
		if (!url.isHeadlinesNotInRightOrder())
		{
			ho = QualityScores.HEADLINESORDER.getScore();
		}
		scores.put(QualityScores.HEADLINESORDER.getLocalizedUIText(), ho);
		if (url.getMetaDescription().length() <= 160)
		{
			lm = QualityScores.LONGMETADESCRIPTION.getScore();
		}
		scores.put(QualityScores.LONGMETADESCRIPTION.getLocalizedUIText(), lm);
		if (url.getTitle().length() <= 57)
		{
			lt = QualityScores.LONGTITLE.getScore();
		}
		scores.put(QualityScores.LONGTITLE.getLocalizedUIText(), lt);
		if (url.getContentHashcode() != null)
		{
			lc = QualityScores.LOWCONTENT.getScore();
		}
		scores.put(QualityScores.LOWCONTENT.getLocalizedUIText(), lc);
		if (url.getH1().length() > 2)
		{
			sh = QualityScores.MISSINGH1.getScore();
		}
		scores.put(QualityScores.MISSINGH1.getLocalizedUIText(), sh);
		if (url.getMetaDescription().length() >= 3)
		{
			sm = QualityScores.MISSINGMETADESCRIPTION.getScore();
		}
		scores.put(QualityScores.MISSINGMETADESCRIPTION.getLocalizedUIText(), sm);
		if (url.getTitle().length() >= 3)
		{
			mt = QualityScores.MISSINGTITLE.getScore();
		}
		scores.put(QualityScores.MISSINGTITLE.getLocalizedUIText(), mt);
		if (!url.isDifferentURLSameAnchor())
		{
			da = QualityScores.DIFFERENTURLSAMEANCHOR.getScore();
		}
		scores.put(QualityScores.DIFFERENTURLSAMEANCHOR.getLocalizedUIText(), da);
		
		Integer sum = l4 + l3 + l2 + l1 + e + fa + fm + ci + dc + dh + dt + ho + lc + sh + lm + lt + sm + mt + da;
		
		ResourceBundle i18n;
		
		if (BotConfig.isPluginAvailable())
		{
			i18n = ResourceBundle.getBundle(USMessages.class.getName(), ((Qualitywatch) UI.getCurrent()).getLocale());
		}
		else
		{
			i18n = ResourceBundle.getBundle(USMessages.class.getName(), Locale.ENGLISH);
		}
		
		scores.put(i18n.getString(USMessages.Total), sum.shortValue());
		
		return scores;
	}
	
}
