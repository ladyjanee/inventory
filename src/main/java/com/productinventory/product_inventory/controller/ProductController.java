package com.productinventory.product_inventory.controller;

import com.productinventory.product_inventory.dto.CreateProductRequest;
import com.productinventory.product_inventory.dto.UpdateProductRequest;
import com.productinventory.product_inventory.entity.Product;
import com.productinventory.product_inventory.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(Model model, @RequestParam(value = "search", required = false) String search) {
        List<Product> products;
        if (search != null && !search.isBlank()) {
            products = productService.getAllProducts().stream()
                    .filter(p -> p.getName().toLowerCase().contains(search.toLowerCase()))
                    .toList();
            model.addAttribute("search", search);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("createProductRequest", new CreateProductRequest());
        return "add-product";
    }

    @PostMapping("/save")
    public String saveProduct(
            @Valid @ModelAttribute("createProductRequest") CreateProductRequest request,
            BindingResult result,
            Model model) {
        
        if (result.hasErrors()) {
            return "add-product";
        }
        
        try {
            productService.createProduct(request);
            return "redirect:/products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to create product: " + e.getMessage());
            return "add-product";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        productService.getProductById(id).ifPresentOrElse(
                p -> {
                    UpdateProductRequest updateRequest = new UpdateProductRequest();
                    updateRequest.setName(p.getName());
                    updateRequest.setDescription(p.getDescription());
                    updateRequest.setPrice(p.getPrice());
                    updateRequest.setQuantity(p.getQuantity());
                    model.addAttribute("id", id);
                    model.addAttribute("updateProductRequest", updateRequest);
                },
                () -> model.addAttribute("updateProductRequest", new UpdateProductRequest())
        );
        return "edit-product";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("updateProductRequest") UpdateProductRequest request,
            BindingResult result,
            Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("id", id);
            return "edit-product";
        }
        
        try {
            productService.updateProduct(id, request);
            return "redirect:/products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to update product: " + e.getMessage());
            model.addAttribute("id", id);
            return "edit-product";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProductById(id);
        } catch (Exception e) {
            // Log error and redirect with error message if needed
        }
        return "redirect:/products";
    }
}