package com.gotogether.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Location embeddable class representing geographic coordinates.
 * <p>
 * This class is used to store latitude and longitude information
 * as an embedded component within other entities like {@link Company}
 * and {@link Challenge}.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;
}
