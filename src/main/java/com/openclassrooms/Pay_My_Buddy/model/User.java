package com.openclassrooms.Pay_My_Buddy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Entité JPA représentant un utilisateur de l'application.
 * <p>
 * Un utilisateur possède un solde, une liste de contacts et peut effectuer
 * des virements vers ses contacts.
 * </p>
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class User {

    /**
     * Identifiant unique de l'utilisateur, généré automatiquement par la base de données.
     */
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Nom d'utilisateur unique (100 caractères maximum).
     */
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    /**
     * Adresse email unique servant d'identifiant de connexion.
     */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * Mot de passe hashé avec BCrypt.
     * Exclu de la sérialisation JSON pour ne jamais être exposé.
     */
    @JsonIgnore
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Solde disponible du compte, initialisé à zéro à la création.
     */
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * Date et heure de création du compte, définie automatiquement à l'instanciation.
     * Non modifiable après persistance.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Liste des contacts de l'utilisateur.
     * Relation symétrique : si A est contact de B, alors B est contact de A.
     * Chargée en mode lazy et exclue de la sérialisation JSON pour éviter les cycles.
     */
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinTable(
        name = "user_connections",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "connection_id")
    )
    private List<User> connections = new ArrayList<>();
}