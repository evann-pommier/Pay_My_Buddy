package com.openclassrooms.Pay_My_Buddy.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.openclassrooms.Pay_My_Buddy.model.Transaction;
import com.openclassrooms.Pay_My_Buddy.model.User;
import com.openclassrooms.Pay_My_Buddy.service.TransactionService;
import com.openclassrooms.Pay_My_Buddy.service.UserService;

@RestController
@RequestMapping("/api")
public class HomeController {

    private final UserService userService;
    private final TransactionService transactionService;

    public HomeController(UserService userService,
                          TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    /**
     * Récupère le profil de l'utilisateur connecté.
     * GET /api/me
     */
    @GetMapping("/me")
    public ResponseEntity<User> me(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return ResponseEntity.ok(user);
    }

    /**
     * Récupère toutes les transactions de l'utilisateur connecté.
     * GET /api/transactions
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> transactions(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return ResponseEntity.ok(transactionService.getTransactions(user));
    }
}