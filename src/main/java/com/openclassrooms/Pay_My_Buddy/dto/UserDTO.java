package com.openclassrooms.Pay_My_Buddy.dto;

import java.math.BigDecimal;

public record UserDTO(
    Integer id,
    String username,
    String email,
    BigDecimal balance
) {}