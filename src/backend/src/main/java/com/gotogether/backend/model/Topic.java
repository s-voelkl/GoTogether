package com.gotogether.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Topic {

    public Topic(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    private String name;

    // private String description;

    // @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    // @JoinColumn(name = "settings_id", referencedColumnName = "id")
    // private Settings settings;
}
