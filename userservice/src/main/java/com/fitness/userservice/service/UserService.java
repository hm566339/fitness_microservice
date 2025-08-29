// package com.fitness.userservice.service;

// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;

// import com.fitness.userservice.dto.RegisterRequest;
// import com.fitness.userservice.dto.UserResponse;
// import com.fitness.userservice.model.User;
// import com.fitness.userservice.repository.UserRepository;

// import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Service
// @AllArgsConstructor
// @Slf4j
// public class UserService {

//     private final UserRepository userRepository;

//     public UserResponse register(RegisterRequest request) {

//         if (userRepository.existsByEmail(request.getEmail())) {
//             User existingUser = userRepository.findByEmail(request.getEmail());
//             UserResponse userResponse = new UserResponse();
//             userResponse.setId(existingUser.getId());
//             userResponse.setKeycloakId(existingUser.getKeycloakId());
//             // userResponse.setFirstname(existingUser.getFirstname());
//             userResponse.setLastname(existingUser.getLastname());
//             userResponse.setEmail(existingUser.getEmail());
//             userResponse.setPassword(existingUser.getPassword());
//             userResponse.setCreatedAt(existingUser.getCreatedAt());
//             userResponse.setUpdatedAt(existingUser.getUpdatedAt());
//             return userResponse;
//         }

//         User user = new User();
//         user.setFirstname(request.getFirstname());
//         user.setLastname(request.getLastname());
//         user.setKeycloakId(request.getKeycloakId());
//         user.setEmail(request.getEmail());
//         user.setPassword(request.getPassword());

//         User saveUser = userRepository.save(user);
//         UserResponse userResponse = new UserResponse();
//         userResponse.setId(saveUser.getId());
//         userResponse.setFirstname(saveUser.getFirstname());
//         userResponse.setLastname(saveUser.getLastname());
//         userResponse.setKeycloakId(saveUser.getKeycloakId());
//         userResponse.setEmail(saveUser.getEmail());
//         userResponse.setPassword(saveUser.getPassword());
//         userResponse.setCreatedAt(saveUser.getCreatedAt());
//         userResponse.setUpdatedAt(saveUser.getUpdatedAt());
//         return userResponse;
//     }

//     public UserResponse getUserProfile(String userId) {
//         User user = userRepository.findById(userId)
//                 .orElseThrow(() -> new IllegalStateException("User not found with email: " + userId));

//         UserResponse userResponse = new UserResponse();
//         userResponse.setId(user.getId());
//         userResponse.setFirstname(user.getFirstname());
//         userResponse.setLastname(user.getLastname());
//         userResponse.setEmail(user.getEmail());
//         userResponse.setPassword(user.getPassword());
//         userResponse.setCreatedAt(user.getCreatedAt());
//         userResponse.setUpdatedAt(user.getUpdatedAt());
//         return userResponse;
//     }

//     public ResponseEntity<String> getAllUsers() {
//         return userRepository.findAll().toString().isEmpty() ? ResponseEntity.ok("No users found")
//                 : ResponseEntity.ok(userRepository.findAll().toString());
//     }

//     public Boolean exitByUser(String userID) {
//         log.info("calling user service to validate userID: {}", userID);
//         return userRepository.existsByKeycloakId(userID);
//     }

// }

package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository repository;

    public UserResponse register(RegisterRequest request) {

        if (repository.existsByEmail(request.getEmail())) {
            User existingUser = repository.findByEmail(request.getEmail());
            UserResponse userResponse = new UserResponse();
            userResponse.setId(existingUser.getId());
            userResponse.setKeycloakId(existingUser.getKeycloakId());
            userResponse.setPassword(existingUser.getPassword());
            userResponse.setEmail(existingUser.getEmail());
            userResponse.setFirstName(existingUser.getFirstName());
            userResponse.setLastName(existingUser.getLastName());
            userResponse.setCreatedAt(existingUser.getCreatedAt());
            userResponse.setUpdatedAt(existingUser.getUpdatedAt());
            return userResponse;
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setKeycloakId(request.getKeycloakId());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = repository.save(user);
        UserResponse userResponse = new UserResponse();
        userResponse.setKeycloakId(savedUser.getKeycloakId());
        userResponse.setId(savedUser.getId());
        userResponse.setPassword(savedUser.getPassword());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());
        userResponse.setCreatedAt(savedUser.getCreatedAt());
        userResponse.setUpdatedAt(savedUser.getUpdatedAt());

        return userResponse;
    }

    public UserResponse getUserProfile(String userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setPassword(user.getPassword());
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());

        return userResponse;
    }

    public Boolean existByUserId(String userId) {
        log.info("Calling User Validation API for userId: {}", userId);
        return repository.existsByKeycloakId(userId);
    }
}