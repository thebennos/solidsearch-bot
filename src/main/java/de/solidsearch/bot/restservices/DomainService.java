package de.solidsearch.bot.restservices;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.solidsearch.bot.dao.restservices.DomainRestManager;
import de.solidsearch.bot.utils.BotConfig;
import de.solidsearch.shared.data.SharedDomain;


@Controller
public class DomainService
{
	private final Logger logger = Logger.getLogger(DomainService.class.getCanonicalName());
	
	@Autowired
	BotConfig config;
	@Autowired
	DomainRestManager domainRestManager;
	
	
	// watch-out: parameter are case sensitive!!
	// Example: http://localhost:8080/solidsearch/rest/externallinksbyproject?projectID=1&page=0&remoteKey=AADSXESDW123CC
	
	@RequestMapping(value = "/domaindatabyproject", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<SharedDomain> getKeywordsByProject(@RequestParam Long projectID, @RequestParam String remoteKey) {
		if (!remoteKey.equalsIgnoreCase(config.REMOTEKEY))
		{
			logger.warn("RemoteKey for externallinksbyproject service was invalid...");
			return new ResponseEntity<SharedDomain>(new SharedDomain(), HttpStatus.FORBIDDEN);
		}
		
		SharedDomain domainData = domainRestManager.getNewestDomainDataByProjectID(projectID);
		
		if (domainData == null)
			return new ResponseEntity<SharedDomain>(domainData, HttpStatus.BAD_REQUEST);
		else 
			return new ResponseEntity<SharedDomain>(domainData, HttpStatus.OK);
	}
	
}
