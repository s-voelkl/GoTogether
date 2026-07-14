package com.gotogether.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Challenge entity class representing a challenge in the system.
 * <p>
 * A challenge is an event or activity hosted by a {@link Company}.
 * It includes details like title, description, location, timing,
 * rewards (currency and experience points), and participant limits.
 * Participants are represented by a list of {@link User}s.
 */
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "challenge_topics", joinColumns = @JoinColumn(name = "challenge_id"), inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private List<Topic> topics = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "host_company_id", nullable = false)
    private Company host;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "challenge_users", joinColumns = @JoinColumn(name = "challenge_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users = new ArrayList<>();

}