package suggest.spellchecker.pojo;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;



@Component
public class SuggestAPIResponse {

	private HttpStatus status;
	private String message;
	private List<String> errors;
	
	
	public SuggestAPIResponse(){
		//Empty constructor
	}
	
	public SuggestAPIResponse(HttpStatus status, String message, List<String> errors) {
		super();
		this.status = status;
		this.message = message;
		this.errors = errors;
	}

	public SuggestAPIResponse(HttpStatus status, String message, String error) {
		super();
		this.status = status;
		this.message = message;
		errors = Arrays.asList(error);
	}

	
	@PostConstruct
	public void defaultApiResponse() {
		this.status = HttpStatus.OK;
		this.message = StringUtils.EMPTY;
		this.errors = Arrays.asList(StringUtils.EMPTY);
	}
	
	
	/**
	 * @return the status
	 */
	public HttpStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the errors
	 */
	public List<String> getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	
	
	
}
