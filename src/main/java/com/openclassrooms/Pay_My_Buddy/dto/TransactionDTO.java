package com.openclassrooms.Pay_My_Buddy.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDTO(
    Integer id,
    String senderEmail,
    String receiverEmail,
    String description,
    BigDecimal amount,
    LocalDateTime createdAt
) {}