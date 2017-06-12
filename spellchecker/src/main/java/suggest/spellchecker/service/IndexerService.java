package suggest.spellchecker.service;

import java.io.IOException;
import java.util.Set;

public interface IndexerService {

	public void indexWordsFile() throws IOException;
	
	public void appendWords(Set<String> words) throws IOException;
}
