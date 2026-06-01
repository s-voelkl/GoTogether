package com.gotogether.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "challenges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean isArchived = false;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Embedded
    private Location location;

    @Column(nullable = false)
    private int durationMinutes;

    @Column(nullable = false)
    private int currency;

    @Column(nullable = false)
    private int experiencePoints;

    @Column(nullable = false)
    private int minSocialBattery = 0;

    @Column(nullable = false, length = 5)
    private String verificationCode; // 5 digits

    @Column(nullable = false)
    private int maxPlayers = 0; // 0 means no limit

    // TODO: topics: join table n:m mit topics -> challenge_topics mit challenge_id
    // und topic_id

    // TODO: host: durch Join Table host 1:n challenge -> challenge_host mit
    // challenge_id und company_id

    // TODO: users: Join Table n user : m challenges -> challenge_users mit
    // challenge_id und user_id

}