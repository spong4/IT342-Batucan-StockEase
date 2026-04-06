package edu.cit.batucan.StockEase.service;

import edu.cit.batucan.StockEase.dto.AuthResponse;
import edu.cit.batucan.StockEase.dto.LoginRequest;
import edu.cit.batucan.StockEase.dto.RegisterRequest;

/**
 * Facade Pattern - AuthFacade interface
 * Provides a simplified interface to the complex authentication subsystem.
 * Hides the complexity of: UserRepository, PasswordEncoder, JwtProvider
 * from the AuthController.
 */
public interface AuthFacade {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
