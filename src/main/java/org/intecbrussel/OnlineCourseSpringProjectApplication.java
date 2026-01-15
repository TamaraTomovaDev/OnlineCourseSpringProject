package org.intecbrussel;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableJpaAuditing
public class OnlineCourseSpringProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineCourseSpringProjectApplication.class, args);
    }
}
