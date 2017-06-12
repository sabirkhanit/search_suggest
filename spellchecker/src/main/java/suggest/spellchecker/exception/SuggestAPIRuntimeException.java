package suggest.spellchecker.exception;

public class SuggestAPIRuntimeException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public SuggestAPIRuntimeException(String msg) {
		super(msg);
	}
	
	public SuggestAPIRuntimeException(Throwable t) {
		super(t);
	}
	
	public SuggestAPIRuntimeException(String msg,Throwable t) {
		super(msg,t);
	}

}
