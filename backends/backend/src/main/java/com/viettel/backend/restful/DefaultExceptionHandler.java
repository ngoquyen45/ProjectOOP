package com.viettel.backend.restful;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.mongodb.MongoException;
import com.viettel.backend.exeption.BusinessException;
import com.viettel.backend.oauth2.core.InvalidTicketException;

/**
 * @author thanh
 */
@ControllerAdvice
public class DefaultExceptionHandler {
    
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        logger.error("Unknow error", e);
        RestError error = new RestError("unknown", 500, "Unknown error occurs");
        Envelope response = new Envelope(error);
        return new ResponseEntity<Envelope>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(value = NoHandlerFoundException.class)
    public ResponseEntity<?> notFoundErrorHandler(HttpServletRequest req, NoHandlerFoundException e) throws Exception {
        logger.debug("ResourceNotFoundException error", e);
        RestError error = new RestError("ResourceNotFoundException", 404, "Requested resource not found");
        Envelope response = new Envelope(error);
        return new ResponseEntity<Envelope>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<?> businessErrorHandler(HttpServletRequest req, BusinessException e) throws Exception {
        logger.debug("BusinessException error", e);
        RestError error = new RestError("BusinessException", 400, e.getCode());
        Envelope response = new Envelope(error);
        return new ResponseEntity<Envelope>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> methodNotSupportErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        logger.debug("MethodNotSupportedException error", e);
        RestError error = new RestError("MethodNotSupportedException", 400, "Method not supported");
        Envelope response = new Envelope(error);
        return new ResponseEntity<Envelope>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(value = MongoException.class)
    public ResponseEntity<?> mongoErrorHandler(HttpServletRequest req, Exception e) throws Exception {
    	logger.error("Mongo error", e);
        RestError error = new RestError("unknown", 500, "Unknown error occurs");
        Envelope response = new Envelope(error);
        return new ResponseEntity<Envelope>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(value = {
            IllegalArgumentException.class, MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class
            })
    public ResponseEntity<?> illegalErrorHandler(HttpServletRequest req, Exception e) throws Exception {
    	logger.error("IllegalArgument error", e);
        RestError error = new RestError("IllegalArgumentException", 400, "Invalid request parameter");
        Envelope response = new Envelope(error);
        return new ResponseEntity<Envelope>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(value = {HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<?> mediaTypeNotSupportErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        logger.debug("HttpMediaTypeNotSupported error", e);
        RestError error = new RestError("MediaTypeNotSupportedException", 400, "MediaType not supported");
        Envelope response = new Envelope(error);
        return new ResponseEntity<Envelope>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(value = {InvalidTicketException.class})
    public String invalidTicketErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        logger.trace("InvalidTicketException error", e);
        return "401";
    }
    
}
