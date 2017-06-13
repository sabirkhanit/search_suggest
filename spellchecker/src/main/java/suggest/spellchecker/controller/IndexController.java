package suggest.spellchecker.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
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
import suggest.spellchecker.pojo.SuggestAPIResponse;
import suggest.spellchecker.pojo.WordsBody;
import suggest.spellchecker.service.IndexerService;
import suggest.spellchecker.util.Loggers;
import suggest.spellchecker.util.Message;

@RestController
@RequestMapping("/index")
public class IndexController {

	
	private static Logger logger = LoggerFactory.getLogger(Loggers.INDEXER_LOGGER.toString());
	
	@Autowired private IndexerService indexService;
	
	@Value("${spellchecker.initialize.buildindex:false}")
	private boolean isFileIndex;
	
	@RequestMapping(method = RequestMethod.POST, value = "/indexfile", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SuggestAPIResponse<Boolean>> buildBaselineIndex(){
		
		Boolean result = false;
		
		if(isFileIndex) {
			logger.info("indexfile is allowed");
			try {
				 result = indexService.indexWordsFile();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
				throw new SuggestAPIRuntimeException(Message.IOEXCEPTION.toString());
			}
		}else{
			logger.info("indexfile is not allowed");
		}
		return new ResponseEntity<>(new SuggestAPIResponse<>(HttpStatus.OK,Message.SUCCESS.toString(),result),HttpStatus.OK);
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/addwords", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SuggestAPIResponse<Boolean>> addWords(@Valid @RequestBody WordsBody words ){
		
		Set<String> uniqueWords = new HashSet<>(words.getWordList());
		uniqueWords.remove(StringUtils.EMPTY);
		
		Boolean result = false;
		
		if(!uniqueWords.isEmpty()){	
			try {
			result = indexService.addWords(uniqueWords);
			}catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new SuggestAPIRuntimeException(Message.IOEXCEPTION.toString());
			}
		}else{
			logger.info("With empty strings being removed from wordList, wordList is empty");
		}
		
		return new ResponseEntity<>(new SuggestAPIResponse<>(HttpStatus.OK,Message.SUCCESS.toString(),result),HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/removewords", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SuggestAPIResponse<Boolean>> removeWords(@Valid @RequestBody WordsBody words){
		
		Set<String> uniqueWords = new HashSet<>(words.getWordList());
		uniqueWords.remove(StringUtils.EMPTY);
		
		Boolean result = false;
		
		if(!uniqueWords.isEmpty()){
			
		try {
			result=indexService.removeWords(uniqueWords);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new SuggestAPIRuntimeException(Message.IOEXCEPTION.toString());
		}
		}else{
			logger.info("With empty strings being removed from wordList, wordList is empty");
		}
		
		return new ResponseEntity<>(new SuggestAPIResponse<>(HttpStatus.OK,Message.SUCCESS.toString(),result),HttpStatus.OK);
	}
	
}
