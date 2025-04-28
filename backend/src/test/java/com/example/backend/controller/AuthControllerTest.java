package com.example.backend.controller;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.model.User;
import com.example.backend.security.JwtUtil;
import com.example.backend.service.UserDetailsServiceImpl;
import com.example.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    // Test data
    private final String TEST_USERNAME = "testuser";
    private final String TEST_PASSWORD = "password123";
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_USER_ID = "user123";
    private final String TEST_TOKEN = "jwt.test.token";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();
    }

    @Test
    void register_ShouldCreateUserAndReturnToken() throws Exception {
        // ARRANGE
        RegisterRequest request = new RegisterRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);
        request.setEmail(TEST_EMAIL);

        User savedUser = new User();
        savedUser.setId(TEST_USER_ID);
        savedUser.setUsername(TEST_USERNAME);
        savedUser.setEmail(TEST_EMAIL);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(TEST_USERNAME)
                .password("encodedPassword")
                .authorities("USER")
                .build();

        when(userService.registerUser(any(User.class))).thenReturn(savedUser);
        when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(TEST_TOKEN);

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(TEST_TOKEN)))
                .andExpect(jsonPath("$.userId", is(TEST_USER_ID)))
                .andExpect(jsonPath("$.username", is(TEST_USERNAME)));
    }

    @Test
    void register_WithExistingUsername_ShouldReturnBadRequest() throws Exception {
        // ARRANGE
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setPassword(TEST_PASSWORD);
        request.setEmail(TEST_EMAIL);

        // Mock service throwing exception
        when(userService.registerUser(any(User.class)))
                .thenThrow(new RuntimeException("Username already exists"));

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() throws Exception {
        // ARRANGE
        LoginRequest request = new LoginRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);

        User user = new User();
        user.setId(TEST_USER_ID);
        user.setUsername(TEST_USERNAME);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(TEST_USERNAME)
                .password("encodedPassword")
                .authorities("USER")
                .build();

        // Mock successful authentication
        when(userService.findByUsername(TEST_USERNAME)).thenReturn(user);
        when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(TEST_TOKEN);

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(TEST_TOKEN)))
                .andExpect(jsonPath("$.userId", is(TEST_USER_ID)))
                .andExpect(jsonPath("$.username", is(TEST_USERNAME)));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        // ARRANGE
        LoginRequest request = new LoginRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword("wrongPassword");

        // Mock authentication failure
        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}