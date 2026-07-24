package com.gotogether.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Settings embeddable class representing user-specific settings.
 * <p>
 * This class encapsulates settings configuration and is embedded
 * within the {@link User} entity to keep user preferences inline.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Settings {

    @Column(nullable = false)
    private String setting = "";
}
