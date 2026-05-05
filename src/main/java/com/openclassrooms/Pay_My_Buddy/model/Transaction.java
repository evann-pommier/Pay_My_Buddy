package com.openclassrooms.Pay_My_Buddy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Utilisateur qui envoie l'argent.
     * LAZY = l'objet User n'est chargé que si on y accède.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * Utilisateur qui reçoit l'argent.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * Date et heure de la transaction.
     * Remplie automatiquement à la création.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private BigDecimal fee = BigDecimal.ZERO; // 0,5% prévu en V1
}