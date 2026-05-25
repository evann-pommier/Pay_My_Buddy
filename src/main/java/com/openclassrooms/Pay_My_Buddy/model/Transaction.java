package com.openclassrooms.Pay_My_Buddy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Entité JPA représentant une transaction financière entre deux utilisateurs.
 * <p>
 * Une transaction enregistre le transfert d'un montant d'un expéditeur vers un destinataire,
 * avec une description optionnelle et une date de création automatique.
 * </p>
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"sender", "receiver"})
@Entity
@Table(name = "transactions")
public class Transaction {

    /**
     * Identifiant unique de la transaction, généré automatiquement par la base de données.
     */
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Utilisateur expéditeur du virement.
     * Chargé en mode lazy pour éviter les requêtes inutiles.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * Utilisateur destinataire du virement.
     * Chargé en mode lazy pour éviter les requêtes inutiles.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    /**
     * Description optionnelle du virement (255 caractères maximum).
     */
    @Column(length = 255)
    private String description;

    /**
     * Montant transféré, strictement positif.
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * Date et heure de création de la transaction, définie automatiquement à l'instanciation.
     * Non modifiable après persistance.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}