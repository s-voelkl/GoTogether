package com.gotogether.backend.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Topic entity class representing a theme or interest category.
 * <p>
 * Topics are used to categorize both {@link User} interests and
 * {@link Challenge} themes. They can have an associated icon and
 * background color for visual representation.
 */
@Entity
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Topic {

    /**
     * Constructs a new Topic with the specified name.
     *
     * @param name the name of the topic
     */
    public Topic(String name) {
        this.name = name;
    }

    /**
     * Constructs a new Topic with the specified name, icon, and background color.
     *
     * @param name            the name of the topic
     * @param icon            the icon representing the topic
     * @param backgroundColor the background color associated with the topic
     */
    public Topic(String name, String icon, String backgroundColor) {
        this.name = name;
        this.icon = icon;
        this.backgroundColor = backgroundColor;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column
    private String icon;

    @Column(name = "background_color", length = 9)
    private String backgroundColor;
}