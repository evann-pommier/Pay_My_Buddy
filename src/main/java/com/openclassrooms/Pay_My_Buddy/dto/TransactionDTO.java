package com.openclassrooms.Pay_My_Buddy.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de représentation d'une transaction financière.
 * <p>
 * Utilisé pour transmettre les données d'une transaction vers la couche présentation
 * sans exposer l'entité JPA.
 * </p>
 *
 * @param id            l'identifiant unique de la transaction
 * @param senderEmail   l'email de l'expéditeur
 * @param receiverEmail l'email du destinataire
 * @param description   la description du virement (peut être {@code null})
 * @param amount        le montant transféré
 * @param createdAt     la date et l'heure de création de la transaction
 */
public record TransactionDTO(
    Integer id,
    String senderEmail,
    String receiverEmail,
    String description,
    BigDecimal amount,
    LocalDateTime createdAt
) {}