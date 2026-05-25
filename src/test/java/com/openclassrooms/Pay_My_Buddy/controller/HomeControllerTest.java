package com.openclassrooms.Pay_My_Buddy.controller;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.Pay_My_Buddy.security.SecurityConfig;
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

    private User alice;

    @BeforeEach
    void setUp() {
        alice = new User();
        alice.setId(1);
        alice.setEmail("alice@email.com");
        alice.setUsername("Alice");
        alice.setBalance(new BigDecimal("500.00"));
        alice.setConnections(new ArrayList<>());
    }

    // -------------------------------------------------------
    //  GET /home
    // -------------------------------------------------------

    @Test
    void homePage_shouldReturn200_andInjectModel() throws Exception {
        when(userService.findByEmail("alice@email.com")).thenReturn(alice);
        when(transactionService.getTransactions(alice)).thenReturn(List.of());

        mockMvc.perform(get("/home").with(user("alice@email.com")))
            .andExpect(status().isOk())
            .andExpect(view().name("home"))
            .andExpect(model().attributeExists("user"))
            .andExpect(model().attributeExists("transactions"));
    }

    @Test
    void homePage_shouldRedirectToLogin_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/home"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    // -------------------------------------------------------
    //  GET /profile
    // -------------------------------------------------------

    @Test
    void profilePage_shouldReturn200_andInjectUser() throws Exception {
        when(userService.findByEmail("alice@email.com")).thenReturn(alice);

        mockMvc.perform(get("/profile").with(user("alice@email.com")))
            .andExpect(status().isOk())
            .andExpect(view().name("profile"))
            .andExpect(model().attributeExists("user"));
    }

    @Test
    void profilePage_shouldRedirectToLogin_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/profile"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    // -------------------------------------------------------
    //  POST /profile — succès
    // -------------------------------------------------------

    @Test
    void updateProfile_shouldRedirectToProfile_whenSuccess() throws Exception {
        when(userService.findByEmail("alice@email.com")).thenReturn(alice);
        when(userService.updateProfile(anyString(), anyString(), anyString(), any()))
            .thenReturn(alice);

        UserDetails details = org.springframework.security.core.userdetails.User
            .withUsername("alice@email.com")
            .password(alice.getPassword() != null ? alice.getPassword() : "")
            .roles("USER").build();
        when(customUserDetailsService.loadUserByUsername(anyString())).thenReturn(details);

        mockMvc.perform(post("/profile")
                .param("username", "Alice Updated")
                .param("email", "alice@email.com")
                .param("newPassword", "")
                .with(user("alice@email.com"))
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/profile"));
    }

    // -------------------------------------------------------
    //  POST /profile — email déjà utilisé
    // -------------------------------------------------------

    @Test
    void updateProfile_shouldRedirectWithError_whenEmailAlreadyExists() throws Exception {
        when(userService.updateProfile(anyString(), anyString(), anyString(), any()))
            .thenThrow(new IllegalArgumentException("Cet email est déjà utilisé."));

        mockMvc.perform(post("/profile")
                .param("username", "Alice")
                .param("email", "taken@email.com")
                .param("newPassword", "")
                .with(user("alice@email.com"))
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/profile"))
            .andExpect(flash().attributeExists("errorMessage"));
    }

    // -------------------------------------------------------
    //  GET / — redirection vers /home
    // -------------------------------------------------------

    @Test
    void root_shouldRedirectToHome_whenAuthenticated() throws Exception {
        mockMvc.perform(get("/").with(user("alice@email.com")))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/home"));
    }
}