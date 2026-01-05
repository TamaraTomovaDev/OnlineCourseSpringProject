package org.intecbrussel.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrollmentResponse {
    private Long id;
    private String studentUsername;
    private String courseTitle;
    private LocalDateTime enrollmentDate;
}
