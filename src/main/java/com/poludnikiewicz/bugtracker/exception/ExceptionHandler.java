package com.poludnikiewicz.bugtracker.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex) {
        LOGGER.error("Illegal State Exception: ",ex.getMessage());

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
    public ResponseEntity<Object> handleBugNotFoundException(BugNotFoundException ex) {
        LOGGER.error("bug not found exception: ", ex.getMessage());

        return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {CommentNotFoundException.class})
    public ResponseEntity<Object> handleCommentNotFoundException(CommentNotFoundException ex) {
        LOGGER.error("Comment not found exception: ", ex.getMessage());

        return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        LOGGER.error("Access denied exception: ", ex.getMessage());

        return new ResponseEntity<>("With your current user role you are not authorized to access this address.",
                HttpStatus.FORBIDDEN);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        LOGGER.error("Illegal argument exception: ", ex.getMessage());

        return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {ApplicationUserNotFoundException.class})
    public ResponseEntity<Object> handleApplicationUserNotFoundException(ApplicationUserNotFoundException ex) {
        LOGGER.error("Application user not found exception: ", ex.getMessage());

        return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
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
