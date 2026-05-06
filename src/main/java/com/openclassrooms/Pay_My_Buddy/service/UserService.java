package com.openclassrooms.Pay_My_Buddy.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.Pay_My_Buddy.model.User;
import com.openclassrooms.Pay_My_Buddy.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Inscription d'un nouvel utilisateur.
     * Le mot de passe est hashé avant d'être stocké.
     */
    @Transactional
    public User register(String username, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    /**
     * Recherche un utilisateur par email.
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + email));
    }

    /**
     * Ajoute un ami à la liste de connexions de l'utilisateur.
     */
    @Transactional
    public void addConnection(User user, String friendEmail) {
        if (user.getEmail().equals(friendEmail)) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous ajouter vous-même.");
        }

        User friend = findByEmail(friendEmail);

        if (user.getConnections().contains(friend)) {
            throw new IllegalArgumentException("Cet utilisateur est déjà dans vos connexions.");
        }

        user.getConnections().add(friend);
        userRepository.save(user);
    }
}