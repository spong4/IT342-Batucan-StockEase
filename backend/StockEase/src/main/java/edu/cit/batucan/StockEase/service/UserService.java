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

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtProvider jwtProvider;
    
    /**
     * Register a new user
     * @param request Registration request containing email, password, name, and role
     * @return AuthResponse with user data and JWT tokens
     * @throws IllegalArgumentException if email already exists or validation fails
     */
    public AuthResponse register(RegisterRequest request) {
        // Validate required fields
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
        
        // Prevent duplicate email registration
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        // Validate role
        UserRole role;
        try {
            role = UserRole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role. Must be OWNER or STAFF");
        }
        
        // Create new user with hashed password
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .role(role)
                .isVerified(true) // Auto-verify for now
                .build();
        
        // Store user in database
        User savedUser = userRepository.save(user);
        
        // Generate JWT tokens
        String accessToken = jwtProvider.generateToken(savedUser.getEmail(), savedUser.getRole().toString());
        String refreshToken = jwtProvider.generateToken(savedUser.getEmail(), savedUser.getRole().toString());
        
        // Build response
        return AuthResponse.builder()
                .user(convertToUserDto(savedUser))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    
    /**
     * Login an existing user
     * @param request Login request containing email and password
     * @return AuthResponse with user data and JWT tokens
     * @throws IllegalArgumentException if credentials are invalid
     */
    public AuthResponse login(LoginRequest request) {
        // Validate credentials using the database
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        User user = userOptional.get();
        
        // Prevent login with invalid credentials
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        // Generate JWT tokens
        String accessToken = jwtProvider.generateToken(user.getEmail(), user.getRole().toString());
        String refreshToken = jwtProvider.generateToken(user.getEmail(), user.getRole().toString());
        
        // Allow successful login to access the system
        return AuthResponse.builder()
                .user(convertToUserDto(user))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    
    /**
     * Get user by email
     * @param email User email
     * @return User if found
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Convert User entity to UserDto (excludes password)
     */
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
