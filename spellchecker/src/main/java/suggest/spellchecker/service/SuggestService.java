package suggest.spellchecker.service;

import java.io.IOException;

public interface SuggestService {

	public String[] singleWordSuggest(String word) throws IOException;
}
