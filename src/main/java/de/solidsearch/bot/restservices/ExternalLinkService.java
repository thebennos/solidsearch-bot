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

import de.solidsearch.bot.dao.restservices.ExternalLinkRestManager;
import de.solidsearch.bot.utils.BotConfig;
import de.solidsearch.shared.data.SharedExternalLink;


@Controller
public class ExternalLinkService
{
	private final Logger logger = Logger.getLogger(ExternalLinkService.class.getCanonicalName());
	
	@Autowired
	BotConfig config;
	@Autowired
	ExternalLinkRestManager externalLinkRestManager;
	
	
	// watch-out: parameter are case sensitive!!
	// Example: http://localhost:8080/solidsearch/rest/externallinksbyproject?projectID=1&page=0&remoteKey=AADSXESDW123CC
	
	@RequestMapping(value = "/externallinksbyproject", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SharedExternalLink>> getKeywordsByProject(@RequestParam Long projectID, @RequestParam Integer page, @RequestParam String remoteKey) {
		if (!remoteKey.equalsIgnoreCase(config.REMOTEKEY))
		{
			logger.warn("RemoteKey for externallinksbyproject service was invalid...");
			return new ResponseEntity<List<SharedExternalLink>>(new ArrayList<SharedExternalLink>(), HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<List<SharedExternalLink>>(externalLinkRestManager.getNewestExternalLinksByProjectID(projectID,page), HttpStatus.OK);
	}
	
}
