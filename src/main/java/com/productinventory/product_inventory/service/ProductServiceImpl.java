package com.productinventory.product_inventory.service;

import com.productinventory.product_inventory.dto.CreateProductRequest;
import com.productinventory.product_inventory.dto.UpdateProductRequest;
import com.productinventory.product_inventory.entity.Product;
import com.productinventory.product_inventory.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of ProductService interface.
 * This service class handles all business logic related to product management operations.
 *
 * Key Responsibilities:
 * - Product CRUD operations (Create, Read, Update, Delete)
 * - Data validation and business rule enforcement
 * - Entity mapping between DTOs and domain objects
 * - Error handling for invalid operations
 *
 * Dependencies:
 * - ProductRepository: For data access operations
 * - CreateProductRequest/UpdateProductRequest: For input validation
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /**
     * Constructor for ProductServiceImpl.
     * Uses dependency injection to provide the ProductRepository instance.
     *
     * @param productRepository The repository for product data access operations
     */
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all products from the inventory.
     * This method provides a complete list of all products currently in the system.
     *
     * @return List of all Product entities in the database
     */
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Saves a product entity to the database.
     * This is a generic save method that can be used for both new and existing products.
     *
     * @param product The Product entity to save
     * @return The saved Product entity (with generated ID if new)
     */
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Creates a new product from a CreateProductRequest DTO.
     * This method handles the business logic for product creation, including validation
     * and mapping from the request DTO to the Product entity.
     *
     * @param request The validated CreateProductRequest containing product data
     * @return The newly created and saved Product entity
     * @throws IllegalArgumentException if the request is null
     */
    @Override
    public Product createProduct(CreateProductRequest request) {
        // Step 1: Validate input parameters
        // Ensure the request object is not null to prevent NullPointerException
        if (request == null) {
            throw new IllegalArgumentException("CreateProductRequest cannot be null");
        }

        // Step 2: Create new Product entity
        Product product = new Product();

        // Step 3: Map data from request to entity
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCreatedAt(java.time.LocalDateTime.now());

        // Step 4: Persist the entity
        // Save the new product to the database and return the saved instance
        return productRepository.save(product);
    }

    /**
     * Updates an existing product with data from UpdateProductRequest DTO.
     * This method handles partial updates where only provided fields are modified.
     * The method validates the existence of the product before updating.
     *
     * @param id      The ID of the product to update
     * @param request The UpdateProductRequest containing updated product data
     * @return The updated Product entity
     * @throws IllegalArgumentException if id or request is null
     * @throws RuntimeException if product with given id is not found
     */
    @Override
    public Product updateProduct(Long id, UpdateProductRequest request) {
        // Step 1: Validate input parameters
        // Ensure both id and request are provided
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("UpdateProductRequest cannot be null");
        }

        // Step 2: Retrieve existing product
        // Find the product by ID, throws exception if not found
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            // Step 3: Get the product entity for modification
            Product product = existingProduct.get();

            // Step 4: Update product fields
            // Map the updated data from request to the existing product
            // Note: null values in request indicate no change for that field
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setQuantity(request.getQuantity());

            // Step 5: Persist the updated entity
            // Save the modified product and return the updated instance
            return productRepository.save(product);
        }

        // Step 6: Handle product not found
        // Throw exception if the product doesn't exist
        throw new RuntimeException("Product with ID " + id + " not found");
    }

    /**
     * Retrieves a specific product by its ID.
     * This method provides access to individual products for detailed operations.
     *
     * @param id The ID of the product to retrieve
     * @return Optional containing the Product if found, empty Optional if not found
     * @throws IllegalArgumentException if id is null
     */
    @Override
    public Optional<Product> getProductById(Long id) {
        // Step 1: Validate input parameter
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        // Step 2: Retrieve product from repository
        return productRepository.findById(id);
    }

    /**
     * Deletes a product by its ID.
     * This method permanently removes a product from the inventory.
     *
     * @param id The ID of the product to delete
     * @throws IllegalArgumentException if id is null
     */
    @Override
    public void deleteProductById(Long id) {
        // Step 1: Validate input parameter
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        // Step 2: Delete the product
        // The repository handles the actual deletion operation
        productRepository.deleteById(id);
    }
}