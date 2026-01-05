package org.intecbrussel.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private String instructorUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
