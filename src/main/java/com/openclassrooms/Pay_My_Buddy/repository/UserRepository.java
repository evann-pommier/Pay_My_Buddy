package com.openclassrooms.Pay_My_Buddy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.openclassrooms.Pay_My_Buddy.model.User;

/**
 * Repository JPA pour l'accès aux données des utilisateurs.
 * <p>
 * Étend {@link JpaRepository} pour les opérations CRUD standard
 * et expose des requêtes métier spécifiques.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Recherche un utilisateur par son adresse email.
     *
     * @param email l'adresse email à rechercher
     * @return un {@link Optional} contenant l'utilisateur si trouvé, vide sinon
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si une adresse email est déjà utilisée.
     *
     * @param email l'adresse email à vérifier
     * @return {@code true} si l'email existe déjà, {@code false} sinon
     */
    boolean existsByEmail(String email);

    /**
     * Vérifie si un nom d'utilisateur est déjà utilisé.
     *
     * @param username le nom d'utilisateur à vérifier
     * @return {@code true} si le username existe déjà, {@code false} sinon
     */
    boolean existsByUsername(String username);

    /**
     * Recherche un utilisateur par son adresse email en chargeant
     * ses connexions en une seule requête.
     * <p>
     * Utilise {@code LEFT JOIN FETCH} pour éviter le problème N+1
     * lors de l'accès à la liste des contacts.
     * </p>
     *
     * @param email l'adresse email à rechercher
     * @return un {@link Optional} contenant l'utilisateur avec ses connexions si trouvé, vide sinon
     */
    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.connections
            WHERE u.email = :email
        """)
    Optional<User> findByEmailWithConnections(@Param("email") String email);
}