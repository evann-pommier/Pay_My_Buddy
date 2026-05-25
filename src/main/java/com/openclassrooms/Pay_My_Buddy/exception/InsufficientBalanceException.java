package com.openclassrooms.Pay_My_Buddy.exception;

/**
 * Exception levée lorsqu'un utilisateur tente d'effectuer un virement
 * dont le montant dépasse son solde disponible.
 */
public class InsufficientBalanceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construit l'exception avec un message décrivant la cause.
     *
     * @param message le message détaillant la raison de l'échec
     */
    public InsufficientBalanceException(String message) {
        super(message);
    }
}