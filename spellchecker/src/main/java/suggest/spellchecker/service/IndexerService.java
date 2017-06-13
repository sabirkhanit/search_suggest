package suggest.spellchecker.service;

import java.io.IOException;
import java.util.Set;

public interface IndexerService {

	public void indexWordsFile() throws IOException;
	
	public void addWords(Set<String> words) throws IOException;
	
	public void removeWords(Set<String> words) throws IOException;
}
