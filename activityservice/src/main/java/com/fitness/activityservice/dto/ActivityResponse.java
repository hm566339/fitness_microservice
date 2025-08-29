package com.fitness.activityservice.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fitness.activityservice.helper.ActivityType;

import lombok.Data;

@Data
public class ActivityResponse {

    private String id;
    private String userId;
    private ActivityType activityType;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime timestamp;
    private Map<String, Object> additionalData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
