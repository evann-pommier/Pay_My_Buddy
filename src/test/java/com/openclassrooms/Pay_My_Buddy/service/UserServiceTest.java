package com.openclassrooms.Pay_My_Buddy.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.openclassrooms.Pay_My_Buddy.model.User;
import com.openclassrooms.Pay_My_Buddy.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User alice;
    private User bob;

    @BeforeEach
    void setUp() {
        alice = new User();
        alice.setId(1);
        alice.setUsername("Alice Martin");
        alice.setEmail("alice@email.com");
        alice.setPassword("hashedPassword");
        alice.setBalance(new BigDecimal("500.00"));
        alice.setConnections(new ArrayList<>());

        bob = new User();
        bob.setId(2);
        bob.setUsername("Bob Dupont");
        bob.setEmail("bob@email.com");
        bob.setPassword("hashedPassword");
        bob.setBalance(new BigDecimal("250.00"));
        bob.setConnections(new ArrayList<>());
    }

    // -------------------------------------------------------
    //  Tests register()
    // -------------------------------------------------------

    @Test
    void register_shouldCreateUser_whenEmailIsNew() {
        when(userRepository.existsByEmail("alice@email.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(alice);

        User result = userService.register("Alice Martin", "alice@email.com", "password123");

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail("alice@email.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.register("Alice Martin", "alice@email.com", "password123")
        );

        assertEquals("Cet email est déjà utilisé.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    // -------------------------------------------------------
    //  Tests findByEmail()
    // -------------------------------------------------------

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {
        when(userRepository.findByEmail("alice@email.com")).thenReturn(Optional.of(alice));

        User result = userService.findByEmail("alice@email.com");

        assertNotNull(result);
        assertEquals("alice@email.com", result.getEmail());
    }

    @Test
    void findByEmail_shouldThrowException_whenEmailNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(
            IllegalArgumentException.class,
            () -> userService.findByEmail("inconnu@email.com")
        );
    }

    // -------------------------------------------------------
    //  Tests addConnection()
    // -------------------------------------------------------

    @Test
    void addConnection_shouldAddFriend_whenValid() {
        when(userRepository.findByEmail("bob@email.com")).thenReturn(Optional.of(bob));
        when(userRepository.save(any(User.class))).thenReturn(alice);

        userService.addConnection(alice, "bob@email.com");

        // Vérification dans les deux sens
        assertTrue(alice.getConnections().contains(bob));
        assertTrue(bob.getConnections().contains(alice));

        // Les deux users sont sauvegardés
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void addConnection_shouldThrowException_whenAddingSelf() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.addConnection(alice, "alice@email.com")
        );

        assertEquals("Vous ne pouvez pas vous ajouter vous-même.", exception.getMessage());
    }

    @Test
    void addConnection_shouldThrowException_whenAlreadyConnected() {
        alice.getConnections().add(bob);
        when(userRepository.findByEmail("bob@email.com")).thenReturn(Optional.of(bob));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.addConnection(alice, "bob@email.com")
        );

        assertEquals("Cet utilisateur est déjà dans vos connexions.", exception.getMessage());
    }
}