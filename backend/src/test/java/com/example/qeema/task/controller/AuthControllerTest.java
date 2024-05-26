package com.example.qeema.task.controller;

import com.example.qeema.task.entity.User;
import com.example.qeema.task.service.JwtService;
import com.example.qeema.task.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void testRegister() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "testuser");
        requestBody.put("password", "password");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setPassword("hashedpassword");

        when(bCryptPasswordEncoder.encode("password")).thenReturn("hashedpassword");
        doAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(user.getId());
            return null;
        }).when(userService).save(any(User.class));
        when(jwtService.generateToken(eq(user.getId()))).thenReturn("jwtToken");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("jwtToken"));
    }

    @Test
    public void testLogin_Success() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "testuser");
        requestBody.put("password", "password");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setPassword("hashedpassword");

        when(userService.findByUsername("testuser")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("password", "hashedpassword")).thenReturn(true);
        when(jwtService.generateToken(eq(user.getId()))).thenReturn("jwtToken");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("jwtToken"));
    }

    @Test
    public void testLogin_UserNotFound() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "testuser");
        requestBody.put("password", "password");

        when(userService.findByUsername("testuser")).thenReturn(null);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not found"));
    }

    @Test
    public void testLogin_InvalidPassword() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "testuser");
        requestBody.put("password", "password");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setPassword("hashedpassword");

        when(userService.findByUsername("testuser")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("password", "hashedpassword")).thenReturn(false);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not found"));
    }
}
