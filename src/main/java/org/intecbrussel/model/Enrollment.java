package org.intecbrussel.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"})
)
public class Enrollment extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private LocalDateTime enrollmentDate;

    @PrePersist
    void onCreate() {
        if (enrollmentDate == null) {
            enrollmentDate = LocalDateTime.now();
        }
    }
}
