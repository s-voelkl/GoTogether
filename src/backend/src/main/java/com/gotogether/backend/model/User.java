package com.gotogether.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
// default values set via field initializers

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User entity class representing a user in the system.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private static final int DEFAULT_SOCIAL_BATTERY = 100;
    private static final int DEFAULT_CURRENCY = 0;
    private static final int DEFAULT_EXPERIENCE_POINTS = 0;

    public User(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.socialBattery = DEFAULT_SOCIAL_BATTERY;
        this.currency = DEFAULT_CURRENCY;
        this.experiencePoints = DEFAULT_EXPERIENCE_POINTS;
        this.interests = new ArrayList<>();
        this.lastLogin = LocalDateTime.now();
        this.settings = new Settings();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)

    private int socialBattery = DEFAULT_SOCIAL_BATTERY;

    @Column(nullable = false)
    private int currency = DEFAULT_CURRENCY;

    @Column(nullable = false)
    private int experiencePoints = DEFAULT_EXPERIENCE_POINTS;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private List<Topic> interests = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime lastLogin = LocalDateTime.now();

    @Embedded
    private Settings settings;
}
