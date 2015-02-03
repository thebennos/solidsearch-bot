package de.solidsearch.bot.data.enumerations;

import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;

import com.vaadin.ui.UI;

import de.qualitywatch.Qualitywatch;
import de.solidsearch.bot.i18n.USMessages;
import de.solidsearch.bot.utils.BotConfig;

public enum ProjectInfo
{
	CRAWLINGISRUNNING, BOTISBLOCKED, ROBOTSTXTNOTFOUND, NOTENOUGHTURLS, SITEISVERYSLOW, INVALIDPROJECTURL,WAITFORSHUTDOWN,COULDNOTFINDVERIFICATIONFILE,MONITORINGWASSTOPPED,COULDNOTRESOLVEHOSTNAME,PROJECTERROR;

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
		case CRAWLINGISRUNNING:
			text = i18n.getString(USMessages.CrawlingIsRunning);
			break;
		case BOTISBLOCKED:
			text = i18n.getString(USMessages.BotisBlocked);
			break;
		case ROBOTSTXTNOTFOUND:
			text = i18n.getString(USMessages.RobotsTxtNotFound);
			break;
		case NOTENOUGHTURLS:
			text = i18n.getString(USMessages.NotEnougthURLs);
			break;
		case SITEISVERYSLOW:
			text = i18n.getString(USMessages.SiteIsVerySlow);
			break;
		case INVALIDPROJECTURL:
			text = i18n.getString(USMessages.InvalidProjectURL);
			break;
		case WAITFORSHUTDOWN:
			text = i18n.getString(USMessages.WaitForShutdown);
			break;
		case COULDNOTFINDVERIFICATIONFILE:
			text = i18n.getString(USMessages.CouldNotFindVerificationFile);
			break;
		case MONITORINGWASSTOPPED:
			text = i18n.getString(USMessages.MonitoringWasStopped);
			break;
		case COULDNOTRESOLVEHOSTNAME:
			text = i18n.getString(USMessages.CouldNotResolveHostname);
			break;
		case PROJECTERROR:
			text = i18n.getString(USMessages.ProjectError);
			break;
			
		default:
			text = "-";
			break;
		}
		return text;
	}
	
	public int getInfoMessageCode()
	{
		int code = 0;
		switch (this)
		{
		case CRAWLINGISRUNNING:
			code = 1;
			break;
		case BOTISBLOCKED:
			code = 2;
			break;
		case ROBOTSTXTNOTFOUND:
			code = 3;
			break;
		case NOTENOUGHTURLS:
			code = 4;
			break;
		case SITEISVERYSLOW:
			code = 5;
			break;
		case INVALIDPROJECTURL:
			code = 6;
			break;
		case WAITFORSHUTDOWN:
			code = 7;
			break;
		case COULDNOTFINDVERIFICATIONFILE:
			code = 8;
			break;
		case MONITORINGWASSTOPPED:
			code = 10;
			break;
		case COULDNOTRESOLVEHOSTNAME:
			code = 11;
			break;
		case PROJECTERROR:
			code = 12;
			break;	
		default:
			code = 0;
			
			break;
		}
		return code;
	}

	public static Collection<ProjectInfo> getSortedValuesByLocalizedText()
	{
		SortedMap<String, ProjectInfo> map = new TreeMap<String, ProjectInfo>();
		for (ProjectInfo i : ProjectInfo.values())
		{
			map.put(i.getLocalizedUIText(), i);
		}
		return map.values();
	}
	
	public static String getLocalizedUITextByMessageCode(int code)
	{
		for (ProjectInfo i : ProjectInfo.values())
		{
			if (i.getInfoMessageCode() == code)
			{
				return i.getLocalizedUIText();
			}
			
		}
		return "not available";
	}

	@Override
	public String toString()
	{
		return name().toLowerCase();
	}

}
