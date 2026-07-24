package com.gotogether.backend.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Company entity class representing a business partner in the system.
 * <p>
 * Companies can host {@link Challenge}s and provide rewards. They have
 * physical locations, contact details, and a currency balance that they
 * use to fund challenge rewards.
 */
@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    private static final int DEFAULT_CURRENCY = 0;

    /**
     * Constructs a new Company with the specified details.
     *
     * @param name     the name of the company
     * @param password the company's hashed password
     * @param email    the company's unique email address
     * @param address  the physical address of the company
     * @param location the geographic location of the company
     */
    public Company(String name, String password, String email, Address address, Location location) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.address = address;
        this.location = location;
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
    private int currency = DEFAULT_CURRENCY;

    @Embedded
    private Address address;

    @Embedded
    private Location location;
}