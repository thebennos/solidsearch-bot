package de.solidsearch.bot.businesslogic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.log4j.Logger;

import de.solidsearch.bot.data.Project;
import de.solidsearch.bot.utils.Base64;

public class DomainAnalyzer
{
	private static final Logger logger = Logger.getLogger(DomainAnalyzer.class.getCanonicalName());
	private Project project;
	private ProjectJobWorker projectWorker;

	private ArrayList<String> disallowedURLs = new ArrayList<String>();
	private ArrayList<String> allowedURLs = new ArrayList<String>();

	public DomainAnalyzer(ProjectJobWorker projectWorker)
	{
		this.projectWorker = projectWorker;
		this.project = projectWorker.getProject();
	}

	/**
	 * Method read robots.txt of given root domain and returns robots filter regex
	 * 
	 * @param rootDomain
	 * @return robots filter regex OR null we are blocked
	 * @throws FileNotFoundException
	 *             if robots not exists
	 */
	public String readRobotsTxtAndGetRegex(String rootDomain) throws FileNotFoundException
	{
		int responseCode = 0;
		HttpGet httpget = null;
		HttpEntity entity = null;
		StringBuffer url = new StringBuffer(rootDomain);

		if (url.toString().endsWith("/"))
			url.append("robots.txt");
		else
			url.append("/robots.txt");

		String regEx = "";
		try
		{
			httpget = new HttpGet(url.toString());
			httpget.setHeader("User-Agent", project.getBotUserAgent());
			httpget.setHeader("Accept-Encoding", "gzip");
			if (project.isUseBasicAuth())
				httpget.setHeader("Authorization", "Basic " + Base64.encodeToString((project.getBasicAuthUser() + ":" + project.getBasicAuthPassword()).getBytes(), false));
			if (!project.getCookieName().isEmpty())
			{
				httpget.setHeader("Cookie", project.getCookieName() + "=" + project.getCookieValue());
			}
			HttpResponse response = projectWorker.getHttpClient().execute(httpget, new BasicHttpContext());
			entity = response.getEntity();
			responseCode = response.getStatusLine().getStatusCode();

			InputStreamReader ir;
			// check if GZIP is enabled
			if (entity.getContentEncoding() != null && entity.getContentEncoding().getValue().equalsIgnoreCase("gzip"))
			{
				// if so, do the right encoding
				ir = new InputStreamReader(new GZIPInputStream(entity.getContent()), "UTF-8");
			}
			else
			{
				ir = new InputStreamReader(entity.getContent(), "UTF-8");
			}

			regEx = getRobotsTxtFilter(ir);
		}
		catch (Exception e)
		{
			logger.error("Exception in readRobotsTxtAndAddToConfig() : ", e);

			if (httpget != null)
			{
				httpget.abort();
			}
		}
		finally
		{

			if (entity != null)
			{
				try
				{
					entity.getContent().close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

		}

		if (responseCode != 200)
			throw new FileNotFoundException();

		if (regEx == null)
			logger.info("There is a site which explicit blocks us: " + rootDomain);

		return regEx;
	}

	/**
	 * Method analyzes robots.txt given in Reader object and generates regex for filtering disallowed URL strings.
	 * 
	 * @param inputReader
	 * @return regex for getting disallowed urls
	 */
	public String getRobotsTxtFilter(Reader inputReader)
	{
		StringBuffer regex = new StringBuffer();

		disallowedURLs.clear();
		allowedURLs.clear();

		boolean allowedMode = false;

		BufferedReader reader = null;

		try
		{
			reader = new BufferedReader(inputReader);
			String lineInLowerCase;

			while ((lineInLowerCase = reader.readLine()) != null)
			{
				lineInLowerCase = lineInLowerCase.toLowerCase().replaceAll("\\s+", "");
				// we only want googlebot or *
				if (checkLineIfUserAgentIsGoogleBotOrAll(lineInLowerCase) || checkLineIfUserAgentIsSolidsearch(lineInLowerCase))
				{
					if (checkIfDissallowedAllIsSetInNextLine(reader))
					{
						allowedMode = true;
					}

					while ((lineInLowerCase = reader.readLine()) != null)
					{
						lineInLowerCase = lineInLowerCase.toLowerCase().replaceAll("\\s+", "");

						if (regex.length() + lineInLowerCase.length() >= 20000)
						{
							logger.error("robots.txt  for project: " + projectWorker.getProject().getRootDomainToCrawl() + " is very long: TODO: bring this warning to UI (reduce ist to max 20000 chars)");
							break;
						}

						if (lineInLowerCase.equalsIgnoreCase("disallow:/"))
						{
							allowedMode = true;
						}
						else if (lineInLowerCase.startsWith("disallow:"))
						{
							String proc = processLine(lineInLowerCase, false);
							if (proc.length() > 0)
								disallowedURLs.add(proc);
						}
						else if (lineInLowerCase.startsWith("allow:"))
						{
							String proc = processLine(lineInLowerCase, true);
							if (proc.length() > 0)
								allowedURLs.add(proc);
						}
						else if (checkLineIfUserAgentIsGoogleBotOrAll(lineInLowerCase))
						{
							continue;
						}
						else if (checkLineIfUserAgentIsSolidsearch(lineInLowerCase))
						{
							if (checkIfDissallowedAllIsSetInNextLine(reader))
							{
								allowedMode = true;
							}
							continue;
						}
						else if (lineInLowerCase.contains("user-agent"))
						{
							break;
						}
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		if (allowedMode)
		{
			if (allowedURLs.size() != 0)
			{
				regex.append("^(.(?!(");
				for (int i = 0; i < allowedURLs.size(); i++)
				{
					regex.append(allowedURLs.get(i));
					if (i < allowedURLs.size() - 1)
						regex.append("|");
					else
						regex.append(")))*$");
				}
			}
			else
			{
				// match all = disallow: /
				regex.append("|.+");
			}
		}
		else
		{
			if (disallowedURLs.size() != 0)
			{
				regex.append("|");
				for (int i = 0; i < disallowedURLs.size(); i++)
				{
					regex.append(disallowedURLs.get(i));
					if (i < disallowedURLs.size() - 1)
						regex.append("|");
					else
						regex.append("");
				}
			}
		}
		logger.info("robots.txt regex for : " + regex.toString());

		if (regex.length() > 3000)
			logger.warn("robots.txt for project : " + projectWorker.getProject().getRootDomainToCrawl() + " is very long: TODO: bring this warning to UI");

		return regex.toString();
	}

	private String processLine(String line, boolean allowLine)
	{
		String processed = "";
		if (allowLine)
			processed = line.substring(6);
		else
		{
			processed = line.substring(9);

			processed = processed.replace("*", ".*");

			if (processed.endsWith("$"))
				processed = processed.substring(0, processed.length() - 1);

			if (processed.contains("?"))
			{
				processed = processed.replace("?", "\\?");

			}
		}
		return processed;
	}

	private boolean checkIfDissallowedAllIsSetInNextLine(BufferedReader reader) throws IOException
	{
		String lineLowerCase;
		while ((lineLowerCase = reader.readLine()) != null)
		{
			lineLowerCase = lineLowerCase.toLowerCase().replaceAll("\\s+", "");

			if (lineLowerCase.length() > 0)
			{
				if (lineLowerCase.equalsIgnoreCase("disallow:/"))
					return true;
				else
				{
					if (lineLowerCase.startsWith("disallow:"))
					{
						String proc = processLine(lineLowerCase, false);
						if (proc.length() > 0)
							disallowedURLs.add(proc);
					}
					else if (lineLowerCase.startsWith("allow:"))
					{
						String proc = processLine(lineLowerCase, true);
						if (proc.length() > 0)
							allowedURLs.add(proc);
					}
					return false;
				}
			}
		}
		return false;
	}

	private boolean checkLineIfUserAgentIsSolidsearch(String lineInLowerCase)
	{
		if ((lineInLowerCase.contains("user-agent") && lineInLowerCase.contains("solidsearchbot")) || (lineInLowerCase.contains("user-agent") && lineInLowerCase.contains("solidsearch")))
		{
			return true;
		}
		else
			return false;
	}

	private boolean checkLineIfUserAgentIsGoogleBotOrAll(String lineInLowerCase)
	{
		if ((lineInLowerCase.contains("user-agent") && lineInLowerCase.contains("googlebot") && !lineInLowerCase.contains("googlebot-image") && !lineInLowerCase.contains("googlebot-mobile")) || lineInLowerCase.equals("user-agent:*"))
		{
			return true;
		}
		else
			return false;
	}

	public int getHTTPStatusCode(String url)
	{
		int responseCode = 0;
		HttpGet httpget = null;
		HttpEntity entity = null;
		try
		{
			Thread.sleep(100);

			httpget = new HttpGet(url);
			httpget.setHeader("User-Agent", project.getBotUserAgent());
			httpget.setHeader("Accept-Encoding", "gzip");
			if (project.isUseBasicAuth())
				httpget.setHeader("Authorization", "Basic " + Base64.encodeToString((project.getBasicAuthUser() + ":" + project.getBasicAuthPassword()).getBytes(), false));

			if (!project.getCookieName().isEmpty())
			{
				httpget.setHeader("Cookie", project.getCookieName() + "=" + project.getCookieValue());
			}

			HttpResponse response = projectWorker.getHttpClient().execute(httpget, new BasicHttpContext());
			entity = response.getEntity();
			responseCode = response.getStatusLine().getStatusCode();

			Thread.sleep(100);

		}
		catch (Exception e)
		{
			if (httpget != null)
			{
				httpget.abort();
			}
			return 118;
		}
		finally
		{

			if (entity != null)
			{
				try
				{
					entity.getContent().close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

		}
		return responseCode;
	}

	/**
	 * Method extract brand name from domain. It also removes "-" like hallo-eltern >> halloeltern
	 * 
	 * @param urlOfRoot
	 * @return
	 */
	public String extractBrandFromDomain(String urlOfRoot)
	{
		try
		{
			URL url = new URL(urlOfRoot);

			String[] segs = url.getHost().split("\\.");

			if (segs.length - 2 > 0)
			{
				String brand = segs[segs.length - 2].toLowerCase();

				if (brand.length() > 255)
				{
					brand = brand.substring(0, 254);
					logger.warn("Brand was cut to 255 chars. Please check if this could be a problem for: " + urlOfRoot);
				}
				else if (brand.length() <= 0)
				{
					logger.warn("Could not detect brand! This wll be huge problem for keyword-topic-detection for: " + urlOfRoot);
				}

				brand.replace("-", " ");

				return brand;
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Method requests domain with HTTP protocol and edit
	 * returns how the entry is given by webmaster.
	 * @param domain string
	 * @return entry url
	 */
	public String testAndSetEntryURLBasesOnRootDomain(String domain) throws Exception
	{
		String httpURL = "http://" + domain;
		HttpResponse response = getResponseForURL(httpURL);

		if (response == null || response.getStatusLine().getStatusCode() > 302)
		{
			int status = -1;
			if (response != null)
			{
				status = response.getStatusLine().getStatusCode();
			}
			throw new RuntimeException("Could not connect to domain: " + domain + ", status: " + status + ", please check network or proxy settings.");
		}
	
		if (response.getStatusLine().getStatusCode() == 301)
		{
			String url = response.getLastHeader("Location").getValue();
			
			if (url.startsWith("/"))
			{
				// relative redirect
				url = "http://" + domain + url;
			}
			return url;
		}

		return httpURL;
	}

	public HttpResponse getResponseForURL(String url)
	{
		HttpGet httpget = null;
		HttpEntity entity = null;
		HttpResponse response = null;
		try
		{
			Thread.sleep(100);
			
			httpget = new HttpGet(url);
			httpget.setHeader("User-Agent", project.getBotUserAgent());
			httpget.setHeader("Accept-Encoding", "gzip");
			if (project.isUseBasicAuth())
				httpget.setHeader("Authorization", "Basic " + Base64.encodeToString((project.getBasicAuthUser() + ":" + project.getBasicAuthPassword()).getBytes(), false));

			if (!project.getCookieName().isEmpty())
			{
				httpget.setHeader("Cookie", project.getCookieName() + "=" + project.getCookieValue());
			}
			
			response = projectWorker.getHttpClient().execute(httpget, new BasicHttpContext());
			entity = response.getEntity();
			
			Thread.sleep(100);
		}
		catch (Exception e)
		{
			if (httpget != null)
			{
				httpget.abort();
			}
			logger.warn(e.getMessage());
			
			return null;
		}
		finally
		{
			if (entity != null)
			{
				try
				{
					entity.getContent().close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

		}
		return response;
	}
}
