package com.fitness.activityservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidateService {
    private final WebClient userServiceWebClient;

    public Boolean validateUser(String userID) {
        log.info("calling user service to validate userID: {}", userID);
        try {
            return userServiceWebClient.get()
                    .uri("/api/users/{userID}/validate", userID)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        } catch (WebClientResponseException e) {
            e.printStackTrace();
        }
        return false;
    }

}
