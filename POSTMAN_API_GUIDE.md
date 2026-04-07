# Product Inventory System - Postman API Testing Guide

## Overview
This guide provides detailed instructions for testing the Product Inventory System API using Postman. The system includes JWT authentication for securing API endpoints.

---

## Prerequisites

### 1. **Database Setup**
The application is configured to use **MySQL**. Ensure MySQL is running with the following configuration:

```
Host: localhost
Port: 3306
Database: product_inventory_db
Username: root
Password: password
```

**For local testing without MySQL**, switch to **H2 Database** (in-memory) by updating `application.properties`:

```properties
# Comment out MySQL configuration
# spring.datasource.url=jdbc:mysql://localhost:3306/product_inventory_db...

# Add H2 configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

### 2. **Start the Application**
```bash
cd c:\Users\Lady Jane\Desktop\lala_product_inventory
.\mvnw.cmd spring-boot:run
```

The application will start on **http://localhost:8081**

### 3. **Import Collections into Postman**
- Open Postman
- Create a new Collection: **Product Inventory API**
- Set up the following environment variable:
  - **Key**: `token`
  - **Value**: (Will be set dynamically after login)
  - **Scope**: Environment

---

## API Endpoints

### 1. Authentication Endpoint

#### **POST /auth/login**
Authenticates a user and returns a JWT token for subsequent requests.

**Default Credentials** (initialized on startup):
- **Username**: `admin`
- **Password**: `password`

OR

- **Username**: `user`
- **Password**: `password`

**Request:**
```
Method: POST
URL: http://localhost:8081/auth/login
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "admin",
  "password": "password"
}
```

**Response (Success - 200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3NTUwMjA1MSwiZXhwIjoxNzc1NTg4NDUxfQ.UWRbg4pom0V-anZz5w6MrefE3JZcfjPVtRBi4FCjo1M"
}
```

**Response (Failure - 400 Bad Request):**
```json
"Invalid username or password"
```

**Postman Setup:**
1. Set **Request** type to `POST`
2. Enter URL: `http://localhost:8081/auth/login`
3. Go to **Headers** tab, add:
   - `Content-Type: application/json`
4. Go to **Body** tab, select `raw` → `JSON`, paste the request body
5. Click **Send**
6. Copy the token from the response
7. Go to **Tests** tab, add this script to auto-save the token:
   ```javascript
   if (pm.response.code === 200) {
     var jsonData = pm.response.json();
     pm.environment.set("token", jsonData.token);
   }
   ```

---

### 2. Protected API Endpoint

#### **GET /api/test**
Returns an authenticated user's information. **Requires JWT Token**.

**Request:**
```
Method: GET
URL: http://localhost:8081/api/test
Authorization: Bearer <JWT_TOKEN>
```

**Headers:**
- `Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3NTUwMjA1MSwiZXhwIjoxNzc1NTg4NDUxfQ.UWRbg4pom0V-anZz5w6MrefE3JZcfjPVtRBi4FCjo1M`

**Response (Success - 200 OK):**
```
Hello admin, you are authenticated!
```

**Response (Failure - 401 Unauthorized):**
```json
{
  "error": "Unauthorized",
  "message": "Invalid or missing JWT token"
}
```

**Postman Setup:**
1. Set **Request** type to `GET`
2. Enter URL: `http://localhost:8081/api/test`
3. Go to **Authorization** tab
4. Select **Type**: `Bearer Token`
5. Enter **Token**: (Use the variable) `{{token}}`
6. Click **Send**

---

### 3. Product Management Endpoints

#### **API: GET /api/products**
Retrieves all products and supports optional search filters.

**Request:**
```
Method: GET
URL: http://localhost:8081/api/products?name=Jtine&description=araykooo&price=100&quantity=1
Authorization: Bearer {{token}}
```

**Response (Success - 200 OK):**
Returns JSON list of products matching the filters.

> Note: This API endpoint requires a valid JWT token.

#### **API: POST /api/products**
Creates a new product using JSON.

**Request:**
```
Method: POST
URL: http://localhost:8081/api/products
Content-Type: application/json
Authorization: Bearer {{token}}
```

**Body:**
```json
{
  "name": "New Product",
  "description": "Product Description",
  "price": 99.99,
  "quantity": 10
}
```

#### **GET /products**
Retrieves all products. (No authentication required for web interface)

**Request:**
```
Method: GET
URL: http://localhost:8081/products
```

**Response (Success - 200 OK):**
Shows the HTML product list page

#### **POST /products**
Creates a new product via form submission.

**Request:**
```
Method: POST
URL: http://localhost:8081/products
Content-Type: application/x-www-form-urlencoded
```

**Body:**
```
name=New Product
description=Product Description
price=99.99
quantity=10
```

#### **GET /products/{id}**
Retrieves details for a specific product.

**Request:**
```
Method: GET
URL: http://localhost:8081/products/1
```

#### **PUT /products/{id}**
Updates an existing product.

**Request:**
```
Method: PUT
URL: http://localhost:8081/products/1
Content-Type: application/x-www-form-urlencoded
```

**Body:**
```
name=Updated Product
description=Updated Description
price=149.99
quantity=15
```

#### **DELETE /products/{id}**
Deletes a product.

**Request:**
```
Method: DELETE
URL: http://localhost:8081/products/1
```

---

## JWT Token Details

### Token Structure
The JWT token consists of three parts:

1. **Header**: Algorithm and token type
   ```json
   {
     "alg": "HS256"
   }
   ```

2. **Payload**: User information and timestamps
   ```json
   {
     "sub": "admin",
     "iat": 1775502051,
     "exp": 1775588451
   }
   ```

3. **Signature**: HMAC-SHA256 signature

### Token Properties
- **Algorithm**: HS256 (HMAC-SHA256)
- **Duration**: 24 hours (86400000 milliseconds)
- **Secret Key**: Configured in `application.properties`

### Token Expiration
Once a token expires (24 hours), you must:
1. Call `/auth/login` again to obtain a new token
2. Update the token in Postman environment variables
3. Subsequent requests will use the new token

---

## Complete Postman Collection

### Collection Name: **Product Inventory API**

#### Request 1: User Login
```
POST http://localhost:8081/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

**Post-request Script** (to auto-set token):
```javascript
if (pm.response.code === 200) {
  var jsonData = pm.response.json();
  pm.environment.set("token", jsonData.token);
}
```

---

#### Request 2: Access Protected Endpoint
```
GET http://localhost:8081/api/test
Authorization: Bearer {{token}}
```

---

#### Request 3: Get All Products
```
GET http://localhost:8081/products
```

---

#### Request 4: Create Product
```
POST http://localhost:8081/products
Content-Type: application/x-www-form-urlencoded

name=Sample Product&description=A test product&price=49.99&quantity=20
```

---

#### Request 5: Get Product by ID
```
GET http://localhost:8081/products/1
```

---

#### Request 6: Update Product
```
PUT http://localhost:8081/products/1
Content-Type: application/x-www-form-urlencoded

name=Updated Product&description=Updated description&price=59.99&quantity=25
```

---

#### Request 7: Delete Product
```
DELETE http://localhost:8081/products/1
```

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| **401 Unauthorized** | Token has expired or is missing. Call `/auth/login` again to get a new token. |
| **Invalid username or password** | Verify credentials. Default: `admin`/`password` or `user`/`password` |
| **Database connection error** | Ensure MySQL is running on localhost:3306, or switch to H2 database. |
| **Port 8080 already in use** | Kill the existing Java process or change the port in `application.properties` with `server.port=8081` |
| **CORS errors** | CORS is disabled for simplicity in this demo. Add `@CrossOrigin` annotations if needed. |

---

## Application Configuration

### Database Configuration (`application.properties`)

#### MySQL (Default)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/product_inventory_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

#### H2 (In-Memory Alternative)
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

### JWT Configuration
```properties
jwt.secret.key=mySecretKeyForJwtTokenGenerationThatIsLongEnoughToBeSecure123456789
```

### Server Configuration
```properties
server.port=8080
spring.application.name=product_inventory
```

---

## Security Features

✅ **JWT Authentication**: Secure token-based authentication  
✅ **BCrypt Password Encoding**: Passwords are encrypted using BCrypt  
✅ **Token Expiration**: Tokens expire after 24 hours  
✅ **HMAC-SHA256 Signature**: Ensures token integrity  
✅ **Protected Endpoints**: `/api/test` requires valid JWT token  

---

## Next Steps

1. ✅ Start the application
2. ✅ Login using `/auth/login` to get a JWT token
3. ✅ Use the token to access `/api/test`
4. ✅ Test product CRUD operations
5. ✅ Verify all endpoints work correctly

For advanced testing, consider:
- Setting up automated test suites in Postman
- Testing with invalid tokens
- Testing expired token scenarios
- Load testing with multiple concurrent requests

---

**Last Updated**: April 7, 2026  
**Application Version**: 1.0  
**Spring Boot Version**: 3.3.12
