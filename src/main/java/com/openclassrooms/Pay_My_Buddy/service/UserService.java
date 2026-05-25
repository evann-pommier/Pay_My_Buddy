package com.openclassrooms.Pay_My_Buddy.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.Pay_My_Buddy.model.User;
import com.openclassrooms.Pay_My_Buddy.repository.UserRepository;

/**
 * Service métier gérant les opérations sur les utilisateurs.
 * <p>
 * Couvre l'inscription, la mise à jour du profil, la recherche
 * et la gestion des connexions entre utilisateurs.
 * </p>
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Construit le service avec les dépendances requises.
     *
     * @param userRepository  le repository d'accès aux données des utilisateurs
     * @param passwordEncoder l'encodeur de mot de passe BCrypt
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Inscrit un nouvel utilisateur.
     * <p>
     * Vérifie l'unicité de l'email et du username avant la création.
     * Le mot de passe est hashé avec BCrypt avant d'être persisté.
     * </p>
     *
     * @param username le nom d'utilisateur souhaité
     * @param email    l'adresse email de l'utilisateur
     * @param password le mot de passe en clair, sera encodé avant persistance
     * @return l'utilisateur créé et persisté
     * @throws IllegalArgumentException si l'email ou le username est déjà utilisé
     */
    @Transactional
    public User register(String username, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris.");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    /**
     * Met à jour le profil d'un utilisateur.
     * <p>
     * Vérifie l'unicité du nouvel email et du nouveau username si ceux-ci ont changé.
     * Le mot de passe n'est mis à jour que s'il est renseigné et non vide.
     * </p>
     *
     * @param currentEmail l'email actuel de l'utilisateur, utilisé pour le retrouver
     * @param newUsername  le nouveau nom d'utilisateur
     * @param newEmail     le nouvel email
     * @param newPassword  le nouveau mot de passe en clair (ignoré si {@code null} ou vide)
     * @return l'utilisateur mis à jour et persisté
     * @throws IllegalArgumentException si le nouvel email ou username est déjà pris,
     *                                  ou si le nouveau mot de passe est trop court
     */
    @Transactional
    public User updateProfile(String currentEmail, String newUsername, String newEmail, String newPassword) {
        User user = findByEmail(currentEmail);

        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        if (!user.getUsername().equals(newUsername) && userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris.");
        }

        user.setUsername(newUsername);
        user.setEmail(newEmail);

        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.length() < 8) {
                throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caractères.");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(user);
    }

    /**
     * Recherche un utilisateur par son adresse email.
     *
     * @param email l'adresse email à rechercher
     * @return l'utilisateur correspondant
     * @throws IllegalArgumentException si aucun utilisateur ne correspond à cet email
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + email));
    }

    /**
     * Recherche un utilisateur par son adresse email en chargeant ses connexions.
     *
     * @param email l'adresse email à rechercher
     * @return l'utilisateur avec sa liste de connexions chargée
     * @throws IllegalArgumentException si aucun utilisateur ne correspond à cet email
     */
    public User findByEmailWithConnections(String email) {
        return userRepository.findByEmailWithConnections(email)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + email));
    }

    /**
     * Ajoute un contact dans la liste de connexions de l'utilisateur.
     * <p>
     * La relation est symétrique : si A ajoute B, B est également ajouté aux contacts de A.
     * </p>
     *
     * @param user        l'utilisateur souhaitant ajouter un contact, avec ses connexions chargées
     * @param friendEmail l'adresse email de l'utilisateur à ajouter
     * @throws IllegalArgumentException si l'utilisateur tente de s'ajouter lui-même
     *                                  ou si la connexion existe déjà
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
        friend.getConnections().add(user);

        userRepository.save(user);
        userRepository.save(friend);
    }
}