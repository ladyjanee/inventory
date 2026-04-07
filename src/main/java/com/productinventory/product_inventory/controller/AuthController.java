package com.productinventory.product_inventory.controller;

import com.productinventory.product_inventory.dto.AuthRequest;
import com.productinventory.product_inventory.dto.AuthResponse;
import com.productinventory.product_inventory.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for handling authentication operations.
 * This controller manages user login and JWT token generation for the application.
 *
 * Endpoints:
 * - POST /auth/login: Authenticates user credentials and returns JWT token
 *
 * Security Features:
 * - Uses Spring Security AuthenticationManager for credential validation
 * - Generates JWT tokens for authenticated sessions
 * - Returns standardized error responses for failed authentication
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Handles user login requests and generates JWT tokens.
     * This endpoint authenticates user credentials using Spring Security
     * and returns a JWT token for subsequent authenticated requests.
     *
     * @param authRequest The authentication request containing username and password
     * @return ResponseEntity with AuthResponse containing JWT token on success,
     *         or error message on authentication failure
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        return authenticate(authRequest.getUsername(), authRequest.getPassword());
    }

    @GetMapping("/login")
    public ResponseEntity<?> loginGet(@RequestParam String username, @RequestParam String password) {
        return authenticate(username, password);
    }

    private ResponseEntity<?> authenticate(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            // Step 2: Extract user details from successful authentication
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Step 3: Generate JWT token
            String token = jwtUtil.generateToken(userDetails.getUsername());

            // Step 4: Return successful authentication response
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (Exception e) {
            // Step 5: Handle authentication failure
            // Return 400 Bad Request with error message for invalid credentials
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }
}