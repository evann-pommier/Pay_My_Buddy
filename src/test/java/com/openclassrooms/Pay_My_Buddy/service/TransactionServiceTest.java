package com.openclassrooms.Pay_My_Buddy.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.Pay_My_Buddy.exception.InsufficientBalanceException;
import com.openclassrooms.Pay_My_Buddy.model.Transaction;
import com.openclassrooms.Pay_My_Buddy.model.User;
import com.openclassrooms.Pay_My_Buddy.repository.TransactionRepository;
import com.openclassrooms.Pay_My_Buddy.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

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
    //  Tests transfer()
    // -------------------------------------------------------

    @Test
    void transfer_shouldDebitSenderAndCreditReceiver_whenValid() {
        when(userRepository.findByEmail("bob@email.com")).thenReturn(Optional.of(bob));
        when(userRepository.save(any(User.class))).thenReturn(alice);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = transactionService.transfer(
            alice, "bob@email.com", new BigDecimal("100.00"), "Test virement");

        // Alice débité de 100
        assertEquals(new BigDecimal("400.00"), alice.getBalance());
        // Bob crédité de 100
        assertEquals(new BigDecimal("350.00"), bob.getBalance());
        // Transaction bien créée
        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals(alice, result.getSender());
        assertEquals(bob, result.getReceiver());

        verify(userRepository, times(2)).save(any(User.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void transfer_shouldThrowException_whenSelfTransfer() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> transactionService.transfer(
                alice, "alice@email.com", new BigDecimal("100.00"), "Test")
        );

        assertEquals("Vous ne pouvez pas vous envoyer de l'argent.", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transfer_shouldThrowException_whenAmountIsZero() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> transactionService.transfer(
                alice, "bob@email.com", BigDecimal.ZERO, "Test")
        );

        assertEquals("Le montant doit être supérieur à 0.", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transfer_shouldThrowException_whenAmountIsNegative() {
        assertThrows(
            IllegalArgumentException.class,
            () -> transactionService.transfer(
                alice, "bob@email.com", new BigDecimal("-50.00"), "Test")
        );

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transfer_shouldThrowInsufficientBalanceException_whenNotEnoughMoney() {
        InsufficientBalanceException exception = assertThrows(
            InsufficientBalanceException.class,
            () -> transactionService.transfer(
                alice, "bob@email.com", new BigDecimal("600.00"), "Test")
        );

        assertEquals("Solde insuffisant pour effectuer ce virement.", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transfer_shouldThrowException_whenReceiverNotFound() {
        when(userRepository.findByEmail("inconnu@email.com")).thenReturn(Optional.empty());

        assertThrows(
            IllegalArgumentException.class,
            () -> transactionService.transfer(
                alice, "inconnu@email.com", new BigDecimal("100.00"), "Test")
        );

        verify(transactionRepository, never()).save(any());
    }

    // -------------------------------------------------------
    //  Tests getTransactions()
    // -------------------------------------------------------

    @Test
    void getTransactions_shouldReturnAllUserTransactions() {
        Transaction t1 = new Transaction();
        t1.setSender(alice);
        t1.setReceiver(bob);
        t1.setAmount(new BigDecimal("50.00"));

        Transaction t2 = new Transaction();
        t2.setSender(bob);
        t2.setReceiver(alice);
        t2.setAmount(new BigDecimal("30.00"));

        when(transactionRepository.findAllByUserWithDetails(alice))
            .thenReturn(List.of(t1, t2));

        List<Transaction> result = transactionService.getTransactions(alice);

        assertEquals(2, result.size());
        verify(transactionRepository).findAllByUserWithDetails(alice);
    }

    @Test
    void getTransactions_shouldReturnEmptyList_whenNoTransactions() {
        when(transactionRepository.findAllByUserWithDetails(alice))
            .thenReturn(List.of());

        List<Transaction> result = transactionService.getTransactions(alice);

        assertTrue(result.isEmpty());
    }
}