package com.productinventory.product_inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Base DTO class containing common validation logic for product requests.
 * This class defines the common fields and validation rules that apply to both
 * create and update product operations.
 */
public abstract class BaseRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 255, message = "Product name must be between 1 and 255 characters")
    protected String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    protected String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    protected Double price;

    @NotNull(message = "Quantity is required")
    protected Integer quantity;

    public BaseRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
