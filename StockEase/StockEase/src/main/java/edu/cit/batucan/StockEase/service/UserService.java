package edu.cit.batucan.StockEase.service;

import edu.cit.batucan.StockEase.dto.AuthResponse;
import edu.cit.batucan.StockEase.dto.LoginRequest;
import edu.cit.batucan.StockEase.dto.RegisterRequest;
import edu.cit.batucan.StockEase.dto.UserDto;
import edu.cit.batucan.StockEase.entity.User;
import edu.cit.batucan.StockEase.entity.UserRole;
import edu.cit.batucan.StockEase.repository.UserRepository;
import edu.cit.batucan.StockEase.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * Facade Pattern - UserService implements AuthFacade
 * Acts as a single entry point for all authentication operations.
 * Controller only needs to know about this facade, not the subsystems.
 */
@Service
public class UserService implements AuthFacade {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        if (request.getFirstname() == null || request.getFirstname().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (request.getLastname() == null || request.getLastname().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        UserRole role;
        try {
            role = UserRole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role. Must be OWNER or STAFF");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .role(role)
                .isVerified(true)
                .build();

        User savedUser = userRepository.save(user);

        String accessToken = jwtProvider.generateToken(
            savedUser.getEmail(), savedUser.getRole().toString());
        String refreshToken = jwtProvider.generateToken(
            savedUser.getEmail(), savedUser.getRole().toString());

        return AuthResponse.builder()
                .user(convertToUserDto(savedUser))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String accessToken = jwtProvider.generateToken(
            user.getEmail(), user.getRole().toString());
        String refreshToken = jwtProvider.generateToken(
            user.getEmail(), user.getRole().toString());

        return AuthResponse.builder()
                .user(convertToUserDto(user))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private UserDto convertToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .role(user.getRole().toString())
                .build();
    }
}
