package com.openclassrooms.Pay_My_Buddy.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.openclassrooms.Pay_My_Buddy.dto.RegisterDTO;
import com.openclassrooms.Pay_My_Buddy.service.UserService;

import jakarta.validation.Valid;

/**
 * Contrôleur MVC pour les pages d'authentification (login / register).
 * Remplace l'ancien AuthController REST + PageController.
 */
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /* ------------------------------------------------------------------ */
    /*  Login                                                               */
    /* ------------------------------------------------------------------ */

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /* ------------------------------------------------------------------ */
    /*  Register                                                            */
    /* @param model     le modèle Thymeleaf dans lequel injecter les données*/
    /* ------------------------------------------------------------------ */

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO("", "", ""));
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid RegisterDTO registerDTO,BindingResult result,Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.register(registerDTO.username(), registerDTO.email(), registerDTO.password());
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }
}