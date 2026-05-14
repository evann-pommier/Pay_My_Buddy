package com.openclassrooms.Pay_My_Buddy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddConnectionDTO(

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    String friendEmail
) {}