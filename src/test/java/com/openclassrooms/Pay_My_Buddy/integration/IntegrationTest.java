package com.openclassrooms.Pay_My_Buddy.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.Pay_My_Buddy.exception.InsufficientBalanceException;
import com.openclassrooms.Pay_My_Buddy.model.Transaction;
import com.openclassrooms.Pay_My_Buddy.model.User;
import com.openclassrooms.Pay_My_Buddy.service.TransactionService;
import com.openclassrooms.Pay_My_Buddy.service.UserService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    // -------------------------------------------------------
    //  Inscription
    // -------------------------------------------------------

    @Test
    void register_shouldCreateUser() {
        User user = userService.register("Alice", "alice@test.com", "password123");

        assertNotNull(user.getId());
        assertEquals("alice@test.com", user.getEmail());
        assertEquals(BigDecimal.ZERO, user.getBalance());
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        userService.register("Alice", "alice@test.com", "password123");

        assertThrows(IllegalArgumentException.class, () ->
            userService.register("Alice2", "alice@test.com", "password123")
        );
    }

    // -------------------------------------------------------
    //  Connexions
    // -------------------------------------------------------

    @Test
    void addConnection_shouldBeSymmetric() {
        userService.register("Alice", "alice@test.com", "password123");
        userService.register("Bob", "bob@test.com", "password123");

        User alice = userService.findByEmailWithConnections("alice@test.com");
        userService.addConnection(alice, "bob@test.com");

        User aliceAfter = userService.findByEmailWithConnections("alice@test.com");
        User bobAfter = userService.findByEmailWithConnections("bob@test.com");

        assertTrue(aliceAfter.getConnections().stream()
            .anyMatch(u -> u.getEmail().equals("bob@test.com")));
        assertTrue(bobAfter.getConnections().stream()
            .anyMatch(u -> u.getEmail().equals("alice@test.com")));
    }

    @Test
    void addConnection_shouldThrow_whenAddingSelf() {
        userService.register("Alice", "alice@test.com", "password123");
        User alice = userService.findByEmailWithConnections("alice@test.com");

        assertThrows(IllegalArgumentException.class, () ->
            userService.addConnection(alice, "alice@test.com")
        );
    }

    // -------------------------------------------------------
    //  Virements
    // -------------------------------------------------------

    @Test
    void transfer_shouldDebitSenderAndCreditReceiver() {
        // Créer les utilisateurs avec un solde initial
        User alice = userService.register("Alice", "alice@test.com", "password123");
        User bob = userService.register("Bob", "bob@test.com", "password123");

        // Donner un solde à Alice directement via le repository
        alice.setBalance(new BigDecimal("500.00"));
        bob.setBalance(new BigDecimal("100.00"));

        // Ajouter la connexion
        User aliceWithConnections = userService.findByEmailWithConnections("alice@test.com");
        userService.addConnection(aliceWithConnections, "bob@test.com");

        // Effectuer le virement
        User sender = userService.findByEmailWithConnections("alice@test.com");
        Transaction transaction = transactionService.transfer(
            sender, "bob@test.com", new BigDecimal("150.00"), "Test virement");

        // Vérifications
        assertNotNull(transaction.getId());
        assertEquals(new BigDecimal("350.00"), userService.findByEmail("alice@test.com").getBalance());
        assertEquals(new BigDecimal("250.00"), userService.findByEmail("bob@test.com").getBalance());
        assertEquals(new BigDecimal("150.00"), transaction.getAmount());
    }

    @Test
    void transfer_shouldThrow_whenInsufficientBalance() {
        userService.register("Alice", "alice@test.com", "password123");
        userService.register("Bob", "bob@test.com", "password123");

        User aliceWithConnections = userService.findByEmailWithConnections("alice@test.com");
        userService.addConnection(aliceWithConnections, "bob@test.com");

        User sender = userService.findByEmailWithConnections("alice@test.com");

        assertThrows(InsufficientBalanceException.class, () ->
            transactionService.transfer(
                sender, "bob@test.com", new BigDecimal("999.00"), "Test")
        );
    }

    @Test
    void transfer_shouldThrow_whenSelfTransfer() {
        userService.register("Alice", "alice@test.com", "password123");
        User sender = userService.findByEmailWithConnections("alice@test.com");

        assertThrows(IllegalArgumentException.class, () ->
            transactionService.transfer(
                sender, "alice@test.com", new BigDecimal("50.00"), "Test")
        );
    }

    // -------------------------------------------------------
    //  Historique des transactions
    // -------------------------------------------------------

    @Test
    void getTransactions_shouldReturnAllUserTransactions() {
        User alice = userService.register("Alice", "alice@test.com", "password123");
        User bob = userService.register("Bob", "bob@test.com", "password123");

        alice.setBalance(new BigDecimal("500.00"));
        User aliceWithConnections = userService.findByEmailWithConnections("alice@test.com");
        userService.addConnection(aliceWithConnections, "bob@test.com");

        User sender = userService.findByEmailWithConnections("alice@test.com");
        transactionService.transfer(sender, "bob@test.com", new BigDecimal("50.00"), "Test 1");
        transactionService.transfer(sender, "bob@test.com", new BigDecimal("30.00"), "Test 2");

        List<Transaction> transactions = transactionService.getTransactions(
            userService.findByEmail("alice@test.com"));

        assertEquals(2, transactions.size());
    }
}