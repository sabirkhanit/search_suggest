package suggest.spellchecker.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import suggest.spellchecker.exception.SuggestAPIRuntimeException;
import suggest.spellchecker.util.Loggers;

@Service
public class IndexerServiceImpl implements IndexerService{
	
	private static Logger logger = LoggerFactory.getLogger(Loggers.INDEXER_LOGGER.toString());

	@Value("${spellchecker.initialize.words.location}:classpath")
	private String wordsLocation;
	
	//If supplying external words file, this needs to be full file path
	@Value("${spellchecker.initialize.words.fileName:classpath:words.txt}")
	private String wordsFileName;
	
	@Value("${spellchecker.initialize.index.directory}")
	private String indexLocation;
	
	@Override
	public void indexWordsFile() throws IOException{
		
		logger.debug("Words File Location : "+wordsLocation+" and fileName ="+wordsFileName);
		
		try(Directory spellIndex = FSDirectory.open(new File(indexLocation).toPath());
			SpellChecker spellchecker = new SpellChecker(spellIndex); ) {	
			
			 PlainTextDictionary ptDictionary = new PlainTextDictionary(getFilePath());
			
			SimpleAnalyzer analyzer = new SimpleAnalyzer();
			IndexWriterConfig wConfig = new IndexWriterConfig(analyzer);
			wConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
			spellchecker.indexDictionary(ptDictionary, wConfig, true);
		} 
		return;
	}
	
	private Path getFilePath(){
		
		Path path;

		if(StringUtils.equals("external", wordsLocation)){
			path= new File(wordsFileName).toPath();
		}else{
			try {
				path= Paths.get(ClassLoader.getSystemResource(wordsFileName).toURI());
			} catch (URISyntaxException e) {
				logger.error(e.getMessage(),e);
				throw new SuggestAPIRuntimeException("URISyntaxException");
			}
		}
		
		return path;
		
	}

}
