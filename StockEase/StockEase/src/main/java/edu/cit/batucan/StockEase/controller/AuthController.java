package edu.cit.batucan.StockEase.controller;

import edu.cit.batucan.StockEase.dto.ApiResponse;
import edu.cit.batucan.StockEase.dto.AuthResponse;
import edu.cit.batucan.StockEase.dto.LoginRequest;
import edu.cit.batucan.StockEase.dto.RegisterRequest;
import edu.cit.batucan.StockEase.service.AuthFacade;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController uses AuthFacade (Facade Pattern)
 * Controller has no knowledge of JWT, BCrypt, or database operations.
 * All complexity is hidden behind the AuthFacade interface.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    // Depends on interface (Facade), not implementation
    @Autowired
    private AuthFacade authFacade;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse authResponse = authFacade.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(authResponse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("VALID-001", "Validation failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("SYSTEM-001", "Internal server error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse authResponse = authFacade.login(request);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(authResponse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("AUTH-001", "Invalid credentials", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("SYSTEM-001", "Internal server error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser() {
        return ResponseEntity.ok("Authentication endpoint - requires Bearer token");
    }
}
