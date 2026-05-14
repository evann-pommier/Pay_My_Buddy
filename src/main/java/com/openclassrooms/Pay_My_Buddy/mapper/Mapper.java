package com.openclassrooms.Pay_My_Buddy.mapper;

import com.openclassrooms.Pay_My_Buddy.dto.TransactionDTO;
import com.openclassrooms.Pay_My_Buddy.dto.UserDTO;
import com.openclassrooms.Pay_My_Buddy.model.Transaction;
import com.openclassrooms.Pay_My_Buddy.model.User;

public class Mapper {

    private Mapper() {}

    public static UserDTO toUserDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getBalance()
        );
    }

    public static TransactionDTO toTransactionDTO(Transaction transaction) {
        return new TransactionDTO(
            transaction.getId(),
            transaction.getSender().getEmail(),
            transaction.getReceiver().getEmail(),
            transaction.getDescription(),
            transaction.getAmount(),
            transaction.getCreatedAt()
        );
    }
}