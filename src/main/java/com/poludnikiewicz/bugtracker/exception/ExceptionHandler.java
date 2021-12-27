package com.poludnikiewicz.bugtracker.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);



    @org.springframework.web.bind.annotation.ExceptionHandler(value = { IllegalStateException.class })
    public ResponseEntity<Object> handleInvalidInputException(IllegalStateException ex) {
        LOGGER.error("Invalid Input Exception: ",ex.getMessage());

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        LOGGER.error("Constraint violation exception: ", ex.getMessage());

        return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {IllegalAccessException.class})
    public ResponseEntity<Object> handleIllegalAccessException(IllegalAccessException ex) {
        LOGGER.error("Illegal access exception: ", ex.getMessage());

        return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.UNAUTHORIZED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {BugNotFoundException.class})
    public ResponseEntity<Object> BugNotFoundException(BugNotFoundException ex) {
        LOGGER.error("bug not found exception: ", ex.getMessage());

        return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> details = new ArrayList<>();
        for(ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        ErrorResponse error = new ErrorResponse("Validation Failed", details);
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);

    }

}
