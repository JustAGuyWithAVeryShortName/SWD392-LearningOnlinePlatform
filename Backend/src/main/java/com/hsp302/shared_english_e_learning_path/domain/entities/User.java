package com.hsp302.shared_english_e_learning_path.domain.entities;

import com.hsp302.shared_english_e_learning_path.domain.enums.AgeGroup;
import com.hsp302.shared_english_e_learning_path.domain.enums.Gender;
import com.hsp302.shared_english_e_learning_path.domain.enums.Role;
import com.hsp302.shared_english_e_learning_path.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    String username;
    String password;
    String email;
    String fullName;
    LocalDate dob;
    @Enumerated(EnumType.STRING)
    Gender gender;
    String phoneNumber;
    String job;
    String address;
    Instant createdAt;
    Instant updatedAt;
    @Enumerated(EnumType.STRING)
    Role role;
    @Enumerated(EnumType.STRING)
    UserStatus status;
    @Enumerated(EnumType.STRING)
    AgeGroup ageGroup;

    @OneToMany(mappedBy = "consultant")
    List<Qualification> qualifications = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    List<Appointment> memberAppointments = new ArrayList<>();

    @OneToMany(mappedBy = "consultant")
    List<Appointment> consultantAppointments = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    List<Blog> blogs = new ArrayList<>();

    @OneToMany(mappedBy = "consultant")
    List<Availability> consultantAvailabilities = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
