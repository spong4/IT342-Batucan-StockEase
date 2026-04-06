package edu.cit.batucan.StockEase.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Strategy Pattern - BCrypt concrete strategy
 * Implements PasswordEncoderStrategy using BCrypt with 12 salt rounds
 * as required by SDD security specification.
 */
@Component
public class BcryptPasswordStrategy implements PasswordEncoderStrategy {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Override
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
