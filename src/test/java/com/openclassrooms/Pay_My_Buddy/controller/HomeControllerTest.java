package com.openclassrooms.Pay_My_Buddy.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.Pay_My_Buddy.config.SecurityConfig;
import com.openclassrooms.Pay_My_Buddy.model.User;
import com.openclassrooms.Pay_My_Buddy.service.CustomUserDetailsService;
import com.openclassrooms.Pay_My_Buddy.service.TransactionService;
import com.openclassrooms.Pay_My_Buddy.service.UserService;

@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void me_shouldReturn200_whenAuthenticated() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("alice@email.com");
        user.setUsername("Alice");
        user.setBalance(new BigDecimal("500.00"));
        user.setConnections(new ArrayList<>());

        when(userService.findByEmail("alice@email.com")).thenReturn(user);

        mockMvc.perform(get("/api/me")
                .with(user("alice@email.com")))
            .andExpect(status().isOk());
    }

    @Test
    void me_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/me"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void transactions_shouldReturn200_whenAuthenticated() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("alice@email.com");
        user.setConnections(new ArrayList<>());

        when(userService.findByEmail("alice@email.com")).thenReturn(user);
        when(transactionService.getTransactions(user)).thenReturn(List.of());

        mockMvc.perform(get("/api/transactions")
                .with(user("alice@email.com")))
            .andExpect(status().isOk());
    }
}