package com.openclassrooms.Pay_My_Buddy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.openclassrooms.Pay_My_Buddy.model.Transaction;
import com.openclassrooms.Pay_My_Buddy.model.User;

/**
 * Repository JPA pour l'accès aux données des transactions financières.
 * <p>
 * Étend {@link JpaRepository} pour les opérations CRUD standard
 * et expose des requêtes métier spécifiques.
 * </p>
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    /**
     * Récupère toutes les transactions envoyées par un utilisateur.
     *
     * @param sender l'utilisateur expéditeur
     * @return la liste des transactions envoyées, vide si aucune
     */
    List<Transaction> findBySender(User sender);

    /**
     * Récupère toutes les transactions reçues par un utilisateur.
     *
     * @param receiver l'utilisateur destinataire
     * @return la liste des transactions reçues, vide si aucune
     */
    List<Transaction> findByReceiver(User receiver);

    /**
     * Récupère toutes les transactions d'un utilisateur (envoyées et reçues),
     * triées par date décroissante.
     * <p>
     * Utilise {@code JOIN FETCH} pour charger les entités {@code sender} et {@code receiver}
     * en une seule requête et éviter le problème N+1.
     * </p>
     *
     * @param user l'utilisateur dont on souhaite récupérer les transactions
     * @return la liste des transactions triées de la plus récente à la plus ancienne
     */
    @Query("""
            SELECT t FROM Transaction t
            JOIN FETCH t.sender
            JOIN FETCH t.receiver
            WHERE t.sender = :user OR t.receiver = :user
            ORDER BY t.createdAt DESC
        """)
    List<Transaction> findAllByUserWithDetails(@Param("user") User user);
}