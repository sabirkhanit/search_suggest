package suggest.spellchecker.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
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
	
	public static final String F_WORD = "word";
	
	//This lock needs to be shared among all apps / services trying to modify index
	//The way - its done currently, will not be ready for clustered deployment 
	private final Object modifyCurrentIndexLock = new Object();

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
		
		synchronized(modifyCurrentIndexLock){
		
		try(Directory spellIndex = FSDirectory.open(new File(indexLocation).toPath());
			SpellChecker spellchecker = new SpellChecker(spellIndex); ) {	
			
			PlainTextDictionary ptDictionary = new PlainTextDictionary(getFilePath());
			
			SimpleAnalyzer analyzer = new SimpleAnalyzer();
			IndexWriterConfig wConfig = new IndexWriterConfig(analyzer);
			wConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
			spellchecker.indexDictionary(ptDictionary, wConfig, true);
		} 
		}
		return;
	}
	
	@Override
	public void appendWords(Set<String> words) throws IOException{
		
		synchronized(modifyCurrentIndexLock){
		
		IndexWriterConfig wConfig = new IndexWriterConfig(new SimpleAnalyzer());
		wConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		
		try(Directory spellIndex = FSDirectory.open(new File(indexLocation).toPath());
			SpellChecker spellchecker = new SpellChecker(spellIndex); 
			IndexWriter writer = new IndexWriter(spellIndex, wConfig);)
		{
			for(String word:words){
				if(!spellchecker.exist(word)){
					
					logger.debug("Word -> "+word +" doesn't exist in dictionary to trying to add to index");
					Document doc = createDocument(word, getMin(word.length()), getMax(word.length()));
					writer.addDocument(doc);
					writer.commit();
				}
			}	
		}
		
		}
		logger.debug("All valid the words added to dictionary");
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
	
	/*
	 * These below four methods codes are copied from Lucene SpellChecker class API - getMin, getMax, createDocument & addGram
	 */
	
	private static int getMin(int l) {
	    if (l > 5) {
	      return 3;
	    }
	    if (l == 5) {
	      return 2;
	    }
	    return 1;
	  }

	  private static int getMax(int l) {
	    if (l > 5) {
	      return 4;
	    }
	    if (l == 5) {
	      return 3;
	    }
	    return 2;
	  }

	
	private static Document createDocument(String text, int ng1, int ng2) {
	    Document doc = new Document();
	    Field f = new StringField(F_WORD, text, Field.Store.YES);
	    doc.add(f); 
	    addGram(text, doc, ng1, ng2);
	    return doc;
	  }
	
	private static void addGram(String text, Document doc, int ng1, int ng2) {
	    int len = text.length();
	    for (int ng = ng1; ng <= ng2; ng++) {
	      String key = "gram" + ng;
	      String end = null;
	      for (int i = 0; i < len - ng + 1; i++) {
	        String gram = text.substring(i, i + ng);
	        FieldType ft = new FieldType(StringField.TYPE_NOT_STORED);
	        ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
	        Field ngramField = new Field(key, gram, ft);

	        doc.add(ngramField);
	        if (i == 0) {
	          Field startField = new StringField("start" + ng, gram, Field.Store.NO);
	          doc.add(startField);
	        }
	        end = gram;
	      }
	      if (end != null) { 
	        Field endField = new StringField("end" + ng, end, Field.Store.NO);
	        doc.add(endField);
	      }
	    }
	  }

}
