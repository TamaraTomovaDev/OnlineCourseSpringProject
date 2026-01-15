package org.intecbrussel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CourseRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;
}


