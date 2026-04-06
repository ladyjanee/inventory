package com.productinventory.product_inventory.dto;

/**
 * Data Transfer Object (DTO) for creating new products in the inventory system.
 * This class extends BaseRequest to inherit common product fields and validation rules.
 *
 * Purpose: Used exclusively for POST operations to create new products.
 * The class ensures data integrity through validation annotations inherited from BaseRequest.
 *
 * Key Features:
 * - Inherits validated fields: name, description, price, quantity
 * - Provides constructors for flexible object creation
 * - Enforces business rules through Jakarta validation annotations
 */
public class CreateProductRequest extends BaseRequest {

    /**
     * Default constructor for CreateProductRequest.
     * Creates an empty instance that can be populated using setter methods.
     * Useful for frameworks that require no-argument constructors (e.g., Jackson for JSON deserialization).
     */
    public CreateProductRequest() {
        super();
    }

    /**
     * Parameterized constructor for CreateProductRequest.
     * Allows creating a fully initialized product creation request in a single step.
     *
     * @param name        The product name (required, 1-255 characters)
     * @param description The product description (optional, max 1000 characters)
     * @param price       The product price (required, must be positive)
     * @param quantity    The initial stock quantity (required, can be zero or positive)
     */
    public CreateProductRequest(String name, String description, Double price, Integer quantity) {
        // Step 1: Call parent constructor to initialize inherited fields
        // This ensures proper inheritance chain initialization
        super();

        // Step 2: Set the product name with validation
        // The name field has @NotBlank and @Size constraints
        this.name = name;

        // Step 3: Set the product description
        // The description field has @Size constraint (max 1000 characters)
        this.description = description;

        // Step 4: Set the product price with validation
        // The price field has @NotNull and @Positive constraints
        this.price = price;

        // Step 5: Set the initial quantity with validation
        // The quantity field has @NotNull constraint
        this.quantity = quantity;
    }
}
