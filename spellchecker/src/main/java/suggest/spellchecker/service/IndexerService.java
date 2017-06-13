package suggest.spellchecker.service;

import java.io.IOException;
import java.util.Set;

public interface IndexerService {

	public Boolean indexWordsFile() throws IOException;
	
	public Boolean addWords(Set<String> words) throws IOException;
	
	public Boolean removeWords(Set<String> words) throws IOException;
}
