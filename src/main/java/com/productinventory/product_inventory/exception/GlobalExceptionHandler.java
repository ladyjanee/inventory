package com.productinventory.product_inventory.exception;

import com.productinventory.product_inventory.response.StandardResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler using @ControllerAdvice.
 * Provides centralized exception handling across the entire application.
 * Returns structured responses with validation error details for API requests.
 * For web requests, allows default Spring Boot error handling.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors from @Valid annotation.
     * Only handles API requests (JSON responses), not web requests.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) throws MethodArgumentNotValidException {

        // Check if this is an API request (accepts JSON)
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("application/json")) {
            Map<String, String> errors = new HashMap<>();

            ex.getBindingResult()
              .getFieldErrors()
              .forEach(error -> errors.put(
                  error.getField(),
                  error.getDefaultMessage()
              ));

            return new ResponseEntity<>(
                StandardResponse.validationError(errors),
                HttpStatus.BAD_REQUEST
            );
        }

        // For web requests, re-throw to let Spring Boot handle it
        throw ex;
    }

    /**
     * Handle type mismatch errors for path variables or request parameters.
     * Only handles API requests (JSON responses), not web requests.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        // Check if this is an API request (accepts JSON)
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("application/json")) {
            Map<String, String> errors = new HashMap<>();
            errors.put(ex.getName(), "Invalid value for parameter: " + ex.getValue());

            return new ResponseEntity<>(
                StandardResponse.validationError(errors),
                HttpStatus.BAD_REQUEST
            );
        }

        // For web requests, re-throw to let Spring Boot handle it
        throw ex;
    }

    /**
     * Handle generic runtime exceptions.
     * Only handles API requests (JSON responses), not web requests.
     */
    @ExceptionHandler(Exception.class)
    public Object handleGeneralException(Exception ex, HttpServletRequest request) throws Exception {

        // Check if this is an API request (accepts JSON)
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("application/json")) {
            Map<String, String> errors = new HashMap<>();
            errors.put("error", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred");

            return new ResponseEntity<>(
                StandardResponse.error(null, errors),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        // For web requests, re-throw to let Spring Boot handle it
        throw ex;
    }
}
