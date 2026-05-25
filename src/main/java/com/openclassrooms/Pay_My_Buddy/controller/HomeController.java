package com.openclassrooms.Pay_My_Buddy.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.openclassrooms.Pay_My_Buddy.dto.TransactionDTO;
import com.openclassrooms.Pay_My_Buddy.mapper.Mapper;
import com.openclassrooms.Pay_My_Buddy.model.User;
import com.openclassrooms.Pay_My_Buddy.service.CustomUserDetailsService;
import com.openclassrooms.Pay_My_Buddy.service.TransactionService;
import com.openclassrooms.Pay_My_Buddy.service.UserService;

/**
 * Contrôleur MVC gérant la page d'accueil et le profil utilisateur.
 * <p>
 * Toutes les données sont injectées dans le modèle Thymeleaf côté serveur.
 * </p>
 */
@Controller
public class HomeController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Construit le contrôleur avec les services requis.
     *
     * @param userService        service métier de gestion des utilisateurs
     * @param transactionService service métier de gestion des transactions
     * @param userDetailsService service de chargement des détails utilisateur Spring Security
     */
    public HomeController(UserService userService, TransactionService transactionService, CustomUserDetailsService userDetailsService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Redirige la racine de l'application vers la page d'accueil.
     * @return une redirection vers {@code /home}
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    /**
     * Affiche la page d'accueil avec le profil et l'historique des transactions
     * de l'utilisateur connecté.
     *
     * @param principal l'utilisateur authentifié fourni par Spring Security
     * @param model     le modèle Thymeleaf dans lequel injecter les données
     * @return le nom de la vue {@code home}
     */
    @GetMapping("/home")
    public String homePage(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());

        List<TransactionDTO> transactions = transactionService.getTransactions(user)
                .stream()
                .map(Mapper::toTransactionDTO)
                .toList();

        model.addAttribute("user", Mapper.toUserDTO(user));
        model.addAttribute("transactions", transactions);
        return "home";
    }

    /**
     * Affiche la page de profil de l'utilisateur connecté.
     *
     * @param principal l'utilisateur authentifié fourni par Spring Security
     * @param model     le modèle Thymeleaf dans lequel injecter les données
     * @return le nom de la vue {@code profile}
     */
    @GetMapping("/profile")
    public String profilePage(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", Mapper.toUserDTO(user));
        return "profile";
    }

    /**
     * Traite la soumission du formulaire de modification du profil.
     * <p>
     * Si l'email est modifié, le contexte de sécurité Spring est rafraîchi
     * afin d'éviter une déconnexion automatique de l'utilisateur.
     * En cas d'erreur métier, le message est transmis via flash attribute.
     * </p>
     *
     * @param principal          l'utilisateur authentifié fourni par Spring Security
     * @param username           le nouveau nom d'utilisateur
     * @param email              le nouvel email
     * @param newPassword        le nouveau mot de passe (optionnel, ignoré si vide)
     * @param redirectAttributes les attributs flash pour transmettre les messages après redirection
     * @return une redirection vers {@code /profile}
     */
    @PostMapping("/profile")
    public String updateProfile(Principal principal,
                                @RequestParam String username,
                                @RequestParam String email,
                                @RequestParam(required = false) String newPassword,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.updateProfile(principal.getName(), username, email, newPassword);

            UserDetails updatedDetails = userDetailsService.loadUserByUsername(email);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedDetails, updatedDetails.getPassword(), updatedDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            redirectAttributes.addFlashAttribute("successMessage", "Profil mis à jour avec succès !");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile";
    }
}