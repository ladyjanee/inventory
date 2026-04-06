package com.productinventory.product_inventory.response;

import java.util.Map;

/**
 * StandardResponse wrapper class for standardizing all API responses.
 * Provides a consistent response format across the entire application.
 * Structure: { "success": boolean, "data": Object, "errors": Map }
 */
public class StandardResponse<T> {

    private boolean success;
    private T data;
    private Map<String, String> errors;

    /**
     * Constructor for successful responses with data.
     */
    public StandardResponse(boolean success, T data) {
        this.success = success;
        this.data = data;
        this.errors = null;
    }

    /**
     * Constructor for responses with errors.
     */
    public StandardResponse(boolean success, T data, Map<String, String> errors) {
        this.success = success;
        this.data = data;
        this.errors = errors;
    }

    /**
     * Static factory method for successful responses.
     */
    public static <T> StandardResponse<T> success(T data) {
        return new StandardResponse<>(true, data);
    }

    /**
     * Static factory method for error responses.
     */
    public static <T> StandardResponse<T> error(T data, Map<String, String> errors) {
        return new StandardResponse<>(false, data, errors);
    }

    /**
     * Static factory method for validation error responses.
     */
    public static <T> StandardResponse<T> validationError(Map<String, String> errors) {
        return new StandardResponse<>(false, null, errors);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
