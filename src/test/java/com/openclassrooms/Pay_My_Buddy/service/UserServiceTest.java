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
import org.mockito.ArgumentCaptor;
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
    void register_shouldCreateUser_whenEmailAndUsernameAreNew() {
        when(userRepository.existsByEmail("alice@email.com")).thenReturn(false);
        when(userRepository.existsByUsername("Alice Martin")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(alice);

        User result = userService.register("Alice Martin", "alice@email.com", "password123");

        assertNotNull(result);
        assertSame(alice, result);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("Alice Martin", savedUser.getUsername());
        assertEquals("alice@email.com", savedUser.getEmail());
        assertEquals("hashedPassword", savedUser.getPassword());
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

    @Test
    void register_shouldThrowException_whenUsernameAlreadyExists() {
        when(userRepository.existsByEmail("alice2@email.com")).thenReturn(false);
        when(userRepository.existsByUsername("Alice Martin")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.register("Alice Martin", "alice2@email.com", "password123")
        );

        assertEquals("Ce nom d'utilisateur est déjà pris.", exception.getMessage());
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

        assertTrue(alice.getConnections().contains(bob));
        assertTrue(bob.getConnections().contains(alice));
        verify(userRepository).save(alice);
        verify(userRepository).save(bob);
    }

    @Test
    void addConnection_shouldThrowException_whenAddingSelf() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.addConnection(alice, "alice@email.com")
        );

        assertEquals("Vous ne pouvez pas vous ajouter vous-même.", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
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
        verify(userRepository, never()).save(any(User.class));
    }

    // -------------------------------------------------------
    //  Tests updateProfile() — NOUVEAUX
    // -------------------------------------------------------

    @Test
    void updateProfile_shouldUpdateUsernameAndEmail_whenValid() {
        when(userRepository.findByEmail("alice@email.com")).thenReturn(Optional.of(alice));
        when(userRepository.existsByEmail("alice.new@email.com")).thenReturn(false);
        when(userRepository.existsByUsername("Alice New")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(alice);

        User result = userService.updateProfile("alice@email.com", "Alice New", "alice.new@email.com", null);

        assertSame(alice, result);
        assertEquals("Alice New", alice.getUsername());
        assertEquals("alice.new@email.com", alice.getEmail());
        verify(userRepository).save(alice);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateProfile_shouldUpdatePassword_whenNewPasswordProvided() {
        when(userRepository.findByEmail("alice@email.com")).thenReturn(Optional.of(alice));
        when(passwordEncoder.encode("newpassword123")).thenReturn("newHashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(alice);

        User result = userService.updateProfile("alice@email.com", "Alice Martin", "alice@email.com", "newpassword123");

        assertSame(alice, result);
        assertEquals("newHashedPassword", alice.getPassword());
        verify(passwordEncoder).encode("newpassword123");
        verify(userRepository).save(alice);
    }

    @Test
    void updateProfile_shouldNotUpdatePassword_whenNewPasswordIsBlank() {
        when(userRepository.findByEmail("alice@email.com")).thenReturn(Optional.of(alice));
        when(userRepository.save(any(User.class))).thenReturn(alice);

        User result = userService.updateProfile("alice@email.com", "Alice Martin", "alice@email.com", "  ");

        assertSame(alice, result);
        assertEquals("hashedPassword", alice.getPassword());
        verify(userRepository).save(alice);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateProfile_shouldThrowException_whenNewEmailAlreadyTaken() {
        when(userRepository.findByEmail("alice@email.com")).thenReturn(Optional.of(alice));
        when(userRepository.existsByEmail("taken@email.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.updateProfile("alice@email.com", "Alice", "taken@email.com", null)
        );

        assertEquals("Cet email est déjà utilisé.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateProfile_shouldThrowException_whenNewUsernameAlreadyTaken() {
        when(userRepository.findByEmail("alice@email.com")).thenReturn(Optional.of(alice));
        when(userRepository.existsByUsername("Bob Dupont")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.updateProfile("alice@email.com", "Bob Dupont", "alice@email.com", null)
        );

        assertEquals("Ce nom d'utilisateur est déjà pris.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateProfile_shouldThrowException_whenNewPasswordTooShort() {
        when(userRepository.findByEmail("alice@email.com")).thenReturn(Optional.of(alice));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.updateProfile("alice@email.com", "Alice Martin", "alice@email.com", "court")
        );

        assertEquals("Le mot de passe doit contenir au moins 8 caractères.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}