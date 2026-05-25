package com.openclassrooms.Pay_My_Buddy.mapper;

import com.openclassrooms.Pay_My_Buddy.dto.TransactionDTO;
import com.openclassrooms.Pay_My_Buddy.dto.UserDTO;
import com.openclassrooms.Pay_My_Buddy.model.Transaction;
import com.openclassrooms.Pay_My_Buddy.model.User;

/**
 * Classe utilitaire de conversion entre entités JPA et DTOs.
 * <p>
 * Non instanciable — tous les méthodes sont statiques.
 * </p>
 */
public class Mapper {

    private Mapper() {}

    /**
     * Convertit une entité {@link User} en {@link UserDTO}.
     *
     * @param user l'entité utilisateur à convertir
     * @return le DTO correspondant
     */
    public static UserDTO toUserDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getBalance()
        );
    }

    /**
     * Convertit une entité {@link Transaction} en {@link TransactionDTO}.
     *
     * @param transaction l'entité transaction à convertir
     * @return le DTO correspondant
     */
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