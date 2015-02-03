package de.solidsearch.bot.data.enumerations;

import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.persistence.Entity;

import de.solidsearch.bot.i18n.USMessages;

@Entity
public enum ProjectSummaryInfo
{	
	NO_INFORMATION_OR_ADVICES, FOUND_MANY_NOINDEX_PAGES, UNLIMITED_PAGINATION_PARAMETER, DUPLICATE_CONTENT_WITH_HTTPS, SIGNIFICANT_CHANGES, LIST_CHANGES;

	public String getLocalizedUIText(Locale locale)
	{
		ResourceBundle i18n = ResourceBundle.getBundle(USMessages.class.getName(), locale);
		String text = "-";
		switch (this)
		{
		case NO_INFORMATION_OR_ADVICES:
			text = i18n.getString(USMessages.NoInformationOrAdvices);
			break;
		case FOUND_MANY_NOINDEX_PAGES:
			text = "1";
			break;
		case UNLIMITED_PAGINATION_PARAMETER:
			text = "2";
			break;
		case DUPLICATE_CONTENT_WITH_HTTPS:
			text = "3";
			break;
		case SIGNIFICANT_CHANGES:
			text = i18n.getString(USMessages.SignificantChanges);
			break;
		case LIST_CHANGES:
			text = i18n.getString(USMessages.SignificantChanges);
			break;
		default:
			text = "-";
			break;
		}
		return text;
	}

	public String getLocalizedUITextDescription(Locale locale)
	{
		//ResourceBundle i18n = ResourceBundle.getBundle(USMessages.class.getName(), locale);

		String text = "-";
		switch (this)
		{
		case NO_INFORMATION_OR_ADVICES:
			text = "no description available";
			break;
		case FOUND_MANY_NOINDEX_PAGES:
			text = "no description available";
			break;
		case UNLIMITED_PAGINATION_PARAMETER:
			text = "no description available";
			break;
		case DUPLICATE_CONTENT_WITH_HTTPS:
			text = "no description available";
			break;
		case SIGNIFICANT_CHANGES:
			text = "no description available";
			break;

		default:
			text = "-";
			break;
		}
		return text;
	}

	public int getProjectSummaryInfoCode()
	{
		int code = 0;
		switch (this)
		{
		case NO_INFORMATION_OR_ADVICES:
			code = 1;
			break;
		case FOUND_MANY_NOINDEX_PAGES:
			code = 2;
			break;
		case UNLIMITED_PAGINATION_PARAMETER:
			code = 3;
			break;
		case DUPLICATE_CONTENT_WITH_HTTPS:
			code = 4;
			break;
		case SIGNIFICANT_CHANGES:
			code = 5;
			break;
		default:
			code = 0;
			break;
		}
		return code;
	}
	
	public static Collection<ProjectSummaryInfo> getSortedValuesByLocalizedText(Locale locale)
	{
		SortedMap<String, ProjectSummaryInfo> map = new TreeMap<String, ProjectSummaryInfo>();
		for (ProjectSummaryInfo i : ProjectSummaryInfo.values())
		{
			map.put(i.getLocalizedUIText(locale), i);
		}
		return map.values();
	}
	
	public static String getLocalizedUITextByMessageCode(int code, Locale locale)
	{
		for (ProjectSummaryInfo i : ProjectSummaryInfo.values())
		{
			if (i.getProjectSummaryInfoCode() == code)
			{
				return i.getLocalizedUIText(locale);
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
