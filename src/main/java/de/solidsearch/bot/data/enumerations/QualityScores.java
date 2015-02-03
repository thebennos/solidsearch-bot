package de.solidsearch.bot.data.enumerations;

import java.util.Locale;
import java.util.ResourceBundle;

import com.vaadin.ui.UI;

import de.qualitywatch.Qualitywatch;
import de.solidsearch.bot.i18n.USMessages;
import de.solidsearch.bot.utils.BotConfig;

public enum QualityScores
{
	LEVEL4_FROM_ROOT, LEVEL3_FROM_ROOT, LEVEL2_FROM_ROOT, LEVEL1_FROM_ROOT, EXTERNALLINKS_BELOW_AVG, FOLLOWLINKSTOHISPAGE_NEAR_MAX, FOLLOWLINKSTOHISPAGE_MORE_AVG, DUPLICATECONTENT, DUPLICATETITLE, DUPLICATEH1, LOWCONTENT, LONGMETADESCRIPTION, MISSINGMETADESCRIPTION, LONGTITLE, MISSINGTITLE, MISSINGH1, CANONICALISSUE, HEADLINESORDER, DIFFERENTURLSAMEANCHOR, GZIPISSUE;

	public short getScore()
	{
		short score = 0;
		switch (this)
		{
		case LEVEL4_FROM_ROOT:
			score = 3;
			break;
		case LEVEL3_FROM_ROOT:
			score = 3;
			break;
		case LEVEL2_FROM_ROOT:
			score = 2;
			break;
		case LEVEL1_FROM_ROOT:
			score = 1;
			break;
		case EXTERNALLINKS_BELOW_AVG:
			score = 8;
			break;
		case FOLLOWLINKSTOHISPAGE_NEAR_MAX:
			score = 3;
			break;
		case FOLLOWLINKSTOHISPAGE_MORE_AVG:
			score = 8;
			break;
		case CANONICALISSUE:
			score = 1;
			break;
		case DUPLICATECONTENT:
			score = 11;
			break;
		case DUPLICATEH1:
			score = 8;
			break;
		case DUPLICATETITLE:
			score = 9;
			break;
		case HEADLINESORDER:
			score = 1;
			break;
		case LONGMETADESCRIPTION:
			score = 1;
			break;
		case LONGTITLE:
			score = 1;
			break;
		case LOWCONTENT:
			score = 10;
			break;
		case MISSINGH1:
			score = 5;
			break;
		case MISSINGMETADESCRIPTION:
			score = 7;
			break;
		case MISSINGTITLE:
			score = 10;
			break;
		case DIFFERENTURLSAMEANCHOR:
			score = 2;
			break;
		case GZIPISSUE:
			score = 6;
			break;
		default:
			score = 0;
			break;
		}
		return score;
	}

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
		case LEVEL4_FROM_ROOT:
			text = i18n.getString(USMessages.DepthFromDomainRoot);
			break;
		case LEVEL3_FROM_ROOT:
			text = i18n.getString(USMessages.DepthFromDomainRoot);
			break;
		case LEVEL2_FROM_ROOT:
			text = i18n.getString(USMessages.DepthFromDomainRoot);
			break;
		case LEVEL1_FROM_ROOT:
			text = i18n.getString(USMessages.DepthFromDomainRoot);
			break;
		case EXTERNALLINKS_BELOW_AVG:
			text = i18n.getString(USMessages.ExternalLinks);
			break;
		case FOLLOWLINKSTOHISPAGE_NEAR_MAX:
			text = i18n.getString(USMessages.InternalLinks);
			break;
		case FOLLOWLINKSTOHISPAGE_MORE_AVG:
			text = i18n.getString(USMessages.InternalLinks);
			break;
		case CANONICALISSUE:
			text = i18n.getString(USMessages.CanonicalIssues);
			break;
		case DUPLICATECONTENT:
			text = i18n.getString(USMessages.DuplicateContent);
			break;
		case DUPLICATEH1:
			text = i18n.getString(USMessages.DuplicateH1);
			break;
		case DUPLICATETITLE:
			text = i18n.getString(USMessages.DuplicateTitle);
			break;
		case HEADLINESORDER:
			text = i18n.getString(USMessages.HeadlinesNotInOrder);
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
		case DIFFERENTURLSAMEANCHOR:
			text = i18n.getString(USMessages.DifferentURLSameAnchor);
			break;
		case GZIPISSUE:
			text = i18n.getString(USMessages.GzipIssues);
			break;
		default:
			text = "-";
			break;
		}
		return text;
	}

	@Override
	public String toString()
	{
		return name().toLowerCase();
	}

}
