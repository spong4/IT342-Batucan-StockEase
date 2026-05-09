package edu.cit.batucan.StockEase.feature.auth;

import edu.cit.batucan.StockEase.feature.auth.dto.AuthResponse;
import edu.cit.batucan.StockEase.feature.auth.dto.LoginRequest;
import edu.cit.batucan.StockEase.feature.auth.dto.RegisterRequest;
import edu.cit.batucan.StockEase.feature.auth.dto.UserDto;
import edu.cit.batucan.StockEase.feature.auth.model.User;
import edu.cit.batucan.StockEase.feature.auth.model.UserRole;
import edu.cit.batucan.StockEase.feature.auth.repository.UserRepository;
import edu.cit.batucan.StockEase.shared.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtProvider jwtProvider;
    
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
        
        String accessToken = jwtProvider.generateAccessToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole().toString());
        String refreshToken = jwtProvider.generateRefreshToken(savedUser.getId());
        
        return AuthResponse.builder()
                .user(convertToUserDto(savedUser))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    
    public AuthResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        User user = userOptional.get();
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().toString());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());
        
        return AuthResponse.builder()
                .user(convertToUserDto(user))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    
    public UserDto getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return convertToUserDto(user);
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
