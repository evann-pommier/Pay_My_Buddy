package com.openclassrooms.Pay_My_Buddy.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.Pay_My_Buddy.config.SecurityConfig;
import com.openclassrooms.Pay_My_Buddy.model.User;
import com.openclassrooms.Pay_My_Buddy.service.CustomUserDetailsService;
import com.openclassrooms.Pay_My_Buddy.service.UserService;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void register_shouldReturn200_whenSuccess() throws Exception {
        User user = new User();
        user.setEmail("alice@email.com");
        user.setUsername("Alice");
        when(userService.register(anyString(), anyString(), anyString()))
            .thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "Alice Martin",
                        "email": "alice@email.com",
                        "password": "password123"
                    }
                """)
                .with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    void register_shouldReturn400_whenEmailAlreadyExists() throws Exception {
        when(userService.register(anyString(), anyString(), anyString()))
            .thenThrow(new IllegalArgumentException("Cet email est déjà utilisé."));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "Alice Martin",
                        "email": "alice@email.com",
                        "password": "password123"
                    }
                """)
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturn400_whenInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "Alice",
                        "email": "pas-un-email",
                        "password": "password123"
                    }
                """)
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturn403_whenNoCsrf() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "Alice",
                        "email": "alice@email.com",
                        "password": "password123"
                    }
                """))
            .andExpect(status().isForbidden());
    }
}