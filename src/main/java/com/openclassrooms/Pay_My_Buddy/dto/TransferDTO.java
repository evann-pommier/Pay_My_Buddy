package com.openclassrooms.Pay_My_Buddy.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransferDTO(

    @NotBlank(message = "L'email du destinataire est obligatoire")
    @Email(message = "Format d'email invalide")
    String receiverEmail,

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    BigDecimal amount,

    String description
) {}