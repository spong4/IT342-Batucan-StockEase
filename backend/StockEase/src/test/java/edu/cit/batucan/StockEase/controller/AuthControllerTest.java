package edu.cit.batucan.StockEase.controller;

import edu.cit.batucan.StockEase.feature.auth.dto.LoginRequest;
import edu.cit.batucan.StockEase.feature.auth.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("null")
@TestPropertySource(properties = {
    "jwt.secret=stockease-secret-key-minimum-32-chars-needed-123456789",
    "jwt.expiration=86400000",
    "jwt.refresh.expiration=604800000"
})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setFirstname("John");
        request.setLastname("Doe");
        request.setRole("OWNER");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    public void testLoginSuccess() throws Exception {
        // First register
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("logintest@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstname("Test");
        registerRequest.setLastname("User");
        registerRequest.setRole("STAFF");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Then login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("logintest@example.com");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user.email").value("logintest@example.com"))
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    public void testLoginInvalidPassword() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("invalidpwd@example.com");
        registerRequest.setPassword("correctpassword");
        registerRequest.setFirstname("Test");
        registerRequest.setLastname("User");
        registerRequest.setRole("OWNER");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("invalidpwd@example.com");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH-001"));
    }

    @Test
    public void testRegisterDuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("duplicate@example.com");
        request.setPassword("password123");
        request.setFirstname("John");
        request.setLastname("Doe");
        request.setRole("OWNER");

        // First registration
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second registration with same email
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALID-001"));
    }
}
