package de.solidsearch.bot.restservices;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.solidsearch.bot.dao.restservices.URLRestManager;
import de.solidsearch.bot.utils.BotConfig;
import de.solidsearch.shared.data.SharedDocument;


@Controller
public class URLService
{
	private final Logger logger = Logger.getLogger(URLService.class.getCanonicalName());
	
	@Autowired
	BotConfig config;
	@Autowired
	URLRestManager urlRestManager;
	
	
	// watch-out: parameter are case sensitive!!
	// Example: http://localhost:8080/solidsearch/rest/urlsbyproject?projectID=1&fromTimeStamp=0&remoteKey=AADSXESDW123CC
	
	@RequestMapping(value = "/urlsbyproject", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SharedDocument>> getKeywordsByProject(@RequestParam Long projectID, @RequestParam Long fromTimeStamp, @RequestParam String remoteKey) {
		if (!remoteKey.equalsIgnoreCase(config.REMOTEKEY))
		{
			logger.warn("RemoteKey for urlsbyproject service was invalid...");
			return new ResponseEntity<List<SharedDocument>>(new ArrayList<SharedDocument>(), HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<List<SharedDocument>>(urlRestManager.getNewestURLsByProjectID(projectID,fromTimeStamp), HttpStatus.OK);
	}
	
}
