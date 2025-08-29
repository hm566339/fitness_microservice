package com.fitness.aiservice.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document(collection = "recommendations")
@Data
@Builder
public class Recommendation {
    private String id;
    private String activityId;
    private String userId;
    private String type;
    private String recommendationText;
    private List<String> improvments;
    private List<String> suggestions;
    private List<String> safety;

    @CreatedDate
    private LocalDateTime createdAt;

}
