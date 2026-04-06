package com.productinventory.product_inventory.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility class for JWT (JSON Web Token) operations.
 * This component handles token generation, validation, and parsing for authentication.
 *
 * Security Features:
 * - HS256 signature algorithm for token integrity
 * - 24-hour token expiration
 * - Secure key generation using JJWT Keys utility
 * - Comprehensive token validation with exception handling
 *
 * Token Structure:
 * - Subject: Username of the authenticated user
 * - Issued At: Token creation timestamp
 * - Expiration: Token expiry timestamp (24 hours from issuance)
 * - Signature: HMAC-SHA256 signature for integrity verification
 */
@Component
public class JwtUtil {

    // Step 1: Define security constants
    // JWT secret key loaded from application.properties
    @Value("${jwt.secret.key}")
    private String secretKey;

    // Token expiration time: 24 hours in milliseconds
    private static final long EXPIRATION_TIME = 86400000; // 24 hours in milliseconds

    /**
     * Generates a secure signing key for JWT operations.
     * Uses JJWT Keys utility to create a proper HMAC-SHA256 key from the secret string.
     *
     * @return Key object suitable for HMAC-SHA256 signing
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Generates a new JWT token for the authenticated user.
     * Creates a signed token containing user identity and expiration information.
     *
     * Token Generation Steps:
     * 1. Set username as token subject
     * 2. Set issued-at timestamp to current time
     * 3. Set expiration timestamp (24 hours from now)
     * 4. Sign with HMAC-SHA256 algorithm
     * 5. Compact into URL-safe string format
     *
     * @param username The username to include in the token
     * @return JWT token string
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)  // Step 1: Set the username as the token subject
                .setIssuedAt(new Date())  // Step 2: Set token issuance timestamp
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // Step 3: Set expiration (24 hours)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // Step 4: Sign with HMAC-SHA256
                .compact();  // Step 5: Generate compact JWT string
    }

    /**
     * Extracts the username from a JWT token.
     * Parses and validates the token signature, then retrieves the subject claim.
     *
     * Token Parsing Steps:
     * 1. Create JWT parser with signing key
     * 2. Parse and validate token signature
     * 3. Extract claims from token body
     * 4. Return the subject (username) claim
     *
     * @param token The JWT token to parse
     * @return Username extracted from token
     * @throws JwtException if token is invalid or signature verification fails
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())  // Step 1: Configure parser with signing key
                .build()
                .parseClaimsJws(token)  // Step 2: Parse and validate token
                .getBody()  // Step 3: Get token claims
                .getSubject();  // Step 4: Extract username from subject claim
    }

    /**
     * Validates a JWT token against a username and expiration status.
     * Performs comprehensive validation including signature verification,
     * username matching, and expiration checking.
     *
     * Validation Steps:
     * 1. Extract username from token
     * 2. Verify username matches expected user
     * 3. Check if token is not expired
     * 4. Handle any JWT parsing exceptions
     *
     * @param token The JWT token to validate
     * @param username The expected username to match against token
     * @return true if token is valid and matches user, false otherwise
     */
    public boolean validateToken(String token, String username) {
        try {
            // Step 1: Extract username from token for comparison
            String extractedUsername = extractUsername(token);

            // Step 2: Validate username matches and token is not expired
            return (username.equals(extractedUsername) && !isTokenExpired(token));

        } catch (JwtException | IllegalArgumentException e) {
            // Step 3: Handle validation failures
            // Return false for any parsing or validation errors
            return false;
        }
    }

    /**
     * Checks if a JWT token has expired.
     * Compares the token's expiration timestamp against the current time.
     *
     * Expiration Check Steps:
     * 1. Parse token to extract expiration claim
     * 2. Compare expiration date with current date
     * 3. Return true if token is expired
     * 4. Handle parsing exceptions (treat as expired)
     *
     * @param token The JWT token to check
     * @return true if token is expired or invalid, false if still valid
     */
    private boolean isTokenExpired(String token) {
        try {
            // Step 1: Parse token and extract expiration timestamp
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            // Step 2: Check if expiration date is before current date
            return expiration.before(new Date());

        } catch (Exception e) {
            // Step 3: Handle parsing failures
            // If token cannot be parsed, consider it expired/invalid
            return true;
        }
    }
}