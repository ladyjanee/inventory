package com.productinventory.product_inventory.dto;

/**
 * Data Transfer Object (DTO) for updating existing products in the inventory system.
 * This class extends BaseRequest to inherit common product fields and validation rules.
 *
 * Purpose: Used exclusively for PUT operations to modify existing products.
 * The class ensures data integrity through validation annotations inherited from BaseRequest.
 * All fields are optional for partial updates - null values indicate no change.
 *
 * Key Features:
 * - Inherits validated fields: name, description, price, quantity
 * - Provides constructors for flexible object creation
 * - Supports partial updates (fields can be null)
 * - Enforces business rules through Jakarta validation annotations
 */
public class UpdateProductRequest extends BaseRequest {

    /**
     * Default constructor for UpdateProductRequest.
     * Creates an empty instance that can be populated using setter methods.
     * Useful for frameworks that require no-argument constructors and for partial updates
     * where only some fields need to be modified.
     */
    public UpdateProductRequest() {
        super();
    }

    /**
     * Parameterized constructor for UpdateProductRequest.
     * Allows creating a fully initialized product update request in a single step.
     * Pass null values for fields that should not be updated.
     *
     * @param name        The new product name (null to keep existing, 1-255 characters when provided)
     * @param description The new product description (null to keep existing, max 1000 characters when provided)
     * @param price       The new product price (null to keep existing, must be positive when provided)
     * @param quantity    The new stock quantity (null to keep existing, can be zero or positive when provided)
     */
    public UpdateProductRequest(String name, String description, Double price, Integer quantity) {
        // Step 1: Call parent constructor to initialize inherited fields
        // This ensures proper inheritance chain initialization
        super();

        // Step 2: Set the product name for update
        // null values indicate no change to the existing name
        // When provided, @NotBlank and @Size constraints apply
        this.name = name;

        // Step 3: Set the product description for update
        // null values indicate no change to the existing description
        // When provided, @Size constraint applies (max 1000 characters)
        this.description = description;

        // Step 4: Set the product price for update
        // null values indicate no change to the existing price
        // When provided, @NotNull and @Positive constraints apply
        this.price = price;

        // Step 5: Set the quantity for update
        // null values indicate no change to the existing quantity
        // When provided, @NotNull constraint applies
        this.quantity = quantity;
    }
}
