package edu.cit.batucan.StockEase.security;

/**
 * Strategy Pattern - PasswordEncoderStrategy interface
 * Defines the contract for password encoding strategies.
 * Allows switching between BCrypt, Argon2, SCrypt without
 * changing any other part of the system.
 */
public interface PasswordEncoderStrategy {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
