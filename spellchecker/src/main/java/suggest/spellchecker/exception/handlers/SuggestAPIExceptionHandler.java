package suggest.spellchecker.exception.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import suggest.spellchecker.exception.SuggestAPIRuntimeException;
import suggest.spellchecker.pojo.SuggestAPIResponse;

@RestControllerAdvice
public class SuggestAPIExceptionHandler {

	
	/**
	 * This is handler for @Valid annotation since that throws
	 * MethodArgumentNotValidException This is used for validating POJO beans
	 * with custom validators for POST requests
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<SuggestAPIResponse> handle(MethodArgumentNotValidException exception) {
		List<String> errors = new ArrayList<>();
		for (FieldError error : exception.getBindingResult().getFieldErrors()) {
			errors.add(error.getField() + ": " + error.getDefaultMessage());
		}
		for (ObjectError error : exception.getBindingResult().getGlobalErrors()) {
			errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
		}
		SuggestAPIResponse apiError = new SuggestAPIResponse(HttpStatus.BAD_REQUEST, "MESSAGE_REQUEST_VALIDATION_FAILURE", errors);
		return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
		
	}
	
	
	
	/**
	 * This is handler for @Validated annotation since that throws
	 * ConstraintViolationException & not MethodArgumentNotValidException This
	 * is used for validating @RequestParam & @PathVariable
	 */
	
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<SuggestAPIResponse> handle(ConstraintViolationException exception) {
		List<String> errors = new ArrayList<>();
		int count = 1;
		for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
			errors.add(" " + count + "." + violation.getMessage());
			++count;
		}
		SuggestAPIResponse apiError = new SuggestAPIResponse(HttpStatus.BAD_REQUEST, "MESSAGE_REQUEST_VALIDATION_FAILURE", errors);
		return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
		
	}

	/**
	 * This is handler for SuggestAPIRuntimeException
	 */
	@ExceptionHandler(SuggestAPIRuntimeException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<SuggestAPIResponse> handle(SuggestAPIRuntimeException ex) {
		List<String> errors = new ArrayList<>();
		errors.add(ex.getLocalizedMessage());
		SuggestAPIResponse apiError = new SuggestAPIResponse(HttpStatus.INTERNAL_SERVER_ERROR, "MESSAGE_INTERNAL_SERVER_ERROR", errors);
		return new ResponseEntity<>(apiError,HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
