package com.openclassrooms.Pay_My_Buddy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.openclassrooms.Pay_My_Buddy.model.Transaction;
import com.openclassrooms.Pay_My_Buddy.model.User;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    /**
     * Récupère toutes les transactions envoyées par un utilisateur.
     */
    List<Transaction> findBySender(User sender);

    /**
     * Récupère toutes les transactions reçues par un utilisateur.
     */
    List<Transaction> findByReceiver(User receiver);

    /**
     * Récupère toutes les transactions d'un utilisateur
     * (envoyées ET reçues), triées par date décroissante.
     */
    @Query("""
    	    SELECT t FROM Transaction t
    	    JOIN FETCH t.sender
    	    JOIN FETCH t.receiver
    	    WHERE t.sender = :user OR t.receiver = :user
    	    ORDER BY t.createdAt DESC
    	""")
    	List<Transaction> findAllByUserWithDetails(
    	    @Param("user") User user
    	);
}