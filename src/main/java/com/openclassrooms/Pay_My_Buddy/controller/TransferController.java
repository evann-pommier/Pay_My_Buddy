package com.openclassrooms.Pay_My_Buddy.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.openclassrooms.Pay_My_Buddy.dto.AddConnectionDTO;
import com.openclassrooms.Pay_My_Buddy.dto.TransactionDTO;
import com.openclassrooms.Pay_My_Buddy.dto.TransferDTO;
import com.openclassrooms.Pay_My_Buddy.dto.UserDTO;
import com.openclassrooms.Pay_My_Buddy.mapper.Mapper;
import com.openclassrooms.Pay_My_Buddy.model.Transaction;
import com.openclassrooms.Pay_My_Buddy.model.User;
import com.openclassrooms.Pay_My_Buddy.service.TransactionService;
import com.openclassrooms.Pay_My_Buddy.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class TransferController {

    private final UserService userService;
    private final TransactionService transactionService;

    public TransferController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    /**
     * Effectue un virement.
     * POST /api/transfer
     */
    @PostMapping("/transfer")
    public ResponseEntity<TransactionDTO> transfer(Principal principal, @Valid @RequestBody TransferDTO dto) {
        User sender = userService.findByEmailWithConnections(principal.getName());
        Transaction transaction = transactionService.transfer(sender, dto.receiverEmail(), dto.amount(), dto.description());
        return ResponseEntity.ok(Mapper.toTransactionDTO(transaction));
    }

    /**
     * Récupère les connexions de l'utilisateur connecté.
     * GET /api/connections
     */
    @GetMapping("/connections")
    public ResponseEntity<List<UserDTO>> connections(Principal principal) {
        User user = userService.findByEmailWithConnections(principal.getName());
        return ResponseEntity.ok(
            user.getConnections()
                .stream()
                .map(Mapper::toUserDTO)
                .toList());
    }

    /**
     * Ajoute un ami.
     * POST /api/connections
     */
    @PostMapping("/connections")
    public ResponseEntity<Void> addConnection(Principal principal, @Valid @RequestBody AddConnectionDTO dto) {
        User user = userService.findByEmailWithConnections(principal.getName());
        userService.addConnection(user, dto.friendEmail());
        return ResponseEntity.ok().build();
    }
}