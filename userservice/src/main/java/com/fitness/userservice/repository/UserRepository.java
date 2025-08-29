// package com.fitness.userservice.repository;

// import java.util.Optional;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// import com.fitness.userservice.model.User;

// @Repository
// public interface UserRepository extends JpaRepository<User, String> {

//     User findByEmail(String email);

//     boolean existsByEmail(String email);

//     Boolean existsByKeycloakId(String keycloakId);

// }

package com.fitness.userservice.repository;

import com.fitness.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

    Boolean existsByKeycloakId(String userId);

    User findByEmail(String email);
}