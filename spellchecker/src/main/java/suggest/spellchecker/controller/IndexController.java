package suggest.spellchecker.controller;


import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import suggest.spellchecker.exception.SuggestAPIRuntimeException;
import suggest.spellchecker.service.IndexerService;
import suggest.spellchecker.util.Loggers;

@RestController
@RequestMapping("/index")
public class IndexController {

	
	private static Logger logger = LoggerFactory.getLogger(Loggers.INDEXER_LOGGER.toString());
	
	@Autowired
	private IndexerService indexService;
	
	@Value("${spellchecker.initialize.buildindex:false}")
	protected boolean initialIndex;
	
	@RequestMapping(method = RequestMethod.POST, value = "/indexfile", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> buildInitialIndex(){
		
		ResponseEntity<Boolean> response = new ResponseEntity<>(initialIndex,HttpStatus.OK);
		
		if(initialIndex) {
			logger.debug("indexfile is allowed");
			try {
				indexService.indexWordsFile();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
				throw new SuggestAPIRuntimeException("IOException");
			}
		}else{
			logger.debug("indexfile is not allowed");
		}
		
		return response;
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/indexwords", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> indexWords(@RequestBody List<String> words){
		
		new HashSet<String>(words);
		
		ResponseEntity<Boolean> response = new ResponseEntity<>(initialIndex,HttpStatus.OK);
		
		
		return response;
	}
}
