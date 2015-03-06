package de.solidsearch.bot.businesslogic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import de.solidsearch.bot.data.URL;
import de.solidsearch.bot.textanalysis.ReadingLevelAnalyzer;
import de.solidsearch.bot.utils.BotConfig;
import de.solidsearch.shared.data.KeywordStem;
import de.solidsearch.shared.textanalysis.TextNormalizer;
import de.solidsearch.shared.textanalysis.TopicDetector;
import de.solidsearch.shared.utils.Background;
import de.solidsearch.shared.utils.Constants;
import de.solidsearch.shared.utils.DomainNormalizer;
import de.solidsearch.shared.utils.QWLocale;
import de.solidsearch.shared.utils.SortKeywordStemByFrequency;
import de.solidsearch.shared.utils.SortKeywordStemByWeight;

public class SourceCodeAnalyzer implements Serializable, Runnable
{
	private static final long serialVersionUID = 3118216068012971663L;
	private static final Logger logger = Logger.getLogger(SourceCodeAnalyzer.class.getName());
	private URL urlAfterCrawling = null;
	private URL urlBeforeCrawling = null;
	private ProjectJobWorker worker = null;
	private AtomicInteger runningThreadCounter = null;

	private HashMap<String, Integer> externalLinksDifferentDomainsOnThisPage = new HashMap<String, Integer>();
	private ArrayList<URL> differentLinksOnThisPage = new ArrayList<URL>();

	private HashMap<Element, Integer> images = new HashMap<Element, Integer>();

	private DomainNormalizer dn = new DomainNormalizer();

	private HashFunction hashTool = Hashing.murmur3_32();

	private boolean aboveTheFold = true;

	private boolean footer = false;

	private String brand = "";

	private String cleanedBrand = "";

	private int interestingExternalLinks = 0;

	private boolean linkLimitHit = false;

	private short forumScore = 0;

	private short blogScore = 0;

	private short lexiconScore = 0;
	
	private Map<Integer,String> textMap = new HashMap<Integer,String>();

	public SourceCodeAnalyzer(ProjectJobWorker projectWorker, URL urlToAnlyze, AtomicInteger runningThreadCounter)
	{
		this.worker = projectWorker;
		this.urlAfterCrawling = urlToAnlyze;

		try
		{
			this.urlBeforeCrawling = urlToAnlyze.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}

		this.runningThreadCounter = runningThreadCounter;
		this.brand = worker.getProjectSummary().getDomainBrandName();
		this.cleanedBrand = brand.replace("-", "");
	}

	public SourceCodeAnalyzer(ProjectJobWorker projectWorker, String brandKeyword)
	{
		this.worker = projectWorker;
		this.brand = brandKeyword;
		this.cleanedBrand = brandKeyword.replace("-", "");
	}

	public SourceCodeAnalyzer(ProjectJobWorker projectWorker)
	{
		this.worker = projectWorker;
	}

	public URL analyzeSourceCode()
	{
		HttpGet httpget = null;
		long timeBefore = System.currentTimeMillis();
		HttpEntity entity = null;
		try
		{
			httpget = new HttpGet(convertURL(urlAfterCrawling.getURLName()));
			httpget.setHeader("User-Agent", worker.getThisUserAgent());
			httpget.setHeader("Accept-Encoding", "gzip");

			if (worker.getProject().isUseBasicAuth())
				httpget.setHeader("Authorization", worker.getBasicAuthHeader());

			if (!worker.getProject().getCookieName().isEmpty())
			{
				httpget.setHeader("Cookie", worker.getCookieHeader());
			}

			HttpResponse response = worker.getHttpClient().execute(httpget, new BasicHttpContext());
			entity = response.getEntity();

			int responseCode = response.getStatusLine().getStatusCode();

			urlAfterCrawling.setFoundTimestamp(Calendar.getInstance().getTimeInMillis());

			if (responseCode >= 300 && responseCode < 400)
			{
				URL redirectedToURL = new URL();
				redirectedToURL.setURLName(response.getLastHeader("Location").getValue());
				urlAfterCrawling.setRedirectedToURL(categorizeAndSaveNewURL(redirectedToURL, worker.getProject().getRootDomainToCrawl()));
			}
			else if (responseCode == 200)
			{
				String sourceCode = "";
				InputStream content = entity.getContent();
				int pageSize = 0;

				try
				{
					// 5 MB
					ByteArrayOutputStream baos = new ByteArrayOutputStream(5120 * 1024);
					IOUtils.copy(content, baos);

					// check if GZIP is enabled
					if (entity.getContentEncoding() != null && entity.getContentEncoding().getValue().equalsIgnoreCase("gzip"))
					{
						// if so, do the right encoding
						byte[] input = baos.toByteArray();

						// 5 MB
						GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(input));
						Reader reader = new InputStreamReader(gzip, "UTF-8");
						StringWriter writer = new StringWriter();

						char[] buffer = new char[10240];
						for (int length = 0; (length = reader.read(buffer)) > 0;)
						{
							writer.write(buffer, 0, length);
						}
						sourceCode = writer.toString();
						pageSize = input.length;
					}
					else
					{
						sourceCode = baos.toString("UTF-8");
						pageSize = sourceCode.length();
						urlAfterCrawling.setGzipIssue(true);
					}
				}
				finally
				{
					content.close();
				}
				long timeAfterRespone = System.currentTimeMillis();

				// b to kb
				if (pageSize != 0)
					pageSize = (int) pageSize / 1024;

				urlAfterCrawling.setPageSize(pageSize);

				int responseTime = (int) (timeAfterRespone - timeBefore);
				urlAfterCrawling.setResponseTime(responseTime);
				worker.addResponseTimeToList(responseTime);
				urlAfterCrawling = grepTags(sourceCode.toString(), urlAfterCrawling, false);
				// processingTime
				worker.addProcessingTimeToList((int) (System.currentTimeMillis() - timeBefore));

			}
			else
			{
				// responscode == 404,500,503...
				urlAfterCrawling.setHttpStatusCode(responseCode);
			}

			urlAfterCrawling.setHttpStatusCode(responseCode);

		}
		catch (SocketTimeoutException s)
		{
			logger.info("Could not retrieve URL data due sockettimeout: " + urlAfterCrawling.getURLName());
			// 118 timeout
			urlAfterCrawling.setHttpStatusCode(0);
			urlAfterCrawling.setTimeout(true);

			if (httpget != null)
			{
				httpget.abort();
			}
		}
		catch (ConnectTimeoutException s)
		{
			logger.info("Could not retrieve URL data due connecttimeout: " + urlAfterCrawling.getURLName());
			s.printStackTrace();
			// 118 timeout
			urlAfterCrawling.setHttpStatusCode(-1);
			urlAfterCrawling.setTimeout(true);

			if (httpget != null)
			{
				httpget.abort();
			}
		}
		catch (Exception e)
		{
			logger.error("Error fetching this URL: " + urlAfterCrawling.getURLName() + " please check URL detection to avoid storing invailid URLs! ");
			e.printStackTrace();
			urlAfterCrawling.setHttpStatusCode(404);

			if (httpget != null)
			{
				httpget.abort();
			}
		}
		finally
		{
			urlAfterCrawling.setAlreadyCrawled(true);

			// control crawling speed
			checkAndWaitIfNecessary(System.currentTimeMillis() - timeBefore);

			if (entity != null)
			{
				try
				{
					entity.getContent().close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

		}
		return urlAfterCrawling;
	}

	public URL grepTags(String sourceCode, URL parentURL, boolean ignoreNewURLs)
	{
		// check if we hav html code here...
		if (!sourceCode.contains("html"))
		{
			if (!sourceCode.matches("head"))
			{
				logger.info("No <html> OR <head> section found. It seems to be no HTML. Ignore URL :" + parentURL.getURLName());
				return parentURL;
			}
		}

		Document doc = Jsoup.parse(sourceCode);
		
		StringBuffer relevantOnpageTextDCDetection = new StringBuffer();

		boolean foundH1Headline = false;
		boolean foundH2Headline = false;
		boolean foundH3Headline = false;

		try
		{
			String parentURLNameLower = parentURL.getURLName().toLowerCase();

			checkBackgroundByURL(parentURLNameLower);

			Elements elements = doc.getAllElements();

			for (int i = 0; i < elements.size(); i++)
			{
				if (i < ((elements.size() / 100d) * 50))
					aboveTheFold = true;
				else
					aboveTheFold = false;

				if (i > ((elements.size() / 100d) * 65))
					footer = true;
				else
					footer = false;

				Element node = elements.get(i);
				
				if (node.tagName().equalsIgnoreCase("html"))
				{
					String lang = node.attr("lang").toLowerCase();

					if (lang.contains("de"))
					{
						parentURL.setQwLocale(QWLocale.GERMAN);
					}
					else if (lang.contains("fr"))
					{
						parentURL.setQwLocale(QWLocale.FRENCH);
					}
					else if (lang.contains("nl"))
					{
						parentURL.setQwLocale(QWLocale.DUTCH);
					}
					else if (lang.contains("it"))
					{
						parentURL.setQwLocale(QWLocale.ITALIAN);
					}
					else if (lang.contains("ru") || lang.contains("kz"))
					{
						parentURL.setQwLocale(QWLocale.RUSSIAN);
					}
					else if (lang.contains("en"))
					{
						parentURL.setQwLocale(QWLocale.ENGLISH);
					}
					else if (lang.contains("en"))
					{
						parentURL.setQwLocale(QWLocale.ENGLISH);
					}
					else if (lang.contains("se"))
					{
						parentURL.setQwLocale(QWLocale.SWEDISH);
					}
					else if (lang.contains("br"))
					{
						parentURL.setQwLocale(QWLocale.PORTUGUESE);
					}
					else if (lang.contains("tr"))
					{
						parentURL.setQwLocale(QWLocale.TURKISH);
					}
				}
				else if (node.tagName().equalsIgnoreCase("title"))
				{
					// avoid storing to long text
					String titleText = node.text();
					if (titleText.length() > 500)
						titleText = titleText.substring(0, 499);
					parentURL.setTitle(removeInvalidChars(titleText.trim()));
				}
				else if (node.tagName().equalsIgnoreCase("meta"))
				{
					String name = node.attr("name");
					if (name != null)
					{
						if (name.equalsIgnoreCase("description"))
						{
							String description = node.attr("content");
							// avoid storing to long text
							if (description.length() > 500)
								description = description.substring(0, 499);
							parentURL.setMetaDescription(removeInvalidChars(description.trim()));
						}
						else if (name.equalsIgnoreCase("robots"))
						{
							String metaRobots = node.attr("content");

							if (metaRobots.toLowerCase().contains("nofollow"))
							{
								parentURL.setMetaRobotsFollow(false);
							}

							if (metaRobots.toLowerCase().contains("noindex"))
							{
								parentURL.setMetaRobotsIndex(false);
							}
						}
						else if (name.equalsIgnoreCase("generator"))
						{
							String type = node.attr("content");

							if (type.toLowerCase().contains("vbulletin"))
							{
								forumScore = 100;
							}
							else if (type.toLowerCase().contains("wordpress") || type.toLowerCase().contains("woo framework") || type.toLowerCase().contains("blogger"))
							{
								blogScore = 100;
							}
						}
					}
				}
				else if (node.tagName().equalsIgnoreCase("h1"))
				{
					if (parentURL.getH1().isEmpty())
					{
						// avoid storing to long text
						String headline = node.text();
						if (headline.length() > 500)
							headline = headline.substring(0, 499);
						parentURL.setH1(headline.trim());
						foundH1Headline = true;
						if (foundH2Headline || foundH3Headline)
							parentURL.setHeadlinesNotInRightOrder(true);
					}
				}
				else if (node.tagName().equalsIgnoreCase("h2"))
				{
					if (parentURL.getH2().isEmpty())
					{
						// avoid storing to long text
						String headline = node.text();
						if (headline.length() > 500)
							headline = headline.substring(0, 499);
						parentURL.setH2(headline.trim());
						foundH2Headline = true;
						if (!foundH1Headline || foundH3Headline)
							parentURL.setHeadlinesNotInRightOrder(true);
					}
				}
				else if (node.tagName().equalsIgnoreCase("h3"))
				{
					if (parentURL.getH3().isEmpty())
					{
						// avoid storing to long text
						String headline = node.text();
						if (headline.length() > 500)
							headline = headline.substring(0, 499);
						parentURL.setH3(headline.trim());
						foundH3Headline = true;
						if (!foundH1Headline || !foundH2Headline)
							parentURL.setHeadlinesNotInRightOrder(true);
					}

				}
				else if (node.tagName().equalsIgnoreCase("script"))
				{
					String data = node.data().toLowerCase();
					String src = node.attr("src").toLowerCase();

					// avoid analyzing to short script tags
					if (data.length() > 40)
					{
						// old GA / new universal GA
						if (data.contains("_setaccount") && data.contains("_gaq") || data.contains("analytics.js") && data.contains("UA-"))
						{
							parentURL.setGoogleAnalyticsCodeFound(true);
						}

						// adsense // own adserver / doubleclick
						else if (src.contains("pagead2.googlesyndication.com") || src.contains("adserv.") || src.contains("ad-emea.") || src.contains("ads."))
						{
							if (parentURL.getAdScripts() < 255)
								parentURL.setAdScripts((short) (parentURL.getAdScripts() + (short) 1));
							else
								parentURL.setAdScripts((short) 254);
						}
					}

				}
				else if (node.tagName().equalsIgnoreCase("a") || node.tagName().equalsIgnoreCase("link"))
				{
					/*
					 * Check number of different links and ignore more than 1000 for performance (like some big wikipedia-sites)
					 */
					if (differentLinksOnThisPage.size() > 1500 && !linkLimitHit)
					{
						// stop logging after 50 examples...
						if (worker.getMaxLinkLimitLogCounter() > 50)
						{
							logger.warn("Max link limit (1500) exceeded on page:" + parentURL.getURLName());
							linkLimitHit = true;
							worker.setMaxLinkLimitLogCounter(worker.getMaxLinkLimitLogCounter() + 1);
						}
					}
					else if (parentURL.getMetaRobotsFollow() || worker.getProject().isIgnoreInternalNofollow())
					{
						String link = node.attr("href");

						if (link != null)
						{

							String rel = node.attr("rel");
							String anchorText = node.text();

							try
							{
								URL newUrlChild = new URL();

								if (rel != null)
								{
									if (rel.toLowerCase().contains("nofollow"))
									{
										newUrlChild.setRelNofollow(true);
									}
									else if (rel.toLowerCase().contains("canonical"))
									{
										String canonicalTag = link.trim();

										canonicalTag = urlRelativToAbsoluteCheck(canonicalTag, parentURL.getURLName());

										parentURL.setCanonicalTag(canonicalTag);
										parentURL.setCanonicalTagHashcode(worker.getHashTool().generateHashCode(canonicalTag));
										if (parentURL.getURLName().equals(canonicalTag))
											continue;
									}
									else if (rel.toLowerCase().contains("pingback"))
									{
										// is wordpress?
										if (link.trim().toLowerCase().contains("xmlrpc.php"))
										{
											blogScore = 100;
										}
									}
									else if (rel.toLowerCase().contains("next"))
									{
										newUrlChild.setPagination(true);
									}
								}

								newUrlChild.setPagination(checkIfPaginationURL(link));

								if (anchorText.length() > 300)
									anchorText = anchorText.substring(0, 299);

								newUrlChild.setURLName(link);
								newUrlChild.setParentId(parentURL.getId());
								newUrlChild.setFirstFoundAnchorTextToThisURL(anchorText);

								if (!ignoreNewURLs)
								{
									categorizeAndSaveNewURL(newUrlChild, parentURL.getURLName());
								}
							}
							catch (Exception e)
							{
								logger.error("Exception during parsing :" + link + " " + e.getMessage());
								e.printStackTrace();
							}
						}
						else
						{
							// something is wrong within html-tags... ignore it
							continue;
						}
					}
					else
					{

					}
				}
				else if (node.tagName().equalsIgnoreCase("img"))
				{
					// put only above the fold images to map...
					if (aboveTheFold)
					{
						String styles = node.attr("style").toLowerCase();
						if (styles != null && styles.length() > 5)
						{
							// remove whitspaces
							styles = styles.replace(" ", "");
							if (styles.contains("display:none"))
							{
								continue;
							}
						}
						images.put(node, i);
					}
				}
				// text extraction for dc-detection
				if (node.tagName().equalsIgnoreCase("div") || node.tagName().equalsIgnoreCase("p") || node.tagName().equalsIgnoreCase("span") || node.tagName().equalsIgnoreCase("li") || node.tagName().equalsIgnoreCase("td")
						|| node.tagName().equalsIgnoreCase("b") || node.tagName().equalsIgnoreCase("strong") || node.tagName().equalsIgnoreCase("em") || node.tagName().equalsIgnoreCase("i") || node.tagName().equalsIgnoreCase("font")
						|| node.tagName().equalsIgnoreCase("h1") || node.tagName().equalsIgnoreCase("h2") || node.tagName().equalsIgnoreCase("h3") || node.tagName().equalsIgnoreCase("a") || node.tagName().equalsIgnoreCase("section"))
				{
					int minLength = 165;

					if (node.tagName().equalsIgnoreCase("div") || 
							node.tagName().equalsIgnoreCase("p"))
					{
						// Gets the combined text of this element and all its children.
						String text = node.text();
					
						if (text.length() > 40)
						{
							organizeOnpageTextMap(i, text);
						}
					}

					String styles = node.attr("style").toLowerCase();
					/**
					 * check for inline-css which controls visibility
					 * 
					 * @TODO implement also outline css
					 */
					if (styles != null && styles.length() > 5)
					{
						// remove whitspaces
						styles = styles.replace(" ", "");
						if (styles.contains("display:none"))
						{
							// avoid taking text which is disabled by css
							continue;
						}
					}
					
					// Gets the text owned by this element only; does not get the combined text of all children.
					String nodeText = node.ownText();

					// not implemented now
					checkIfTextContainsTimestamp(nodeText);

					// avoid to short text samples for dc detection
					if (nodeText.length() > minLength)
					{						
						relevantOnpageTextDCDetection.append(nodeText);

						// for security detecting sentence end... to avoid problems in readingLevel-detection 
						if (!nodeText.endsWith(".") && !nodeText.endsWith("!") && !nodeText.endsWith("?"))
						{
							relevantOnpageTextDCDetection.append(".");
						}
					}
				}
			}
			parentURL = checkIfRelevantImagesAvailable(doc, parentURL);

			parentURL.setOnPageText(extractRelevantOnpageTextFragement());
			
			parentURL = analyzeOnpageText(relevantOnpageTextDCDetection.toString(), parentURL);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return parentURL;
	}

	private String categorizeAndSaveNewURL(URL newUrlChild, String parent)
	{
		String urlName = newUrlChild.getURLName();
		boolean externalLink = false;

		if (urlName == null || urlName.isEmpty())
		{
			return urlName;
		}
		else
		{
			urlName = urlName.trim();
		}
		// do not crawl images / css etc.
		if (!worker.getUrlChecker().isURLAllowed(urlName))
		{
			return urlName;
		}

		urlName = urlRelativToAbsoluteCheck(urlName, parent);

		// only persist new url if its more than 4 chars
		if (urlName.length() > 4)
		{
			// external link check
			if (!urlName.startsWith("http://" + worker.getProject().getRootDomainToCrawlWithoutProtocol()) && !urlName.startsWith("https://" + worker.getProject().getRootDomainToCrawlWithoutProtocol()))
			{
				externalLink = true;

				// avoid differend versions of urls...
				urlName = dn.removeTrailingSlash(urlName);

				if (interestingExternalLinks > 30 || urlName.contains(brand) || urlName.contains(cleanedBrand))
				{
					// do not power up more than 30 externalLinks on this page and also do not power external links which contain brand...
					newUrlChild.setExternalLinkPower((short) 0);
				}
				else
				{
					if (newUrlChild.getFirstFoundAnchorTextToThisURL().length() < 1)
					{
						newUrlChild.setExternalLinkPower((short) 0);
					}
					else
					{
						// ranking for link
						if (aboveTheFold)
							newUrlChild.setExternalLinkPower((short) 45);
						else if (footer)
							newUrlChild.setExternalLinkPower((short) 5);
						else
							// somewhere else
							newUrlChild.setExternalLinkPower((short) 25);

						if (!newUrlChild.isRelNofollow())
						{
							newUrlChild.setExternalLinkPower((short) (newUrlChild.getExternalLinkPower() + 5));
						}
						interestingExternalLinks++;
					}
				}
			}
			// replace html amperstamps
			urlName = urlName.replace("&amp;", "?");

			// remove domain dot in url if exists e.g. www.example.de./test
			int firstIndexOf = urlName.indexOf("./");
			if (firstIndexOf > 0)
			{
				int firstIndexOfFirstSlash = urlName.indexOf("/", 8);

				if (firstIndexOfFirstSlash == (firstIndexOf + 1))
				{
					logger.warn("Found dot on domain end! New URL: " + urlName + " found at: " + urlAfterCrawling.getURLName());
					StringBuffer newURL = new StringBuffer(urlName.substring(0, firstIndexOf));
					newURL.append(urlName.substring(firstIndexOf + 1, urlName.length()));
					urlName = newURL.toString();
				}
			}

			// return if we have got a javascript link
			if (urlName.toLowerCase().startsWith("javascript"))
			{
				return "";
			}

			// remove hashtag parameters
			firstIndexOf = urlName.indexOf("#");
			if (firstIndexOf > 0)
			{
				urlName = urlName.substring(0, firstIndexOf);
			}

			if (urlName.length() >= 4000)
			{
				logger.warn("Found very long URL! URL was not saved: " + urlName + " found at: " + urlAfterCrawling.getURLName());
				return "";
			}

			// now set up this new obj, because we want to store it

			newUrlChild.setId(worker.getHashTool().generateHashCode(urlName));
			newUrlChild.setPartitionkey(hashTool.hashString(urlName).asInt());
			newUrlChild.setURLName(urlName);
			newUrlChild.setFoundTimestamp(Calendar.getInstance().getTimeInMillis());
			newUrlChild.setFoundAtURL(urlAfterCrawling.getURLName());
			newUrlChild.setExternalLink(externalLink);

			// check if we already count this internal link on this page...
			// we do not want to do the next steps again and again
			// if the same link occurs more than one time at this page

			synchronized (worker)
			{
				// increment total link count
				worker.getProjectSummary().setTotalLinks(worker.getProjectSummary().getTotalLinks() + 1);
			}

			if (!differentLinksOnThisPage.contains(newUrlChild))
			{
				// ignore if newURL is the same like parent
				if (!urlAfterCrawling.getId().equals(newUrlChild.getId()))
				{
					if (!newUrlChild.isExternalLink())
					{
						urlAfterCrawling.setInternalLinksOnThisPage(urlAfterCrawling.getInternalOutgoingLinksOnThisPage() + 1);
					}

					int depth = newUrlChild.getDepthFromDomainRoot();
					if (depth == -1)
					{
						depth = urlAfterCrawling.getDepthFromDomainRoot() + 1;
						newUrlChild.setDepthFromDomainRoot(depth);

						// take a look on configured limits...
						if (newUrlChild.getDepthFromDomainRoot() > worker.getProject().getCrawlingDepth())
						{
							return "";
						}
					}
					// /////////////////////////////////////////////////////////////
					// now save new urls
					/**
					 * Need to sync this block because search and save URL must be done in one step...
					 */
					synchronized (worker)
					{
						// do not analyze new urls if discover-limit is reached...
						if ((worker.getProjectSummary().getInternalURLs() + worker.getProjectSummary().getExternalURLs()) >= worker.getDiscoverLimit())
						{
							return "";
						}

						// search if we have got this url in insert cache
						URL oldURL = worker.getUrlManager().getURLFromInsertCache(newUrlChild.getId());
						// search if we have got this url in update cache
						if (oldURL == null)
							oldURL = worker.getUrlManager().getURLFromUpdateCache(newUrlChild.getId());
						// search if we have got this url in database
						if (oldURL == null)
						{
							oldURL = worker.getUrlManager().getURLbyId(newUrlChild.getId(), newUrlChild.getPartitionkey());
						}

						if (oldURL == null && !newUrlChild.isExternalLink())
						{
							worker.getProjectSummary().setInternalURLs(worker.getProjectSummary().getInternalURLs() + 1);

						}
						else if (newUrlChild.isExternalLink())
						{
							String externalDomainName = dn.extractDomainName(newUrlChild.getURLName());
							if (externalDomainName.length() > 300)
								externalDomainName = externalDomainName.substring(0, 299);

							if (oldURL == null)
							{
								// this external link was not found until now...
								worker.getProjectSummary().setExternalURLs(worker.getProjectSummary().getExternalURLs() + 1);
								newUrlChild.setExternalHostName(externalDomainName);
							}
							else
							{
								urlAfterCrawling.setExternalLinksOnThisPage(urlAfterCrawling.getExternalLinksOnThisPage() + 1);
								if (externalDomainName != null)
								{
									if (!externalLinksDifferentDomainsOnThisPage.containsKey(externalDomainName))
									{
										externalLinksDifferentDomainsOnThisPage.put(externalDomainName, new Integer(1));
									}
									else
									{
										int value = externalLinksDifferentDomainsOnThisPage.get(externalDomainName).intValue();
										externalLinksDifferentDomainsOnThisPage.put(externalDomainName, new Integer(value + 1));
									}
								}
							}
						}

						// only persist if it was not already persisted...
						if (oldURL == null)
						{
							if (newUrlChild.isRelNofollow())
								newUrlChild.setNofollowLinksToThisPage(newUrlChild.getNofollowLinksToThisPage() + 1);
							else
								newUrlChild.setFollowLinksToThisPage(newUrlChild.getFollowLinksToThisPage() + 1);

							worker.getUrlManager().saveURL(newUrlChild);
						}
						else
						{
							worker.getUrlManager().incrementLinkCountsAndCheckRelNofollowchange(oldURL);
						}
					}
					differentLinksOnThisPage.add(newUrlChild);
				}
			}
		}
		return urlName;
	}

	/**
	 * Method checks given processing time due to average processing times. If necessary method sleeps a while to hold average values.
	 * 
	 * @param currentProcessingTime
	 */
	private void checkAndWaitIfNecessary(long currentProcessingTime)
	{
		final long MINWAIT = 150;
		long avgProcTimeThreshold = MINWAIT;
		long speedNormalizer = 50;

		if (worker.getLastAvgProcessingTime() != 0)
		{
			// take 70% of average processing time
			avgProcTimeThreshold = (long) (worker.getLastAvgProcessingTime() / 100d * 70d);
			if (avgProcTimeThreshold <= MINWAIT)
				avgProcTimeThreshold = MINWAIT;
			// avoid to be constant on average values...
			speedNormalizer = (long) (worker.getLastAvgProcessingTime() / 100d * 10d);
			if (speedNormalizer < 50)
				speedNormalizer = 50;
		}

		if (currentProcessingTime < avgProcTimeThreshold)
		{
			// if we are quicker than threshold, wait a while to be on avg-line...

			// bring speed on avg-track minus speedNormalizer (10%) == let us be a little quicker than avg...
			long sleepTime = worker.getLastAvgProcessingTime() - currentProcessingTime - speedNormalizer;
			if ((currentProcessingTime + sleepTime) <= MINWAIT)
				sleepTime = MINWAIT;
			try
			{
				Thread.sleep(sleepTime);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		// do all a little bit slower if it is a remote project
		// /////////////////////////////////////////////
		try
		{
			if (worker.getProject().isRemoteProject())
			{
				Thread.sleep(250);
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		// ///////////////////////////////////////////
	}

	/**
	 * Method encodes invailid characters.
	 * 
	 * @param url
	 * @return encoded url string
	 */
	public String convertURL(String str)
	{
		String url = null;
		try
		{
			// first only remove the most common invailid chars like whitspaces and pipes
			url = new String(str.trim().replace(" ", "%20").replace("|", "%7C"));

			// replace("&", "%26")
			// .replace(",", "%2c").replace("(", "%28").replace(")", "%29")
			// .replace("!", "%21").replace("=", "%3D").replace("<", "%3C")
			// .replace(">", "%3E").replace("#", "%23").replace("$", "%24")
			// .replace("'", "%27").replace("*", "%2A").replace("-", "%2D")
			// .replace(".", "%2E").replace("/", "%2F").replace(":", "%3A")
			// .replace(";", "%3B").replace("?", "%3F").replace("@", "%40")
			// .replace("[", "%5B").replace("\\", "%5C").replace("]", "%5D")
			// .replace("_", "%5F").replace("`", "%60").replace("{", "%7B")
			// .replace("|", "%7C").replace("}", "%7D"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return url;
	}
	
	private String extractRelevantOnpageTextFragement()
	{
		SortedSet<Integer> keys = new TreeSet<Integer>(textMap.keySet());
		
		String text = textMap.get(keys.first());
		long maxLength = text.length();

		int i = 0;
		for (Integer key : keys) { 
			if (keys.size() == 1)
			{
				text = textMap.get(key);
			}
			
			if (i != 0)
			{
				if (textMap.get(key).length() <= maxLength  && textMap.get(key).length() > (maxLength * 0.58f))
				{
					if (textMap.get(key).length() <= text.length())
					{
							text = textMap.get(key);
					}
				}
			}
			i++;
		}
		return text;
	}

	private URL analyzeOnpageText(String relevantOnpageTextDC, URL parentURL)
	{
		// mark as low content by default...
		parentURL.setContentHashcode(null);

		// check if text has less than 320 chars...
		if (relevantOnpageTextDC.length() > 320)
		{
			// for performance: if text is long enough skip word and sentence detection
			if (relevantOnpageTextDC.length() > 3500)
			{
				parentURL.setContentHashcode(worker.getHashTool().generateHashCode(relevantOnpageTextDC.toString()));
			}
			else
			{
				// check if text has less than 55 words...
				final int countOfWords = 55;
				if (relevantOnpageTextDC.toString().split("\\s+", (countOfWords + 2)).length >= countOfWords)
				{
					// check if text has less than 3 sentences...
					if (sentencesAvailableInText(relevantOnpageTextDC.toString(), 3))
					{
						parentURL.setContentHashcode(worker.getHashTool().generateHashCode(relevantOnpageTextDC.toString()));
					}
				}
			}
		}

		languageFallbackCheck(parentURL);

		// only extract keywords if URL is INDEX
		if (parentURL.getMetaRobotsIndex() == true)
		{
			// only extract keywords if canonical is not pointing to another URL
			if (parentURL.getCanonicalTag().equalsIgnoreCase(parentURL.getURLName()) || parentURL.getCanonicalTag().length() == 0)
			{		
				// avoid analyzing text with less input... (low content)
				if (parentURL.getOnPageText().length() > 50)
				{
					ReadingLevelAnalyzer rl = new ReadingLevelAnalyzer();
					
					// performance: for keyword detection and readinglevel, cut text to a meaningful length...
					if (relevantOnpageTextDC.length() > 20000)
					{
						StringBuffer reducedText = new StringBuffer(relevantOnpageTextDC.substring(0, 20000));
						reducedText.append(".");
						
						extractAndWeightKeywords(reducedText.toString(), parentURL);
						parentURL.setReadingLevel(rl.getReadingLevel(reducedText.toString()));
					}
					else
					{
						extractAndWeightKeywords(relevantOnpageTextDC, parentURL);
						parentURL.setReadingLevel(rl.getReadingLevel(relevantOnpageTextDC));
					}
					
					if (parentURL.getOnPageText().length() > 400000)
					{
						parentURL.setOnPageText(parentURL.getOnPageText().substring(0, 400000));
						logger.warn("Found very long text. Cut text to 400.000 chars for security: " + parentURL.getURLName());
					}
					
					// important for db and elasticsearch security
					parentURL.setOnPageText(removeInvalidChars(parentURL.getOnPageText()));
				}
				else
				{
					parentURL.setOnPageText("");
				}
			}
		}

		parentURL.setBackgroundId(calculateBackground());

		return parentURL;
	}

	/**
	 * Not implemented now
	 * 
	 * @param nodeText
	 */
	private void checkIfTextContainsTimestamp(String nodeText)
	{
		/**
		 * TODO search for timestamp-patterns
		 */
		// if (timestampCounter<5)
		// {
		// if (nodeText.matches(".*[0-9]{1,2}:[0-9]{2}.*"))
		// {
		// timestampCounter++;
		// System.out.println("time found " + nodeText);
		// }
		// }
	}

	private URL checkIfRelevantImagesAvailable(Document doc, URL parentURL)
	{
		int linkedImages = 0;

		for (Element element : images.keySet())
		{
			Element node = element;
			Element parent = node.parent();

			String parentText = "";
			if (parent.tagName().equalsIgnoreCase("a"))
			{
				parentText = parent.parent().text();
				linkedImages++;
				
				// assume this might be a online shop or a site with a log of images - e.g. image gallery...
				if (linkedImages > 10)
				{
					parentURL.setRelevantImages(true);
					break;
				}
			}
			else
			{
				parentText = parent.text();
			}

			if (parentText.length() > 110)
			{
				parentURL.setRelevantImages(true);
			}
		}

		return parentURL;
	}

	private void extractAndWeightKeywords(String onpageText, URL parentURL)
	{
		String urlKeywords = parentURL.getURLName().toLowerCase();

		if (urlKeywords.startsWith("https"))
		{
			urlKeywords = urlKeywords.replace("https://" + worker.getProject().getRootDomainToCrawlWithoutProtocol(), "");
		}
		else
		{
			urlKeywords = urlKeywords.replace("http://" + worker.getProject().getRootDomainToCrawlWithoutProtocol(), "");
		}
		// remove .html or .htm
		urlKeywords = urlKeywords.replaceAll(".html|.htm", " ");
		// remove non letter chars for different languages (also cyrillic) == [^a-zA-Z] for only german
		urlKeywords = urlKeywords.replaceAll("[^\\p{L}]+", " ");

		// remove domain
		String title = parentURL.getTitle().toLowerCase();
		String domainWP = worker.getProject().getRootDomainToCrawlWithoutProtocol().toLowerCase();

		// www.example.de
		title = title.replace(domainWP, "");
		// example.de
		title = title.replace(domainWP.substring(4), "");
		// example

		title = title.replace(brand, "");
		// ex-ample
		title = title.replace(cleanedBrand, "");

		onpageText = onpageText.toLowerCase();
		// remove possible www.brand.de
		onpageText = onpageText.replace(domainWP, "");
		// remove possible brand.de
		onpageText = onpageText.replace(domainWP.substring(4), "");
		// remove possible brand
		onpageText = onpageText.replace(brand, "");
		// remove possible cleaned brand
		onpageText = onpageText.replace(cleanedBrand, "");

		TextNormalizer tn = new TextNormalizer();

		ArrayList<KeywordStem> allKeywords = new ArrayList<KeywordStem>();

		final short TITLESCORE = 30;
		final short URLSCORE = 14;
		final short FIRSTANCHORTEXTSCORE = 8;
		final short H1SCORE = 8;
		final short H2SCORE = 5;
		final short H3SCORE = 5;

		ArrayList<KeywordStem> normalizedTitleVector = tn.normalize(title, parentURL.getQwLocale(), (short) TITLESCORE);
		ArrayList<KeywordStem> normalizedURLKeywordVector = tn.normalize(urlKeywords, parentURL.getQwLocale(), (short) URLSCORE);
		ArrayList<KeywordStem> normalizedFirstAnchorTextVector = tn.normalize(parentURL.getFirstFoundAnchorTextToThisURL(), parentURL.getQwLocale(), (short) FIRSTANCHORTEXTSCORE);

		ArrayList<KeywordStem> normalizedOnpageTextVector = null;

		normalizedOnpageTextVector = tn.normalize(onpageText, parentURL.getQwLocale(), (short) 0);

		ArrayList<KeywordStem> normalizedH1Vector = tn.normalize(parentURL.getH1(), parentURL.getQwLocale(), (short) H1SCORE);
		ArrayList<KeywordStem> normalizedH2Vector = tn.normalize(parentURL.getH2(), parentURL.getQwLocale(), (short) H2SCORE);
		ArrayList<KeywordStem> normalizedH3Vector = tn.normalize(parentURL.getH3(), parentURL.getQwLocale(), (short) H3SCORE);

		parentURL.setNormalizedTitle(tn.stemTermVectorToString(normalizedTitleVector));
		parentURL.setNormalizedText(tn.stemTermVectorToString(normalizedOnpageTextVector));
		parentURL.setNormalizedH1(tn.stemTermVectorToString(normalizedH1Vector));
		parentURL.setNormalizedH2(tn.stemTermVectorToString(normalizedH2Vector));
		parentURL.setNormalizedH3(tn.stemTermVectorToString(normalizedH3Vector));

		TopicDetector td = new TopicDetector();

		normalizedTitleVector = td.reduceAndCombineLists(new ArrayList<KeywordStem>(), normalizedTitleVector, false, false, false);
		normalizedURLKeywordVector = td.reduceAndCombineLists(new ArrayList<KeywordStem>(), normalizedURLKeywordVector, false, false, false);
		normalizedFirstAnchorTextVector = td.reduceAndCombineLists(new ArrayList<KeywordStem>(), normalizedFirstAnchorTextVector, false, false, false);

		normalizedOnpageTextVector = td.reduceAndCombineLists(new ArrayList<KeywordStem>(), normalizedOnpageTextVector, false, false, false);
		normalizedOnpageTextVector = td.weigthKeywordsByFrequency(normalizedOnpageTextVector);
		normalizedH1Vector = td.reduceAndCombineLists(new ArrayList<KeywordStem>(), normalizedH1Vector, false, false, false);
		normalizedH2Vector = td.reduceAndCombineLists(new ArrayList<KeywordStem>(), normalizedH2Vector, false, false, false);
		normalizedH3Vector = td.reduceAndCombineLists(new ArrayList<KeywordStem>(), normalizedH3Vector, false, false, false);

		allKeywords = td.reduceAndCombineLists(allKeywords, normalizedTitleVector, true, false, false);
		allKeywords = td.reduceAndCombineLists(allKeywords, normalizedURLKeywordVector, true, false, false);
		allKeywords = td.reduceAndCombineLists(allKeywords, normalizedFirstAnchorTextVector, true, false, false);

		allKeywords = td.reduceAndCombineLists(allKeywords, normalizedOnpageTextVector, true, false, false);
		allKeywords = td.reduceAndCombineLists(allKeywords, normalizedH1Vector, true, false, false);
		allKeywords = td.reduceAndCombineLists(allKeywords, normalizedH2Vector, true, false, false);
		allKeywords = td.reduceAndCombineLists(allKeywords, normalizedH3Vector, true, false, false);

		Collections.sort(allKeywords, new SortKeywordStemByWeight());

		ArrayList<KeywordStem> relevantKeywords = new ArrayList<KeywordStem>();

		// extract relevant keywords...
		for (int i = 0; i < allKeywords.size(); i++)
		{
			if (allKeywords.get(i).getKeywordWeight() >= (Constants.RELEVANCETHRESHOLD))
			{
				relevantKeywords.add(allKeywords.get(i));
			}
		}

		// ////////////////////////
		allKeywords = removeDomainRootKeywords(allKeywords, parentURL);
		// ///////////////////////

		for (int i = 0; i < allKeywords.size(); i++)
		{
			// double brand check: avoid brand as topic...
			if (!allKeywords.get(i).getShortestTerm().contains(cleanedBrand) && !allKeywords.get(i).getShortestTerm().contains(brand))
			{
				if (allKeywords.get(i).getTupel() == 1)
				{
					// avoid overriding, if it was already set...
					if (parentURL.getTopicKeywordOneTerm().length() <= 0)
					{
						// avoid short stings and numbers to be topic
						if (allKeywords.get(i).getMostFrequentTerm().length() <= 1)
							continue;
						if (isNumeric(allKeywords.get(i).getMostFrequentTerm()))
							continue;

						KeywordStem topic = extractTopicByTupel(allKeywords, i);

						parentURL.setTopicKeywordOneTerm(topic.getMostFrequentTerm());
						parentURL.setNormalizedTopicKeywordOneTerm(topic.getKeywordStem());
						parentURL.setTopicKeywordOneTermWeight(topic.getKeywordWeight());
					}
				}
				else if (allKeywords.get(i).getTupel() == 2 && allKeywords.get(i).getKeywordWeight() >= 40)
				{
					// avoid overriding, if it was already set...
					if (parentURL.getTopicKeywordTwoTerms().length() <= 0)
					{
						KeywordStem topic = extractTopicByTupel(allKeywords, i);

						parentURL.setTopicKeywordTwoTerms(topic.getMostFrequentTerm());
						parentURL.setNormalizedTopicKeywordTwoTerms(topic.getKeywordStem());
						parentURL.setTopicKeywordTwoTermsWeight(topic.getKeywordWeight());
					}
				}
				else if (allKeywords.get(i).getTupel() == 3 && allKeywords.get(i).getKeywordWeight() >= 40)
				{
					// avoid overriding, if it was already set...
					if (parentURL.getTopicKeywordThreeTerms().length() <= 0)
					{
						KeywordStem topic = extractTopicByTupel(allKeywords, i);

						parentURL.setTopicKeywordThreeTerms(topic.getMostFrequentTerm());
						parentURL.setNormalizedTopicKeywordThreeTerms(topic.getKeywordStem());
						parentURL.setTopicKeywordThreeTermsWeight(topic.getKeywordWeight());
					}
				}
			}
		}

		synchronized (worker)
		{
			worker.setKeywords(td.reduceAndCombineLists(worker.getKeywords(), relevantKeywords, false, true, true));
		}

		parentURL.setSpamScore(calculateSpamFactor(normalizedOnpageTextVector, allKeywords.get(0)));

	}

	private KeywordStem extractTopicByTupel(ArrayList<KeywordStem> allKeywords, int currentListPosition)
	{
		KeywordStem topic = allKeywords.get(currentListPosition);

		for (int j = currentListPosition; j < allKeywords.size(); j++)
		{
			if (topic.getKeywordWeight() == allKeywords.get(j).getKeywordWeight())
			{
				// avoid short stings and numbers to be topic
				if (allKeywords.get(j).getMostFrequentTerm().length() <= 1)
					continue;
				if (isNumeric(allKeywords.get(j).getMostFrequentTerm()))
					continue;

				if (allKeywords.get(j).getTupel() == topic.getTupel())
				{
					if (allKeywords.get(j).getFrequency() > topic.getFrequency())
					{
						topic = allKeywords.get(j);
					}
				}
			}
			else
			{
				break;
			}
		}
		return topic;
	}

	private ArrayList<KeywordStem> removeDomainRootKeywords(ArrayList<KeywordStem> allKeywords, URL parentURL)
	{
		ArrayList<KeywordStem> rootKeywords = worker.getRootKeywords();

		// check if this is root... (root keyword extraction)
		// /////////////////////
		if (dn.removeTrailingSlash(dn.removeProtocol(parentURL.getURLName())).equalsIgnoreCase(worker.getProject().getRootDomainToCrawlWithoutProtocol()))
		{
			// extract root keywords
			if (!rootKeywords.isEmpty())
			{
				logger.warn("It seems to be that we want to double extract root keywords. Please check!");
			}
			else
			{
				for (int i = 0; i < allKeywords.size(); i++)
				{
					KeywordStem ks = allKeywords.get(i);
					if (ks.getKeywordWeight() >= 60)
					{
						rootKeywords.add(ks);
					}
				}
				worker.setRootKeywords(rootKeywords);
			}
		}
		else
		// ATTENTION: this is in 99% the default case...
		{
			// remove root-keywords of allKeywords result...
			for (int i = 0; i < rootKeywords.size(); i++)
			{
				allKeywords.remove(rootKeywords.get(i));
			}
		}
		return allKeywords;
	}

	/**
	 * Method checks how frequent topickeyword is used in onpagetext and calculates spam score. If text is less 25 terms, no spam score is calculated.
	 * 
	 * @param normalizedOnpageTextVector
	 * @param allKeywords
	 * @return
	 */
	private short calculateSpamFactor(ArrayList<KeywordStem> normalizedOnpageTextVector, KeywordStem topicKeyword)
	{
		long totalOnpageTerms = 0;

		for (int i = 0; i < normalizedOnpageTextVector.size(); i++)
		{
			totalOnpageTerms += normalizedOnpageTextVector.get(i).getFrequency();
		}

		Collections.sort(normalizedOnpageTextVector, new SortKeywordStemByFrequency());

		long freqOfTopicKeyword = 0;

		// only calc spam factor if text contains more than 50 terms
		if (totalOnpageTerms > 25)
		{
			for (int j = 0; j < normalizedOnpageTextVector.size(); j++)
			{
				// search for the topic keyword...
				if (topicKeyword.getKeywordStem().equalsIgnoreCase(normalizedOnpageTextVector.get(j).getKeywordStem()))
				{
					freqOfTopicKeyword = normalizedOnpageTextVector.get(j).getFrequency();
					break;
				}
			}
		}
		else
		{
			return 0;
		}

		short spamScore = 0;

		int offset = 0;

		// reduce thresholds for long documents...
		if (totalOnpageTerms > 1000)
			offset = 1;
		if (totalOnpageTerms > 2000)
			offset = 2;

		if ((100d / totalOnpageTerms * freqOfTopicKeyword) >= (5 - offset))
		{
			spamScore += 5;
		}
		if ((100d / totalOnpageTerms * freqOfTopicKeyword) >= (6 - offset))
		{
			spamScore += 5;
		}
		if ((100d / totalOnpageTerms * freqOfTopicKeyword) >= (7 - offset))
		{
			spamScore += 5;
		}
		if ((100d / totalOnpageTerms * freqOfTopicKeyword) >= (8 - offset))
		{
			spamScore += 10;
		}
		if ((100d / totalOnpageTerms * freqOfTopicKeyword) >= (9 - offset))
		{
			spamScore += 15;
		}
		if ((100d / totalOnpageTerms * freqOfTopicKeyword) >= (10 - offset))
		{
			spamScore += 30;
		}
		if ((100d / totalOnpageTerms * freqOfTopicKeyword) >= (11 - offset))
		{
			spamScore += 30;
		}

		return spamScore;
	}

	private String urlRelativToAbsoluteCheck(String urlName, String parentUrlName)
	{
		if (!urlName.startsWith("http") && !urlName.contains(worker.getProject().getRootDomainToCrawlWithoutProtocol()))
		{
			// we have got a relative link in different ways:
			// "href=kategorie/index.htm or href=./kategorie/index.htm"
			try
			{
				// check if relativ url points to root like this href="test.htm" (like miss-sixty bug http://www.zalando.de/miss-sixty/)
				java.net.URL baseUrl;
				if (!urlName.startsWith("/") && !urlName.startsWith("."))
					baseUrl = new java.net.URL(worker.getProject().getRootDomainToCrawl());
				else
					baseUrl = new java.net.URL(parentUrlName);

				java.net.URL url = new java.net.URL(baseUrl, urlName);
				urlName = url.toExternalForm();
			}
			catch (MalformedURLException e)
			{
				if (!urlName.toLowerCase().startsWith("javascript") && !urlName.toLowerCase().startsWith("tel:") && !urlName.toLowerCase().startsWith("android-app") && !urlName.toLowerCase().startsWith("irc:")
						&& !urlName.toLowerCase().startsWith("mms:") && !urlName.toLowerCase().startsWith("news:"))
					logger.warn("Found malformed relative URL! New URL: " + urlName + " found at: " + urlAfterCrawling.getURLName());
			}
		}
		return urlName;
	}

	private String removeInvalidChars(String text)
	{
		// remove " from text for security
		text = text.replaceAll("\"", "");
		// remove \ from text for security
		text = text.replaceAll("\\\\", "");
		text = text.replaceAll("'", "");
		return text;
	}

	private void languageFallbackCheck(URL parentURL)
	{
		if (parentURL.getQwLocale() == -1)
		{
			String domainRoot = worker.getProject().getRootDomainToCrawl().toLowerCase();

			if (domainRoot.contains(".de"))
			{
				parentURL.setQwLocale(QWLocale.GERMAN);
			}
			else if (domainRoot.contains(".fr"))
			{
				parentURL.setQwLocale(QWLocale.FRENCH);
			}
			else if (domainRoot.contains(".nl"))
			{
				parentURL.setQwLocale(QWLocale.DUTCH);
			}
			else if (domainRoot.contains(".it"))
			{
				parentURL.setQwLocale(QWLocale.ITALIAN);
			}
			else if (domainRoot.contains(".se"))
			{
				parentURL.setQwLocale(QWLocale.SWEDISH);
			}
			else if (domainRoot.contains(".ru") || domainRoot.contains(".kz"))
			{
				parentURL.setQwLocale(QWLocale.RUSSIAN);
			}
			else if (domainRoot.contains(".com.br"))
			{
				parentURL.setQwLocale(QWLocale.PORTUGUESE);
			}
			else if (domainRoot.contains(".com.tr"))
			{
				parentURL.setQwLocale(QWLocale.TURKISH);
			}
			else if (domainRoot.contains(".com") || domainRoot.contains(".uk") || domainRoot.contains(".us"))
			{
				parentURL.setQwLocale(QWLocale.ENGLISH);
			}
			else
			{
				// fallback
				parentURL.setQwLocale(QWLocale.GERMAN);
			}
		}
	}

	private void checkBackgroundByURL(String parentURLNameLower)
	{
		if (parentURLNameLower.contains("/forum") || parentURLNameLower.contains("/foren") || parentURLNameLower.contains("-forum") || parentURLNameLower.contains("/discussion/") || parentURLNameLower.contains("/community/"))
		{
			forumScore = 55;
		}
		else if (parentURLNameLower.contains("blogspot.") || parentURLNameLower.contains("wordpress.") || parentURLNameLower.contains("blogger.") || parentURLNameLower.contains("blog."))
		{
			blogScore = 55;
		}
		else if (parentURLNameLower.contains("wikipedia.org"))
		{
			lexiconScore = 55;
		}
	}

	private short calculateBackground()
	{
		if (forumScore == 0 && blogScore == 0 && lexiconScore == 0)
		{
			// others
			return Background.OTHERS.getBackgroundId();
		}
		else if (forumScore > blogScore && forumScore > lexiconScore)
		{
			// forum
			return Background.FORUM.getBackgroundId();
		}
		else if (lexiconScore > blogScore)
		{
			// forum
			return Background.LEXICON.getBackgroundId();
		}
		else
		{
			// blog
			return Background.BLOG.getBackgroundId();
		}
	}

	public void organizeOnpageTextMap(int divPosition, String text)
	{
		if (textMap.size() < 10)
		{		
			textMap.put(divPosition, text);
		}	
		else
		{
		    Iterator<Entry<Integer, String>> it = textMap.entrySet().iterator();
		    
		    long textLength = text.length();
		    Map.Entry<Integer, String> keyValueToRemove = null;
		    
		    while (it.hasNext()) 
		    {
		    	Map.Entry<Integer, String> keyValue = (Map.Entry<Integer, String>)it.next();
		    	
		    	if (keyValue.getValue().length() < textLength)
		    	{
		    		textLength = keyValue.getValue().length();
		    		keyValueToRemove = keyValue;
		    	}
		    	else if (textLength == keyValue.getValue().length())
		    	{
		    		if (keyValue.getKey() > divPosition)
		    			keyValueToRemove = keyValue;
		    		else
		    			continue;
		    	}
		    }
		    
		    if (keyValueToRemove != null)
		    {
			    textMap.remove(keyValueToRemove.getKey());
			    textMap.put(divPosition, text);
		    }
		}		
	}
	
	public boolean sentencesAvailableInText(String text, int countOfSentences)
	{
		// \\p{Lu} means matching uppercase chars in any language (also cyrillic, like [A-Z] for german)
		return text.toString().split("([\\?\\.!][\\s][\\p{Lu}])", countOfSentences + 2).length >= countOfSentences;
	}

	public boolean checkIfPaginationURL(String link)
	{
		if (link.matches(".*\\d.*"))
		{
			link = link.toLowerCase();

			if (link.matches(".*[\\?|&]p=\\d.*")) // many sites
				return true;
			if (link.matches(".*[\\?|&]page=\\d.*")) // many sites
				return true;
			if (link.matches(".*page/\\d.*")) // wordpress
				return true;
			if (link.matches(".*/page\\d.*")) // familie.de
				return true;
			if (link.matches(".*[\\?|&]seite=\\d.*")) // heise
				return true;
			if (link.matches(".*[\\?|&]startindex=\\d.*")) // many sites
				return true;
			if (link.matches(".*(-|/)\\d{1,2}(?!\\d).htm.*")) // many sites like www.test.de/test-2.htm
				return true;
		}
		return false;
	}

	public boolean isNumeric(String str)
	{
		try
		{
			double d = Double.parseDouble(str);
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}

	public void run()
	{
		try
		{
			analyzeSourceCode();
			synchronized (worker)
			{
				urlAfterCrawling.setExternalLinksDifferentDomainsOnThisPage(externalLinksDifferentDomainsOnThisPage.size());
				worker.getUrlManager().updateURL(urlAfterCrawling, urlBeforeCrawling);

				if (!(worker instanceof ListJobWorker))
				{
					worker.setProjectSummary(worker.getProjectSummaryManager().updateProjectSummaryWithoutPersist(worker.getProjectSummary(), urlAfterCrawling));

					// check is plugin (pagerank feature) is available...
					if (BotConfig.isPluginAvailable())
					{
						// use max 55 Mio links for pageRank calc...
						if (worker.getProjectSummary().getTotalLinks() < 55000000)
						{
							// add links to link graph storage for pagerank-calculation...
							for (int i = 0; i < differentLinksOnThisPage.size(); i++)
							{
								if (!differentLinksOnThisPage.get(i).isExternalLink())
								{
									// remove trailingslashes to avoid different results for www.test.de/ and www.test.de
									worker.getUrlManager().addLinkToGraph(dn.removeTrailingSlash(urlAfterCrawling.getURLName()), dn.removeTrailingSlash(differentLinksOnThisPage.get(i).getURLName()));
								}
							}
						}
					}
				}
			}

		}
		finally
		{
			runningThreadCounter.decrementAndGet();
		}
	}
}
