package suggest.spellchecker.service;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SuggestServiceImpl implements SuggestService {

	@Value("${spellchecker.initialize.index.directory}")
	private String indexLocation;
	
	@Value("${spellchecker.suggest.suggestCount}")
	private int suggestCount;
	
	@Override
	public String[] singleWordSuggest(String word) throws IOException {
		
		String[] response ;
		
		try(Directory spellIndex = FSDirectory.open(new File(indexLocation).toPath());
				SpellChecker spellchecker = new SpellChecker(spellIndex); ) {	
			if (!spellchecker.exist(word)) {
				response = spellchecker.suggestSimilar(word, suggestCount);
			}else{
				response=new String[]{word};
			}
		}
		return response;
	}

}
