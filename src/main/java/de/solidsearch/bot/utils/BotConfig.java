package de.solidsearch.bot.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration("BotConfig")
@PropertySource("classpath:bot.properties")
public class BotConfig
{	
	@Value("${application.detailedCrawlingDataByProject}")
	public int detailedCrawlingDataByProject;
	@Value("${application.detailedCrawlingDataByRemoteProject}")
	public int detailedCrawlingDataByRemoteProject;
	@Value("${application.maxparallelcrawlings}")
	public int maxParallelCrawlings;
		
	@Value("${application.remoteKey}")
	public String REMOTEKEY;
		
	@Value("${admin.user}")
	public String ADMIN_USER;
	@Value("${admin.password}")
	public String ADMIN_PASSWORD;
	
	@Value("${proxy.host}")
	public String PROXYHOST;
	@Value("${proxy.port}")
	public String PROXYPORT;
	@Value("${proxy.user}")
	public String PROXYUSER;
	@Value("${proxy.password}")
	public String PROXYPASSWORD;
	@Value("${proxy.ntlm.workstation}")
	public String PROXYNTLMWWORSTATION;
	@Value("${proxy.ntlm.domain}")
	public String PROXYNTLMDOMAIN;
	
	public int MAX_URLS = 50000000;
	
	public static boolean allProjectsPauseSignal = false;
	
	public static boolean isAllProjectsPauseSignal()
	{
		return allProjectsPauseSignal;
	}
	public static void setAllProjectsPauseSignal(boolean allProjectsPauseSignal)
	{
		BotConfig.allProjectsPauseSignal = allProjectsPauseSignal;
	}
	
	private static Integer plugInStatus = null;
	
	private static boolean pluginAvailable = false;
	
	public static boolean isPluginAvailable()
	{
		if (plugInStatus == null)
		{
			try
			{
				Class.forName("de.qualitywatch.Qualitywatch");
				pluginAvailable = true;
			}
			catch (Exception e)
			{
				pluginAvailable = false;
			}
			plugInStatus = new Integer(1);
		}
		return pluginAvailable;	
	}
	
}
