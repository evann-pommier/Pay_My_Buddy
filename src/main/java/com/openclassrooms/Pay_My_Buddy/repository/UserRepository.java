package com.openclassrooms.Pay_My_Buddy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.openclassrooms.Pay_My_Buddy.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Recherche un utilisateur par son email.
     * Utilisé lors de la connexion et de l'ajout d'ami.
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un email existe déjà en BDD.
     * Utilisé lors de l'inscription.
     */
    boolean existsByEmail(String email);
}