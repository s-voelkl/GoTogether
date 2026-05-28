package com.gotogether.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String passwordHash;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private int socialBattery;

    @Column(nullable = false)
    private int currency;

    @Column(nullable = false)
    private int experiencePoints;

    @Column(nullable = false)
    private LocalDateTime lastLogin;

//    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "settings_id", referencedColumnName = "id")
//    private Settings settings;
}
