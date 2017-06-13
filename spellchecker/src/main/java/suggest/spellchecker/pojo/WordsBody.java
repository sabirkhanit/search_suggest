package suggest.spellchecker.pojo;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class WordsBody {

	@NotEmpty
	@NotNull
	private List<String> wordList;

	/**
	 * @return the wordList
	 */
	public List<String> getWordList() {
		return wordList;
	}

	/**
	 * @param wordList the wordList to set
	 */
	public void setWordList(List<String> wordList) {
		this.wordList = wordList;
	}

	
	
}
