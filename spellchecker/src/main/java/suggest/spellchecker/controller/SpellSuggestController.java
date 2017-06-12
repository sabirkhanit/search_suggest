package suggest.spellchecker.controller;

import java.io.IOException;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import suggest.spellchecker.exception.SuggestAPIRuntimeException;
import suggest.spellchecker.service.SuggestService;
import suggest.spellchecker.util.Loggers;


@RestController
@RequestMapping("/suggest")
@Validated
public class SpellSuggestController {
	
	private static Logger logger = LoggerFactory.getLogger(Loggers.SUGGEST_LOGGER.toString());
	
	@Autowired
	private SuggestService suggestService;
	
	@RequestMapping(method = RequestMethod.GET, value = "/singleword", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String[]> suggest(@NotEmpty @RequestParam String word){
		
		String[] results = null;
		
		logger.debug("Suggest API is called for word : "+word);
		
		try {
			results = suggestService.singleWordSuggest(word);
			
			logger.debug("Results after search");
			
			for(String result : results ){
				logger.debug(result);
			}
			
			
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new SuggestAPIRuntimeException("IOException");
		}
		
		return new ResponseEntity<>(results,HttpStatus.OK);
		
	}
	
	

}
