package com.productinventory.product_inventory.service;

import com.productinventory.product_inventory.dto.CreateProductRequest;
import com.productinventory.product_inventory.dto.UpdateProductRequest;
import com.productinventory.product_inventory.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    
    Product saveProduct(Product product);
    
    Product createProduct(CreateProductRequest request);
    
    Product updateProduct(Long id, UpdateProductRequest request);
    
    Optional<Product> getProductById(Long id);
    
    void deleteProductById(Long id);
}