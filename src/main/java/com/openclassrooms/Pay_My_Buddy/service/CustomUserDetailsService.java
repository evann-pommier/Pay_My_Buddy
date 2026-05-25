package com.openclassrooms.Pay_My_Buddy.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.openclassrooms.Pay_My_Buddy.model.User;
import com.openclassrooms.Pay_My_Buddy.repository.UserRepository;

/**
 * Implémentation de {@link UserDetailsService} pour l'intégration avec Spring Security.
 * <p>
 * Charge les détails d'un utilisateur depuis la base de données à partir de son adresse email,
 * utilisée comme identifiant de connexion.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Construit le service avec le repository utilisateur requis.
     *
     * @param userRepository le repository d'accès aux données des utilisateurs
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Charge un utilisateur par son adresse email.
     * <p>
     * Appelé automatiquement par Spring Security lors de la tentative de connexion.
     * L'email est utilisé comme identifiant ({@code usernameParameter("email")} dans {@code SecurityConfig}).
     * </p>
     *
     * @param email l'adresse email de l'utilisateur à charger
     * @return les détails de l'utilisateur sous forme de {@link UserDetails}
     * @throws UsernameNotFoundException si aucun utilisateur ne correspond à cet email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Utilisateur introuvable : " + email));

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .roles("USER")
            .build();
    }
}