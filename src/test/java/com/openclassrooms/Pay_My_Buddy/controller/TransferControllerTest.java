package com.openclassrooms.Pay_My_Buddy.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.Pay_My_Buddy.config.SecurityConfig;
import com.openclassrooms.Pay_My_Buddy.exception.InsufficientBalanceException;
import com.openclassrooms.Pay_My_Buddy.model.Transaction;
import com.openclassrooms.Pay_My_Buddy.model.User;
import com.openclassrooms.Pay_My_Buddy.service.CustomUserDetailsService;
import com.openclassrooms.Pay_My_Buddy.service.TransactionService;
import com.openclassrooms.Pay_My_Buddy.service.UserService;

@WebMvcTest(TransferController.class)
@Import(SecurityConfig.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private User buildAlice() {
        User alice = new User();
        alice.setId(1);
        alice.setEmail("alice@email.com");
        alice.setUsername("Alice");
        alice.setBalance(new BigDecimal("500.00"));
        alice.setConnections(new ArrayList<>());
        return alice;
    }

    @Test
    void transfer_shouldReturn200_whenValid() throws Exception {
        User alice = buildAlice();
        User bob = new User();
        bob.setId(2);
        bob.setEmail("bob@email.com");
        bob.setUsername("Bob");
        bob.setConnections(new ArrayList<>());

        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("50.00"));
        transaction.setSender(alice);    // ← ajouté
        transaction.setReceiver(bob);    // ← ajouté

        when(userService.findByEmailWithConnections("alice@email.com")).thenReturn(alice); // ← corrigé
        when(transactionService.transfer(any(), anyString(), any(), anyString())).thenReturn(transaction);

        mockMvc.perform(post("/api/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "receiverEmail": "bob@email.com",
                        "amount": 50.00,
                        "description": "Test"
                    }
                """)
                .with(user("alice@email.com"))
                .with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    void transfer_shouldReturn400_whenInsufficientBalance() throws Exception {
        User alice = buildAlice();
        when(userService.findByEmailWithConnections("alice@email.com")).thenReturn(alice); // ← corrigé
        when(transactionService.transfer(any(), anyString(), any(), anyString()))
            .thenThrow(new InsufficientBalanceException("Solde insuffisant."));

        mockMvc.perform(post("/api/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "receiverEmail": "bob@email.com",
                        "amount": 9999.00,
                        "description": "Test"
                    }
                """)
                .with(user("alice@email.com"))
                .with(csrf()))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void transfer_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "receiverEmail": "bob@email.com",
                        "amount": 50.00,
                        "description": "Test"
                    }
                """)
                .with(csrf()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void connections_shouldReturn200_whenAuthenticated() throws Exception {
        User alice = buildAlice();
        when(userService.findByEmailWithConnections("alice@email.com")).thenReturn(alice);

        mockMvc.perform(get("/api/connections")
                .with(user("alice@email.com")))
            .andExpect(status().isOk());
    }

    @Test
    void addConnection_shouldReturn200_whenValid() throws Exception {
        User alice = buildAlice();
        when(userService.findByEmailWithConnections("alice@email.com")).thenReturn(alice); // ← corrigé
        doNothing().when(userService).addConnection(any(), anyString());

        mockMvc.perform(post("/api/connections")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "friendEmail": "bob@email.com"
                    }
                """)
                .with(user("alice@email.com"))
                .with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    void addConnection_shouldReturn400_whenAlreadyConnected() throws Exception {
        User alice = buildAlice();
        when(userService.findByEmailWithConnections("alice@email.com")).thenReturn(alice); // ← corrigé
        doThrow(new IllegalArgumentException("Déjà connecté."))
            .when(userService).addConnection(any(), anyString());

        mockMvc.perform(post("/api/connections")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "friendEmail": "bob@email.com"
                    }
                """)
                .with(user("alice@email.com"))
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }
}