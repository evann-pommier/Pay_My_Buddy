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

/**
 * Service métier gérant les opérations sur les transactions financières.
 * <p>
 * Applique les règles métier de transfert et délègue la persistance
 * aux repositories.
 * </p>
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    /**
     * Construit le service avec les repositories requis.
     *
     * @param transactionRepository le repository d'accès aux transactions
     * @param userRepository        le repository d'accès aux utilisateurs
     */
    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Effectue un virement d'un expéditeur vers un destinataire.
     * <p>
     * Règles métier appliquées dans l'ordre :
     * <ul>
     *   <li>L'expéditeur ne peut pas s'envoyer de l'argent à lui-même</li>
     *   <li>Le montant doit être strictement positif</li>
     *   <li>Le solde de l'expéditeur doit être suffisant</li>
     *   <li>Le destinataire doit faire partie des contacts de l'expéditeur</li>
     * </ul>
     * L'annotation {@code @Transactional} garantit l'atomicité de l'opération :
     * en cas d'échec, toutes les modifications sont annulées (rollback automatique).
     * </p>
     *
     * @param sender        l'utilisateur expéditeur, avec ses connexions déjà chargées
     * @param receiverEmail l'adresse email du destinataire
     * @param amount        le montant à transférer, strictement positif
     * @param description   la description du virement (peut être {@code null})
     * @return la transaction persistée
     * @throws IllegalArgumentException      si une règle métier est violée
     * @throws InsufficientBalanceException  si le solde de l'expéditeur est insuffisant
     */
    @Transactional
    public Transaction transfer(User sender, String receiverEmail, BigDecimal amount, String description) {
        if (sender.getEmail().equals(receiverEmail)) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous envoyer de l'argent.");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à 0.");
        }

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Solde insuffisant pour effectuer ce virement.");
        }

        boolean isConnected = sender.getConnections().stream()
            .anyMatch(u -> u.getEmail().equals(receiverEmail));
        if (!isConnected) {
            throw new IllegalArgumentException("Vous ne pouvez envoyer de l'argent qu'à vos contacts.");
        }

        User receiver = userRepository.findByEmail(receiverEmail)
            .orElseThrow(() -> new IllegalArgumentException("Destinataire introuvable."));

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        userRepository.save(sender);
        userRepository.save(receiver);

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setDescription(description);

        return transactionRepository.save(transaction);
    }

    /**
     * Récupère toutes les transactions d'un utilisateur (envoyées et reçues),
     * triées par date décroissante.
     *
     * @param user l'utilisateur dont on souhaite récupérer les transactions
     * @return la liste des transactions, vide si aucune
     */
    public List<Transaction> getTransactions(User user) {
        return transactionRepository.findAllByUserWithDetails(user);
    }
}