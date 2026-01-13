package org.intecbrussel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CourseRequest {

    @NotBlank
    private String title;

    private String description;
}

