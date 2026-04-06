# Structured Request Validation Pattern - Implementation Guide

## Overview

This document outlines the comprehensive validation pattern implemented in the Product Inventory System. The architecture ensures all incoming user inputs are validated before reaching the business logic layer, improving data integrity, security, and maintainability.

---

## Architecture Components

### 1. **Data Transfer Objects (DTOs)**

Located in `src/main/java/com/productinventory/product_inventory/dto/`

#### BaseRequest (Abstract Class)
- **Purpose**: Common base class containing shared validation logic for all request types
- **Validation Annotations**: 
  - `@NotBlank` on name field (required)
  - `@Size` for field length constraints
  - `@NotNull` for price and quantity (required)
  - `@Positive` for price (must be > 0)
- **Fields**:
  - `name`: Product name (required, 1-255 chars)
  - `description`: Product description (optional, max 1000 chars)
  - `price`: Product price (required, > 0)
  - `quantity`: Product quantity (required, >= 0)

#### CreateProductRequest
- **Extends**: BaseRequest
- **Purpose**: Validates data for creating new products (POST operations)
- **Constructor**: Overloaded for flexible instantiation
- **Usage**: Form submission on `/products/new`

#### UpdateProductRequest
- **Extends**: BaseRequest
- **Purpose**: Validates data for updating existing products (PUT/POST operations)
- **Constructor**: Overloaded for flexible instantiation
- **Usage**: Form submission on `/products/edit/{id}`

---

### 2. **Response Wrapper Classes**

Located in `src/main/java/com/productinventory/product_inventory/response/`

#### StandardResponse<T>
- **Generic Type Parameter**: Allows response to carry any data type
- **Fields**:
  - `success` (boolean): Indicates operation success/failure
  - `data` (T): The actual response payload (can be null for errors)
  - `errors` (Map<String, String>): Field-level error messages
- **Factory Methods**:
  - `success(T data)`: Creates successful response
  - `error(T data, Map<String, String> errors)`: Creates error response
  - `validationError(Map<String, String> errors)`: Creates validation error response
- **Response Format**:
```json
{
  "success": true,
  "data": { "id": 1, "name": "Product A", ... },
  "errors": null
}
```

---

### 3. **Global Exception Handler**

Located in `src/main/java/com/productinventory/product_inventory/exception/`

#### GlobalExceptionHandler
- **Annotation**: `@ControllerAdvice` - handles exceptions globally
- **Exception Handlers**:

##### MethodArgumentNotValidException
- **Trigger**: When `@Valid` validation fails on model binding
- **Behavior**: Extracts field errors and returns them in StandardResponse format
- **HTTP Status**: 400 (Bad Request)
- **Response Format**:
```json
{
  "success": false,
  "data": null,
  "errors": {
    "name": "Product name is required",
    "price": "Price must be greater than zero"
  }
}
```

##### MethodArgumentTypeMismatchException
- **Trigger**: Type mismatch in request parameters (e.g., String instead of Long)
- **HTTP Status**: 400 (Bad Request)

##### Exception (Generic)
- **Trigger**: Uncaught runtime exceptions
- **HTTP Status**: 500 (Internal Server Error)

---

### 4. **Service Layer Refactoring**

Located in `src/main/java/com/productinventory/product_inventory/service/`

#### ProductService Interface
**New Methods**:
```java
Product createProduct(CreateProductRequest request);
Product updateProduct(Long id, UpdateProductRequest request);
```
**Existing Methods** (retain backward compatibility):
```java
Product saveProduct(Product product);
List<Product> getAllProducts();
Optional<Product> getProductById(Long id);
void deleteProductById(Long id);
```

#### ProductServiceImpl
**Key Responsibilities**:
1. **DTO to Entity Conversion**: Converts validated DTOs to Product entities
2. **Business Logic**: Applies business rules before saving
3. **Database Operations**: Delegates persistence to repository
4. **Error Handling**: Throws meaningful exceptions (e.g., "Product not found")

**Conversion Logic** (Example: createProduct):
```java
public Product createProduct(CreateProductRequest request) {
    Product product = new Product();
    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setQuantity(request.getQuantity());
    return productRepository.save(product);
}
```

---

### 5. **Controller Layer Refactoring**

Located in `src/main/java/com/productinventory/product_inventory/controller/`

#### ProductController Changes

**Old Approach** (Direct Entity Usage):
```java
@PostMapping("/save")
public String saveProduct(@Valid @ModelAttribute("product") Product product, BindingResult result) {
    // Validation is coupled with entity
    // Service receives unvalidated entity
}
```

**New Approach** (DTO-Based):
```java
@PostMapping("/save")
public String saveProduct(
    @Valid @ModelAttribute("createProductRequest") CreateProductRequest request,
    BindingResult result,
    Model model) {
    
    if (result.hasErrors()) {
        return "add-product"; // Re-render with validation errors
    }
    
    try {
        productService.createProduct(request); // Receive only validated data
        return "redirect:/products";
    } catch (Exception e) {
        model.addAttribute("errorMessage", "Failed to create product: " + e.getMessage());
        return "add-product";
    }
}
```

**Key Improvements**:
- Validation happens before controller logic
- BindingResult captures and displays validation errors
- Service layer receives only validated data
- Exception handling provides meaningful error messages
- Model attributes prepared for Thymeleaf templates

---

### 6. **Template (Thymeleaf) Integration**

Located in `src/main/resources/templates/`

#### add-product.html & edit-product.html

**Validation Error Display**:

1. **Global Error Alert**:
```html
<div th:if="${errorMessage}" class="alert alert-danger">
    <span th:text="${errorMessage}"></span>
</div>
```

2. **Validation Errors Summary**:
```html
<div th:if="${#fields.hasAnyErrors()}" class="alert alert-danger">
    <ul>
        <li th:each="err : ${#fields.allErrors()}" th:text="${err}"></li>
    </ul>
</div>
```

3. **Field-Level Error Styling**:
```html
<input type="text" 
       th:field="*{name}"
       th:classappend="${#fields.hasErrors('name')} ? 'is-invalid' : ''" />
<div class="invalid-feedback" th:if="${#fields.hasErrors('name')}">
    <span th:errors="*{name}"></span>
</div>
```

**Form Binding to DTOs**:
```html
<!-- For Create -->
<form th:object="${createProductRequest}" method="post">
    <input th:field="*{name}" />
    <input th:field="*{price}" />
</form>

<!-- For Update -->
<form th:object="${updateProductRequest}" method="post">
    <input th:field="*{name}" />
    <input th:field="*{price}" />
</form>
```

**HTML5 Validation Attributes** (Client-side validation):
```html
<input type="number" min="0.01" step="0.01" />
<input type="text" required />
<textarea maxlength="1000"></textarea>
```

---

## Validation Flow Diagram

```
User Input (Form)
        ↓
HTML5 Validation (Client-side)
        ↓
POST Request to Controller
        ↓
@Valid Annotation Triggers BindingResult
        ↓
Validation Annotations Applied (@NotNull, @Size, etc.)
        ↓
Validation Fails? ──→ GlobalExceptionHandler
                    (Returns StandardResponse with errors)
        ↓
Validation Passes
        ↓
BindingResult Check in Controller
        ↓
Errors? ──→ Re-render Form with Error Messages
        ↓
No Errors
        ↓
Service Layer (createProduct/updateProduct)
        ↓
DTO → Entity Conversion
        ↓
Business Logic Application
        ↓
Database CRUD Operation
        ↓
Success ──→ Redirect to List View
```

---

## Key Benefits

### 1. **Data Integrity**
- All data is validated before entering the database
- Type safety enforced through annotations
- Business rules enforced at service layer

### 2. **Security**
- Input validation prevents injection attacks
- Comprehensive error handling prevents information leakage
- Role-based access control ready (can be added to security layer)

### 3. **Maintainability**
- DTOs separate API contracts from internal models
- Centralized validation logic in GlobalExceptionHandler
- Changes to validation rules in one place (annotations)

### 4. **User Experience**
- Clear, user-friendly validation error messages
- Field-level error highlighting
- Consistent error response format across API
- Client-side HTML5 validation provides immediate feedback

### 5. **Separation of Concerns**
- **DTOs**: Validate and transfer data boundaries
- **Service**: Handle business logic and state transformations
- **Repository**: Handle data persistence only
- **Controller**: Orchestrate request-response flow
- **Exception Handler**: Centralize error handling

---

## Custom Validation Messages

Each field in BaseRequest includes custom error messages:

```java
@NotBlank(message = "Product name is required")
@Size(min = 1, max = 255, message = "Product name must be between 1 and 255 characters")
protected String name;

@NotNull(message = "Price is required")
@Positive(message = "Price must be greater than zero")
protected Double price;
```

**Display in Templates**:
```html
<div class="invalid-feedback" th:if="${#fields.hasErrors('price')}">
    <span th:errors="*{price}"></span>  <!-- Shows custom message -->
</div>
```

---

## Testing Validation

### 1. **Valid Request** (Success Case)
- Form: Name="Product A", Price=29.99, Quantity=10
- Result: Product created, redirect to list

### 2. **Missing Required Fields** (Failure Case)
- Form: Name="", Price=null, Quantity=null
- Result: Validation errors displayed, form re-rendered

### 3. **Invalid Data Types** (Failure Case)
- Form: Price="abc", Quantity="xyz"
- Result: Type mismatch handled, validation error returned

### 4. **Out of Range Values** (Failure Case)
- Form: Price=-5.00 (negative), Name="x" * 300 (too long)
- Result: Validation error message shown

---

## Future Enhancements

1. **Custom Validators**: Create `@ValidProduct` annotation for complex validations
2. **API Layer**: Expose DTO-based REST endpoints returning StandardResponse
3. **Audit Logging**: Track who modified what data and when
4. **Batch Validation**: Handle bulk operations with partial failure scenarios
5. **Business Rule Engine**: Move complex validations to dedicated service
6. **Database Constraints**: Add unique constraints, foreign key validations

---

## File Structure

```
src/main/java/com/productinventory/product_inventory/
├── dto/
│   ├── BaseRequest.java         (Abstract base with common validation)
│   ├── CreateProductRequest.java (Extends BaseRequest)
│   └── UpdateProductRequest.java (Extends BaseRequest)
├── response/
│   └── StandardResponse.java    (Generic response wrapper)
├── exception/
│   └── GlobalExceptionHandler.java (Centralized exception handling)
├── controller/
│   └── ProductController.java   (Refactored to use DTOs)
├── service/
│   ├── ProductService.java      (Updated interface)
│   └── ProductServiceImpl.java   (Updated implementation)
└── model/
    └── Product.java             (Unchanged)

src/main/resources/templates/
├── add-product.html             (Updated to use CreateProductRequest)
└── edit-product.html            (Updated to use UpdateProductRequest)
```

---

## Running the Application

```bash
# Start the application
.\mvnw.cmd spring-boot:run

# Access the web interface
# Dashboard: http://localhost:8080/products
# Add Product: http://localhost:8080/products/new
# Edit Product: http://localhost:8080/products/edit/{id}

# Database Console
# H2 Console: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:product_inventory_db
# User: sa, Password: (empty)
```

---

## Summary

This validation pattern provides a robust, maintainable framework for ensuring data quality across the Product Inventory System. By enforcing validation at multiple layers (client-side HTML5, server-side annotations, centralized exception handling), the application achieves high data integrity while maintaining clean separation of concerns.
