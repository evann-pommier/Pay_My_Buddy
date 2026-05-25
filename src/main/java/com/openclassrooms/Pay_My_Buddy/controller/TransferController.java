package com.openclassrooms.Pay_My_Buddy.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.openclassrooms.Pay_My_Buddy.dto.TransactionDTO;
import com.openclassrooms.Pay_My_Buddy.dto.UserDTO;
import com.openclassrooms.Pay_My_Buddy.mapper.Mapper;
import com.openclassrooms.Pay_My_Buddy.model.User;
import com.openclassrooms.Pay_My_Buddy.service.TransactionService;
import com.openclassrooms.Pay_My_Buddy.service.UserService;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Contrôleur MVC gérant les pages de transfert d'argent et d'ajout de connexion.
 * <p>
 * Toutes les données sont injectées dans le modèle Thymeleaf côté serveur.
 * {@code @Validated} active la validation des paramètres de requête ({@code @RequestParam})
 * via Bean Validation.
 * </p>
 */
@Controller
@Validated
public class TransferController {

    private final UserService userService;
    private final TransactionService transactionService;

    /**
     * Construit le contrôleur avec les services requis.
     *
     * @param userService        service métier de gestion des utilisateurs
     * @param transactionService service métier de gestion des transactions
     */
    public TransferController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    /**
     * Affiche la page de transfert avec la liste des contacts et l'historique
     * des transactions de l'utilisateur connecté.
     *
     * @param principal l'utilisateur authentifié fourni par Spring Security
     * @param model     le modèle Thymeleaf dans lequel injecter les données
     * @return le nom de la vue {@code transfer}
     */
    @GetMapping("/transfer")
    public String transferPage(Principal principal, Model model) {
        User user = userService.findByEmailWithConnections(principal.getName());

        List<UserDTO> connections = user.getConnections()
                .stream()
                .map(Mapper::toUserDTO)
                .toList();

        List<TransactionDTO> transactions = transactionService.getTransactions(user)
                .stream()
                .map(Mapper::toTransactionDTO)
                .toList();

        model.addAttribute("user", Mapper.toUserDTO(user));
        model.addAttribute("connections", connections);
        model.addAttribute("transactions", transactions);
        return "transfer";
    }

    /**
     * Traite la soumission du formulaire de virement.
     * <p>
     * En cas de succès, un message de confirmation est transmis via flash attribute.
     * En cas d'erreur (solde insuffisant, destinataire non contact, etc.),
     * le message d'erreur est transmis via flash attribute.
     * </p>
     *
     * @param principal          l'utilisateur authentifié fourni par Spring Security
     * @param receiverEmail      l'email du destinataire du virement (obligatoire, format valide)
     * @param amount             le montant à transférer (obligatoire, supérieur à 0)
     * @param description        la description du virement (optionnelle)
     * @param redirectAttributes les attributs flash pour transmettre les messages après redirection
     * @return une redirection vers {@code /transfer}
     */
    @PostMapping("/transfer")
    public String transfer(Principal principal,
                           @RequestParam @NotBlank @Email String receiverEmail,
                           @RequestParam @NotNull @DecimalMin("0.01") BigDecimal amount,
                           @RequestParam(required = false) String description,
                           RedirectAttributes redirectAttributes) {
        try {
            User sender = userService.findByEmailWithConnections(principal.getName());
            transactionService.transfer(sender, receiverEmail, amount, description);
            redirectAttributes.addFlashAttribute("successMessage", "Virement effectué avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/transfer";
    }

    /**
     * Affiche la page d'ajout de connexion.
     *
     * @return le nom de la vue {@code add-connection}
     */
    @GetMapping("/add-connection")
    public String addConnectionPage() {
        return "add-connection";
    }

    /**
     * Traite la soumission du formulaire d'ajout d'un contact.
     * <p>
     * En cas de succès, un message de confirmation est transmis via flash attribute.
     * En cas d'erreur (doublon, auto-connexion, utilisateur introuvable),
     * le message d'erreur est transmis via flash attribute.
     * </p>
     *
     * @param principal          l'utilisateur authentifié fourni par Spring Security
     * @param friendEmail        l'email de l'utilisateur à ajouter comme contact (obligatoire, format valide)
     * @param redirectAttributes les attributs flash pour transmettre les messages après redirection
     * @return une redirection vers {@code /add-connection}
     */
    @PostMapping("/add-connection")
    public String addConnection(Principal principal,
                                @RequestParam @NotBlank @Email String friendEmail,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmailWithConnections(principal.getName());
            userService.addConnection(user, friendEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Relation ajoutée avec succès !");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/add-connection";
    }
}