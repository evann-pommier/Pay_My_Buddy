package com.openclassrooms.Pay_My_Buddy.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration Spring Security pour l'architecture MVC Thymeleaf.
 * <p>
 * La protection CSRF est activée par défaut — les formulaires Thymeleaf
 * incluent le token automatiquement via {@code th:action}.
 * Les ressources statiques et les pages publiques sont accessibles sans authentification.
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Définit la chaîne de filtres de sécurité HTTP.
     * <p>
     * Règles appliquées :
     * <ul>
     *   <li>Pages publiques : {@code /login}, {@code /register} et ressources statiques</li>
     *   <li>Toutes les autres routes nécessitent une authentification</li>
     *   <li>Connexion via formulaire avec l'email comme identifiant</li>
     *   <li>Déconnexion avec redirection vers {@code /login?logout}</li>
     * </ul>
     * </p>
     *
     * @param http le builder de configuration HTTP Spring Security
     * @return la chaîne de filtres configurée
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> {})
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        return http.build();
    }

    /**
     * Déclare le bean d'encodage des mots de passe utilisant l'algorithme BCrypt.
     *
     * @return une instance de {@link BCryptPasswordEncoder}
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}