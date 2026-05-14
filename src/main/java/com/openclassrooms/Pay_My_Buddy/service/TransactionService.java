package com.openclassrooms.Pay_My_Buddy.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.Pay_My_Buddy.exception.InsufficientBalanceException;
import com.openclassrooms.Pay_My_Buddy.model.Transaction;
import com.openclassrooms.Pay_My_Buddy.model.User;
import com.openclassrooms.Pay_My_Buddy.repository.TransactionRepository;
import com.openclassrooms.Pay_My_Buddy.repository.UserRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Effectue un virement entre deux utilisateurs.
     * @Transactional garantit que si une étape échoue,
     * tout est annulé (rollback automatique).
     */
    @Transactional
    public Transaction transfer(User sender, String receiverEmail, BigDecimal amount, String description) {
        // Règle : pas de virement vers soi-même
        if (sender.getEmail().equals(receiverEmail)) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous envoyer de l'argent.");
        }

        // Règle : montant positif
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à 0.");
        }

        // Règle : solde suffisant
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Solde insuffisant pour effectuer ce virement.");
        }

        // Recherche du destinataire
        User receiver = userRepository.findByEmail(receiverEmail)
            .orElseThrow(() -> new IllegalArgumentException("Destinataire introuvable."));

        // Débit expéditeur / crédit destinataire
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        userRepository.save(sender);
        userRepository.save(receiver);

        // Création de la transaction
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setDescription(description);

        return transactionRepository.save(transaction);
    }

    /**
     * Récupère toutes les transactions d'un utilisateur.
     */
    public List<Transaction> getTransactions(User user) {
        return transactionRepository
            .findBySenderOrReceiverOrderByCreatedAtDesc(user, user);
    }
}