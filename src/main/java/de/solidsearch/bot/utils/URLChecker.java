package de.solidsearch.bot.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class URLChecker
{
	
	private static final Logger logger = Logger.getLogger(URLChecker.class.getCanonicalName());
	
	private boolean ignoreImages;
	private boolean ignoreCSS;
	private boolean ignoreJS;;
	private boolean ignoreRobotsTxt;
	private String robotsRegex = null;
	private boolean allowAll = true;
	private Pattern filterPattern = null;
	
	public URLChecker(boolean ignoreImages,boolean ignoreCSS, boolean ignoreJS, boolean ignoreRobotsTxt, String robotsRegex)
	{
		this.ignoreImages = ignoreImages;
		this.ignoreCSS = ignoreCSS;
		this.ignoreJS = ignoreJS;
		this.ignoreRobotsTxt = ignoreRobotsTxt;
		if (robotsRegex == null && ignoreRobotsTxt == false) throw new IllegalStateException("robotsRegex should not be null");
		this.robotsRegex= robotsRegex;
		filterPattern = generateFilter();
	}
	
	public boolean isURLAllowed(String link)
	{
		if (allowAll) return true;
		Matcher m = filterPattern.matcher(link.toLowerCase());
		return !m.find();
	}
	
	private Pattern generateFilter()
	{
		// initial ignore some docs and specials
		String filterRegex = "(\\.mp3|\\.wav|\\.pdf|\\.doc|\\.docx|\\.xls|\\.xlsx|\\.xml|\\.zip|mailto:";
		
		if (!ignoreImages && !ignoreCSS && !ignoreJS)
		{
			allowAll = true;
		}
		else {allowAll = false;}
		
		if (ignoreImages)
		{
			filterRegex = filterRegex + "|\\.jpg|\\.png|\\.gif|\\.bmp|\\.ico";
		}
		if (ignoreCSS)
		{
			filterRegex = filterRegex + "|\\.css";
		}
		if (ignoreJS)
		{
			filterRegex = filterRegex + "|\\.js";
		}
		
		if (robotsRegex.length() != 0 && ignoreRobotsTxt == false) 
		{
			if (robotsRegex.startsWith("^(.(?!"))
			{
				// all is per default disallowed in robots.txt
				filterRegex = robotsRegex;
			}
			else
			{
				// all is per default allowed in robots.txt
				filterRegex = filterRegex + robotsRegex;
				
			}
		}
		if (!robotsRegex.startsWith("^(.(?!"))
			filterRegex = filterRegex + ")";
		
		logger.info("FilterRegex : " + filterRegex);
		return Pattern.compile(filterRegex);
	}
}
