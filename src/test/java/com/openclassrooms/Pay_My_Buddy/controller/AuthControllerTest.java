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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.Pay_My_Buddy.security.SecurityConfig;
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

    // -------------------------------------------------------
    //  GET /register
    // -------------------------------------------------------

    @Test
    void registerPage_shouldReturn200_andContainForm() throws Exception {
        mockMvc.perform(get("/register"))
            .andExpect(status().isOk())
            .andExpect(view().name("register"))
            .andExpect(model().attributeExists("registerDTO"));
    }

    // -------------------------------------------------------
    //  GET /login
    // -------------------------------------------------------

    @Test
    void loginPage_shouldReturn200() throws Exception {
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk())
            .andExpect(view().name("login"));
    }

    // -------------------------------------------------------
    //  POST /register — succès
    // -------------------------------------------------------

    @Test
    void register_shouldRedirectToLogin_whenSuccess() throws Exception {
        User user = new User();
        user.setEmail("alice@email.com");
        user.setUsername("Alice");
        when(userService.register(anyString(), anyString(), anyString())).thenReturn(user);

        mockMvc.perform(post("/register")
                .param("username", "Alice")
                .param("email", "alice@email.com")
                .param("password", "password123")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?registered"));
    }

    // -------------------------------------------------------
    //  POST /register — email déjà utilisé
    // -------------------------------------------------------

    @Test
    void register_shouldReturnRegisterView_whenEmailAlreadyExists() throws Exception {
        when(userService.register(anyString(), anyString(), anyString()))
            .thenThrow(new IllegalArgumentException("Cet email est déjà utilisé."));

        mockMvc.perform(post("/register")
                .param("username", "Alice")
                .param("email", "alice@email.com")
                .param("password", "password123")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("register"))
            .andExpect(model().attributeExists("errorMessage"));
    }

    // -------------------------------------------------------
    //  POST /register — username déjà pris
    // -------------------------------------------------------

    @Test
    void register_shouldReturnRegisterView_whenUsernameAlreadyExists() throws Exception {
        when(userService.register(anyString(), anyString(), anyString()))
            .thenThrow(new IllegalArgumentException("Ce nom d'utilisateur est déjà pris."));

        mockMvc.perform(post("/register")
                .param("username", "Alice")
                .param("email", "alice@email.com")
                .param("password", "password123")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("register"))
            .andExpect(model().attributeExists("errorMessage"));
    }

    // -------------------------------------------------------
    //  POST /register — validation Bean Validation : email invalide
    // -------------------------------------------------------

    @Test
    void register_shouldReturnRegisterView_whenEmailInvalid() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "Alice")
                .param("email", "pas-un-email")
                .param("password", "password123")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("register"))
            .andExpect(model().hasErrors());
    }

    // -------------------------------------------------------
    //  POST /register — validation Bean Validation : mot de passe trop court
    // -------------------------------------------------------

    @Test
    void register_shouldReturnRegisterView_whenPasswordTooShort() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "Alice")
                .param("email", "alice@email.com")
                .param("password", "court")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("register"))
            .andExpect(model().hasErrors());
    }

    // -------------------------------------------------------
    //  POST /register — validation Bean Validation : username vide
    // -------------------------------------------------------

    @Test
    void register_shouldReturnRegisterView_whenUsernameBlank() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "")
                .param("email", "alice@email.com")
                .param("password", "password123")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("register"))
            .andExpect(model().hasErrors());
    }

    // -------------------------------------------------------
    //  POST /register — pas de token CSRF
    // -------------------------------------------------------

    @Test
    void register_shouldReturn403_whenNoCsrf() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "Alice")
                .param("email", "alice@email.com")
                .param("password", "password123"))
            .andExpect(status().isForbidden());
    }
}