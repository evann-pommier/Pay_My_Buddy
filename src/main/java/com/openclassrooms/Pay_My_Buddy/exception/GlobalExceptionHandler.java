package com.openclassrooms.Pay_My_Buddy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

/**
 * Gestionnaire global des exceptions pour tous les contrôleurs MVC.
 * <p>
 * Intercepte les exceptions non gérées localement et retourne
 * la vue {@code error} avec le code et le message appropriés.
 * </p>
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Gère les erreurs métier génériques (email déjà utilisé, destinataire introuvable, etc.).
     *
     * @param e     l'exception levée
     * @param model le modèle Thymeleaf dans lequel injecter les données d'erreur
     * @return le nom de la vue {@code error}
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException e, Model model) {
        log.error("Invalid argument: {}", e.getMessage());
        model.addAttribute("errorCode", "400");
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

    /**
     * Gère les violations de contraintes Bean Validation sur les {@code @RequestParam}
     * des contrôleurs annotés avec {@code @Validated}.
     *
     * @param e     l'exception levée
     * @param model le modèle Thymeleaf dans lequel injecter les données d'erreur
     * @return le nom de la vue {@code error}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolation(ConstraintViolationException e, Model model) {
        log.error("Constraint violation: {}", e.getMessage());
        model.addAttribute("errorCode", "400");
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

    /**
     * Gère les cas de solde insuffisant lors d'un virement.
     *
     * @param e     l'exception levée
     * @param model le modèle Thymeleaf dans lequel injecter les données d'erreur
     * @return le nom de la vue {@code error}
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleInsufficientBalance(InsufficientBalanceException e, Model model) {
        log.error("Insufficient balance: {}", e.getMessage());
        model.addAttribute("errorCode", "422");
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

    /**
     * Gère toutes les exceptions non interceptées par les handlers spécifiques.
     *
     * @param e     l'exception levée
     * @param model le modèle Thymeleaf dans lequel injecter les données d'erreur
     * @return le nom de la vue {@code error}
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneric(Exception e, Model model) {
        log.error("Unexpected error: {}", e.getMessage());
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorMessage", "Une erreur inattendue est survenue.");
        return "error";
    }
}