package de.solidsearch.bot.restservices;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.solidsearch.bot.dao.restservices.CrawlingJobRestManager;
import de.solidsearch.bot.utils.BotConfig;
import de.solidsearch.shared.data.CrawlingJob;


@Controller
public class CrawlingJobService
{
	private final Logger logger = Logger.getLogger(CrawlingJobService.class.getCanonicalName());
	
	@Autowired
	BotConfig config;
	@Autowired
	CrawlingJobRestManager crawlingJobManager;
	
	// example : http://localhost:8080/bot/rest/placecrawlingjob?remoteKey=AADSXESDW123CC
	@RequestMapping(value = "placecrawlingjob", method = RequestMethod.POST)
	public ResponseEntity<Long> placeCrawlingJob(@RequestBody CrawlingJob crawlingjob, @RequestParam String remoteKey) {
		
		if (!remoteKey.equalsIgnoreCase(config.REMOTEKEY))
		{
			logger.warn("RemoteKey for crawlingJob service was invalid...");
			return new ResponseEntity<Long>(0l, HttpStatus.FORBIDDEN);
		}
		
		long projectId = crawlingJobManager.placeCrawlingJob(crawlingjob);
		
		if (projectId == -1 )
			return new ResponseEntity<Long>(-1l, HttpStatus.BAD_REQUEST);
		
		return new ResponseEntity<Long>(projectId,HttpStatus.OK);
	}
	
	// example : http://localhost:8080/bot/rest/deletecrawlingjobdata?projectId=1&remoteKey=AADSXESDW123CC
	@RequestMapping(value = "deletecrawlingjobdata", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Boolean> deleteCrawlingJobProject(@RequestParam Long projectId, @RequestParam String remoteKey) {
		if (!remoteKey.equalsIgnoreCase(config.REMOTEKEY))
		{
			logger.warn("RemoteKey for deletecrawlingjobdata service was invalid...");
			return new ResponseEntity<Boolean>(HttpStatus.FORBIDDEN);
		}
		
		if (!crawlingJobManager.deleteCrawlingJobProject(projectId))
		{
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);
		}
		
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
	
	// example : http://localhost:8080/bot/rest/checkCrawlingJobStatus?remoteKey=AADSXESDW123CC
	@RequestMapping(value = "checkCrawlingJobStatus", method = RequestMethod.POST)
	public ResponseEntity<Short> checkCrawlingJobStatus(@RequestBody CrawlingJob crawlingjob, @RequestParam String remoteKey) {
		
		if (!remoteKey.equalsIgnoreCase(config.REMOTEKEY))
		{
			logger.warn("RemoteKey for iscrawlingjobfinished service was invalid...");
			return new ResponseEntity<Short>((short)-1, HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<Short>(crawlingJobManager.checkCrawlingJobStatus(crawlingjob),HttpStatus.OK);
	}
	
	// example : http://localhost:8080/bot/rest/getInfoMessages?remoteKey=AADSXESDW123CC&projectId=1
	@RequestMapping(value = "getInfoMessages", method = RequestMethod.GET)
	public ResponseEntity<String[]> getInfoMessages(@RequestParam Long projectId, @RequestParam String remoteKey) {
		
		if (!remoteKey.equalsIgnoreCase(config.REMOTEKEY))
		{
			logger.warn("RemoteKey for getInfoMessages service was invalid...");
			return new ResponseEntity<String[]>(new String[]{}, HttpStatus.FORBIDDEN);
		}
		
		String[] messages = crawlingJobManager.getInfoMessages(projectId);
		
		if (messages == null)
		{
			return new ResponseEntity<String[]>(new String[]{}, HttpStatus.NOT_FOUND);
		}
		else
		{
			return new ResponseEntity<String[]>(messages,HttpStatus.OK);	
		}
	}
	
	// example : http://localhost:8080/bot/rest/doesprojectexist?remoteKey=AADSXESDW123CC&projectId=1
	@RequestMapping(value = "doesprojectexist", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Boolean> doesProjectExist(@RequestParam Long projectId, @RequestParam String remoteKey) {
		if (!remoteKey.equalsIgnoreCase(config.REMOTEKEY))
		{
			logger.warn("RemoteKey for doesProjectExist service was invalid...");
			return new ResponseEntity<Boolean>(HttpStatus.FORBIDDEN);
		}
		
		if (!crawlingJobManager.checkIfProjectExists(projectId))
		{
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);
		}
		
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
}
