package com.fitness.activityservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidateService userValidateService;
    private final KafkaTemplate<String, Activity> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topicname;

    public ActivityResponse trackActivity(ActivityRequest request) {
        Boolean isValidUser = userValidateService.validateUser(request.getUserId());
        if (!isValidUser) {
            throw new IllegalArgumentException("Invalid user ID: " + request.getUserId());
        }

        Activity activity = new Activity().builder()
                .userId(request.getUserId())
                .activityType(request.getActivityType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .timestamp(request.getTimestamp())
                .additionalData(request.getAdditionalData())
                .build();

        Activity saveActivity = activityRepository.save(activity);

        try {
            kafkaTemplate.send(topicname, saveActivity.getUserId(), saveActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mapToResponse(saveActivity);
    }

    private ActivityResponse mapToResponse(Activity activity) {
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setActivityType(activity.getActivityType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setTimestamp(activity.getTimestamp());
        response.setAdditionalData(activity.getAdditionalData());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());
        return response;
    }

}
