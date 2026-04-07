package com.productinventory.product_inventory.controller;

import com.productinventory.product_inventory.dto.CreateProductRequest;
import com.productinventory.product_inventory.dto.UpdateProductRequest;
import com.productinventory.product_inventory.entity.Product;
import com.productinventory.product_inventory.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API Controller for Product management operations.
 * This controller provides JSON API endpoints for CRUD operations on products.
 *
 * Endpoints:
 * - GET /api/products: Get all products
 * - GET /api/products/{id}: Get product by ID
 * - POST /api/products: Create new product
 * - PUT /api/products/{id}: Update existing product
 * - DELETE /api/products/{id}: Delete product
 *
 * Security:
 * - All endpoints require authentication (except possibly GET operations)
 */
@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProductService productService;

    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves all products.
     *
     * @return List of all products
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "quantity", required = false) Integer quantity) {

        List<Product> products = productService.getAllProducts();

        if (name != null && !name.isBlank()) {
            products = products.stream()
                    .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (description != null && !description.isBlank()) {
            products = products.stream()
                    .filter(p -> p.getDescription() != null && p.getDescription().toLowerCase().contains(description.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (price != null) {
            products = products.stream()
                    .filter(p -> p.getPrice() != null && p.getPrice().equals(price))
                    .collect(Collectors.toList());
        }
        if (quantity != null) {
            products = products.stream()
                    .filter(p -> p.getQuantity() != null && p.getQuantity().equals(quantity))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(products);
    }

    /**
     * Retrieves a specific product by ID.
     *
     * @param id The product ID
     * @return Product details or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new product.
     *
     * @param request The product creation request
     * @return Created product with 201 status
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductRequest request) {
        try {
            Product createdProduct = productService.createProduct(request);
            return ResponseEntity.status(201).body(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create product: " + e.getMessage());
        }
    }

    /**
     * Updates an existing product.
     *
     * @param id The product ID to update
     * @param request The product update request
     * @return Updated product or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        try {
            Product updatedProduct = productService.updateProduct(id, request);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update product: " + e.getMessage());
        }
    }

    /**
     * Deletes a product by ID.
     *
     * @param id The product ID to delete
     * @return 204 No Content on success, 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProductById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete product: " + e.getMessage());
        }
    }
}