package com.openclassrooms.Pay_My_Buddy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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
    List<Transaction> findBySenderOrReceiverOrderByCreatedAtDesc(User sender, User receiver);
}