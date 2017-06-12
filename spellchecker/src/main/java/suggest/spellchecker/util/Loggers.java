package suggest.spellchecker.util;

public enum Loggers {

	INDEXER_LOGGER("SpellCheckerIndexerLogger"),
	
	SUGGEST_LOGGER("SpellCheckerLogger");
	
	private String logger;
	
	private Loggers(final String logger) {
		this.logger = logger;
	}
	
	@Override
	public String toString(){
		return this.logger;
	}
	
}
