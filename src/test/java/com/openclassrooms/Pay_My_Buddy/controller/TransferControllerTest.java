package com.openclassrooms.Pay_My_Buddy.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.Pay_My_Buddy.security.SecurityConfig;
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

    private User alice;
    private User bob;

    @BeforeEach
    void setUp() {
        alice = new User();
        alice.setId(1);
        alice.setEmail("alice@email.com");
        alice.setUsername("Alice");
        alice.setBalance(new BigDecimal("500.00"));
        alice.setConnections(new ArrayList<>());

        bob = new User();
        bob.setId(2);
        bob.setEmail("bob@email.com");
        bob.setUsername("Bob");
        bob.setBalance(new BigDecimal("250.00"));
        bob.setConnections(new ArrayList<>());

        alice.getConnections().add(bob);
    }

    // -------------------------------------------------------
    //  GET /transfer
    // -------------------------------------------------------

    @Test
    void transferPage_shouldReturn200_andInjectModel() throws Exception {
        when(userService.findByEmailWithConnections("alice@email.com")).thenReturn(alice);
        when(transactionService.getTransactions(alice)).thenReturn(List.of());

        mockMvc.perform(get("/transfer").with(user("alice@email.com")))
            .andExpect(status().isOk())
            .andExpect(view().name("transfer"))
            .andExpect(model().attributeExists("user"))
            .andExpect(model().attributeExists("connections"))
            .andExpect(model().attributeExists("transactions"));
    }

    @Test
    void transferPage_shouldRedirectToLogin_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/transfer"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    // -------------------------------------------------------
    //  POST /transfer — succès
    // -------------------------------------------------------

    @Test
    void transfer_shouldRedirectWithSuccess_whenValid() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("50.00"));
        transaction.setSender(alice);
        transaction.setReceiver(bob);

        when(userService.findByEmailWithConnections("alice@email.com")).thenReturn(alice);
        when(transactionService.transfer(any(), anyString(), any(), any()))
            .thenReturn(transaction);

        mockMvc.perform(post("/transfer")
                .param("receiverEmail", "bob@email.com")
                .param("amount", "50.00")
                .param("description", "Test virement")
                .with(user("alice@email.com"))
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/transfer"))
            .andExpect(flash().attributeExists("successMessage"));
    }

    // -------------------------------------------------------
    //  POST /transfer — solde insuffisant
    // -------------------------------------------------------

    @Test
    void transfer_shouldRedirectWithError_whenInsufficientBalance() throws Exception {
        when(userService.findByEmailWithConnections("alice@email.com")).thenReturn(alice);
        when(transactionService.transfer(any(), anyString(), any(), any()))
            .thenThrow(new InsufficientBalanceException("Solde insuffisant."));

        mockMvc.perform(post("/transfer")
                .param("receiverEmail", "bob@email.com")
                .param("amount", "9999.00")
                .param("description", "Test")
                .with(user("alice@email.com"))
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/transfer"))
            .andExpect(flash().attributeExists("errorMessage"));
    }

    // -------------------------------------------------------
    //  POST /transfer — destinataire non contact
    // -------------------------------------------------------

    @Test
    void transfer_shouldRedirectWithError_whenReceiverNotContact() throws Exception {
        when(userService.findByEmailWithConnections("alice@email.com")).thenReturn(alice);
        when(transactionService.transfer(any(), anyString(), any(), any()))
            .thenThrow(new IllegalArgumentException(
                "Vous ne pouvez envoyer de l'argent qu'à vos contacts."));

        mockMvc.perform(post("/transfer")
                .param("receiverEmail", "stranger@email.com")
                .param("amount", "50.00")
                .param("description", "Test")
                .with(user("alice@email.com"))
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/transfer"))
            .andExpect(flash().attributeExists("errorMessage"));
    }

    // -------------------------------------------------------
    //  POST /transfer — non authentifié
    // -------------------------------------------------------

    @Test
    void transfer_shouldRedirectToLogin_whenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/transfer")
                .param("receiverEmail", "bob@email.com")
                .param("amount", "50.00")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    // -------------------------------------------------------
    //  GET /add-connection
    // -------------------------------------------------------

    @Test
    void addConnectionPage_shouldReturn200() throws Exception {
        mockMvc.perform(get("/add-connection").with(user("alice@email.com")))
            .andExpect(status().isOk())
            .andExpect(view().name("add-connection"));
    }

    @Test
    void addConnectionPage_shouldRedirectToLogin_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/add-connection"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    // -------------------------------------------------------
    //  POST /add-connection — succès
    // -------------------------------------------------------

    @Test
    void addConnection_shouldRedirectWithSuccess_whenValid() throws Exception {
        when(userService.findByEmailWithConnections("alice@email.com")).thenReturn(alice);
        doNothing().when(userService).addConnection(any(), anyString());

        mockMvc.perform(post("/add-connection")
                .param("friendEmail", "bob@email.com")
                .with(user("alice@email.com"))
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/add-connection"))
            .andExpect(flash().attributeExists("successMessage"));
    }

    // -------------------------------------------------------
    //  POST /add-connection — déjà connecté
    // -------------------------------------------------------

    @Test
    void addConnection_shouldRedirectWithError_whenAlreadyConnected() throws Exception {
        when(userService.findByEmailWithConnections("alice@email.com")).thenReturn(alice);
        doThrow(new IllegalArgumentException("Cet utilisateur est déjà dans vos connexions."))
            .when(userService).addConnection(any(), anyString());

        mockMvc.perform(post("/add-connection")
                .param("friendEmail", "bob@email.com")
                .with(user("alice@email.com"))
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/add-connection"))
            .andExpect(flash().attributeExists("errorMessage"));
    }

    // -------------------------------------------------------
    //  POST /add-connection — auto-connexion
    // -------------------------------------------------------

    @Test
    void addConnection_shouldRedirectWithError_whenAddingSelf() throws Exception {
        when(userService.findByEmailWithConnections("alice@email.com")).thenReturn(alice);
        doThrow(new IllegalArgumentException("Vous ne pouvez pas vous ajouter vous-même."))
            .when(userService).addConnection(any(), anyString());

        mockMvc.perform(post("/add-connection")
                .param("friendEmail", "alice@email.com")
                .with(user("alice@email.com"))
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/add-connection"))
            .andExpect(flash().attributeExists("errorMessage"));
    }

    // -------------------------------------------------------
    //  POST /add-connection — pas de CSRF
    // -------------------------------------------------------

    @Test
    void addConnection_shouldReturn403_whenNoCsrf() throws Exception {
        mockMvc.perform(post("/add-connection")
                .param("friendEmail", "bob@email.com")
                .with(user("alice@email.com")))
            .andExpect(status().isForbidden());
    }
}