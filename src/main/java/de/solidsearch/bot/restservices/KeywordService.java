package de.solidsearch.bot.restservices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.solidsearch.bot.dao.restservices.KeywordRestManager;
import de.solidsearch.bot.utils.BotConfig;


@Controller
@RequestMapping("/keywordsbyproject")
public class KeywordService
{
	@Autowired
	BotConfig config;
	@Autowired
	KeywordRestManager keywordManager;
	
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public @ResponseBody String getDefault() {
		return "list";
 
	}
	//http://localhost:8080/solidsearch/rest/keywordsbyproject/1/0?remoteKey=AADSXESDW123CC
	@RequestMapping(value = "/{projectId}/{page}", method = RequestMethod.GET)
	public @ResponseBody List getKeywordsByProject(@PathVariable Long projectId, @PathVariable Integer page) {
		List keywords = keywordManager.getKeywordsByProject(projectId,page);
		return keywords;
	}
	
}
