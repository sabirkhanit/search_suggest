package suggest.spellchecker.pojo;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;



@Component
public class SuggestAPIResponse<T>{

	private HttpStatus status;
	private String message;
	private List<String> errors;
	private T body;
	
	public SuggestAPIResponse(){
		//Empty constructor
	}
	
	/**
	 * 
	 * @param status
	 * @param message
	 * Will be used to construct success responses with messages like , OK etc
	 */
	public SuggestAPIResponse(HttpStatus status, String message){
		this.status = status;
		this.message = message;
		this.errors=null;
	}
	
	public SuggestAPIResponse(HttpStatus status, String message, T body){
		this.status = status;
		this.message = message;
		this.body=body;
		this.errors=null;
	}
	
	public SuggestAPIResponse(HttpStatus status, String message, List<String> errors) {
		this.status = status;
		this.message = message;
		this.errors = errors;
		this.body=null;
	}

	public SuggestAPIResponse(HttpStatus status, String message, String error) {
		this.status = status;
		this.message = message;
		errors = Arrays.asList(error);
		this.body=null;
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

	/**
	 * @return the body
	 */
	public T getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(T body) {
		this.body = body;
	}
	
	
	
}
