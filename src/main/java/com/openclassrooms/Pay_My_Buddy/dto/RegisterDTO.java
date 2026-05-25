package com.openclassrooms.Pay_My_Buddy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de transfert des données du formulaire d'inscription.
 * <p>
 * Utilisé lors de la soumission du formulaire {@code /register}.
 * Les contraintes Bean Validation sont évaluées avant l'appel au service.
 * </p>
 *
 * @param username le nom d'utilisateur (obligatoire, unique)
 * @param email    l'adresse email (obligatoire, format valide, unique)
 * @param password le mot de passe en clair (obligatoire, minimum 8 caractères)
 */
public record RegisterDTO(

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    String username,

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    String email,

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    String password

) {}