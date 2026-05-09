package edu.cit.batucan.StockEase.security;

import edu.cit.batucan.StockEase.shared.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=stockease-secret-key-minimum-32-chars-needed-123456789",
    "jwt.expiration=86400000",
    "jwt.refresh.expiration=604800000"
})
public class JwtProviderTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    public void testGenerateAndValidateAccessToken() {
        String token = jwtProvider.generateAccessToken(1L, "test@example.com", "OWNER");
        
        assertTrue(jwtProvider.validateToken(token), "Token should be valid");
        assertEquals("test@example.com", jwtProvider.getEmailFromToken(token), "Email should match");
        assertEquals(1L, jwtProvider.getUserIdFromToken(token), "User ID should match");
        assertEquals("OWNER", jwtProvider.getRoleFromToken(token), "Role should match");
    }

    @Test
    public void testGenerateRefreshToken() {
        String refreshToken = jwtProvider.generateRefreshToken(1L);
        
        assertTrue(jwtProvider.validateToken(refreshToken), "Refresh token should be valid");
        assertEquals("1", jwtProvider.getEmailFromToken(refreshToken), "User ID subject should match");
    }

    @Test
    public void testInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertFalse(jwtProvider.validateToken(invalidToken), "Invalid token should not validate");
    }

    @Test
    public void testTokenWithDifferentRoles() {
        String ownerToken = jwtProvider.generateAccessToken(1L, "owner@example.com", "OWNER");
        String staffToken = jwtProvider.generateAccessToken(2L, "staff@example.com", "STAFF");
        
        assertEquals("OWNER", jwtProvider.getRoleFromToken(ownerToken));
        assertEquals("STAFF", jwtProvider.getRoleFromToken(staffToken));
    }
}
