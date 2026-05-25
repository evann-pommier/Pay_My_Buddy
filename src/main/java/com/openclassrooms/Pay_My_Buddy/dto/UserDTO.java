package com.openclassrooms.Pay_My_Buddy.dto;

import java.math.BigDecimal;

/**
 * DTO de représentation d'un utilisateur.
 * <p>
 * Utilisé pour transmettre les données d'un utilisateur vers la couche présentation
 * sans exposer l'entité JPA ni le mot de passe.
 * </p>
 *
 * @param id       l'identifiant unique de l'utilisateur
 * @param username le nom d'utilisateur
 * @param email    l'adresse email
 * @param balance  le solde du compte
 */
public record UserDTO(
    Integer id,
    String username,
    String email,
    BigDecimal balance
) {}