package edu.cit.batucan.StockEase.feature.auth;

import edu.cit.batucan.StockEase.feature.auth.dto.LoginRequest;
import edu.cit.batucan.StockEase.feature.auth.dto.RegisterRequest;
import edu.cit.batucan.StockEase.feature.auth.dto.UserDto;
import edu.cit.batucan.StockEase.feature.auth.model.User;
import edu.cit.batucan.StockEase.feature.auth.model.UserRole;
import edu.cit.batucan.StockEase.feature.auth.repository.UserRepository;
import edu.cit.batucan.StockEase.shared.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .passwordHash("encodedPassword")
                .firstname("John")
                .lastname("Doe")
                .role(UserRole.OWNER)
                .build();
    }

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setFirstname("Jane");
        request.setLastname("Smith");
        request.setRole("STAFF");

        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPwd");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u = User.builder()
                    .id(2L).email(u.getEmail()).passwordHash(u.getPasswordHash())
                    .firstname(u.getFirstname()).lastname(u.getLastname()).role(u.getRole())
                    .build();
            return u;
        });
        when(jwtProvider.generateToken(anyString(), anyString())).thenReturn("access-token");

        var response = userService.register(request);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertEquals("newuser@example.com", response.getUser().getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_DuplicateEmail_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        request.setFirstname("John");
        request.setLastname("Doe");
        request.setRole("OWNER");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtProvider.generateToken("test@example.com", "OWNER")).thenReturn("access-token");

        var response = userService.login(request);

        assertNotNull(response);
        assertEquals("test@example.com", response.getUser().getEmail());
        assertEquals("OWNER", response.getUser().getRole());
        assertNotNull(response.getAccessToken());
    }

    @Test
    void testLogin_WrongPassword_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.login(request));
    }

    @Test
    void testLogin_UserNotFound_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.login(request));
    }

    @Test
    void testGetCurrentUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto result = userService.getCurrentUser(1L);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("John", result.getFirstname());
        assertEquals("Doe", result.getLastname());
    }

    @Test
    void testGetCurrentUser_NotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.getCurrentUser(99L));
    }
}
