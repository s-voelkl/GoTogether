package com.gotogether.backend.services;

import com.gotogether.backend.dto.CompanyDTO;
import com.gotogether.backend.mapper.CompanyMapper;
import com.gotogether.backend.model.Address;
import com.gotogether.backend.model.Company;
import com.gotogether.backend.model.Location;
import com.gotogether.backend.repository.CompanyRepository;
import com.gotogether.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing company-related operations such as creation,
 * authentication,
 * and currency modifications.
 */
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository repo;
    private final UserRepository userRepo;

    private final CompanyMapper companyMapper;
    private final SecurityService securityService;

    /**
     * Retrieves a company by its unique identifier.
     *
     * @param id the UUID of the company to retrieve
     * @return the company data transfer object
     * @throws RuntimeException if the company is not found
     */
    public CompanyDTO getCompanyById(UUID id) {
        if (id == null) {
            throw new RuntimeException("Company ID must not be null.");
        }

        return repo.findById(id)
                .map(companyMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }

    /**
     * Retrieves a list of all registered companies.
     *
     * @return a list of company data transfer objects
     */
    public List<CompanyDTO> getAllCompanies() {
        return repo.findAll().stream()
                .map(companyMapper::toDTO)
                .toList();
    }

    /**
     * Creates a new company record after validating all input data.
     * Ensure the email is unique across both companies and users before creating.
     *
     * @param name        the name of the company
     * @param password    the unhashed password
     * @param email       an email address, which must be unique
     * @param street      the street of the company address
     * @param houseNumber the house number of the company address
     * @param zipCode     the zip code of the company address
     * @param city        the city of the company address
     * @param latitude    the latitude coordinate for the company location
     * @param longitude   the longitude coordinate for the company location
     * @return the UUID of the newly created company
     * @throws RuntimeException if inputs are invalid or the email is already taken
     */
    public UUID createCompany(String name, String password, String email, String street, String houseNumber,
            String zipCode, String city, double latitude, double longitude) {
        // email validation
        if (email == null
                || email.trim().isEmpty()
                || !EmailValidator.getInstance().isValid(email.trim().toLowerCase())) {
            throw new RuntimeException("Invalid email address: " + email);
        }

        String normalizedEmail = email.trim().toLowerCase();

        // email must be unique in both companies and users
        if (repo.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Email already exists: " + email);
        }

        if (userRepo.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Email already exists at users: " + email);
        }

        // company name
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Company name must not be empty: " + name);
        }

        // password
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Password must not be empty.");
        }

        // address
        if (street == null || street.trim().isEmpty()
                || houseNumber == null || houseNumber.trim().isEmpty()
                || city == null || city.trim().isEmpty()
                || zipCode == null || zipCode.trim().isEmpty()) {
            throw new RuntimeException("Address must not be null and must have all fields filled.");
        }

        // location: lat
        if (latitude < -90 || latitude > 90) {
            throw new RuntimeException("Latitude must be between -90 and 90: " + latitude);
        }

        // location: long
        if (longitude < -180 || longitude > 180) {
            throw new RuntimeException("Longitude must be between -180 and 180: " + longitude);
        }

        String passwordHash = securityService.hashPassword(password);

        Address address = new Address(street.trim(), houseNumber.trim(), zipCode.trim(), city.trim());
        Location location = new Location(latitude, longitude);

        // create company
        Company company = repo.save(new Company(
                name.trim(),
                passwordHash,
                normalizedEmail,
                address,
                location));

        return company.getId();
    }

    /**
     * Logs in a company using email and password, returning their ID.
     * Authenticates the credentials and updates the company record.
     *
     * @param email    the email of the company to log in
     * @param password the password associated with the email
     * @return the UUID of the authenticated company
     * @throws RuntimeException if authentication fails
     */
    public UUID loginCompany(String email, String password) {
        Company company = authenticateCompany(email, password);

        if (company == null) {
            throw new RuntimeException("Company not found with email: " + email);
        }

        repo.save(company);
        return company.getId();
    }

    /**
     * Authenticates a company by validating its email format, verifying existence,
     * and checking the password against the stored hash.
     *
     * @param email    the company's email
     * @param password the company's plain text password
     * @return the authenticated company entity
     * @throws RuntimeException if the email is invalid, the company doesn't exist,
     *                          or the password is incorrect
     */
    public Company authenticateCompany(String email, String password) {
        // validate email input
        if (email == null
                || email.trim().isEmpty()
                || !EmailValidator.getInstance().isValid(email.trim().toLowerCase())) {
            throw new RuntimeException("Invalid email address: " + email);
        }

        // validate password input
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Password must not be empty.");
        }

        String normalizedEmail = email.trim().toLowerCase();

        // find company by email
        Company company = repo.findByEmail(normalizedEmail);
        if (company == null) {
            throw new RuntimeException("No company found with email: " + email);
        }

        // check password
        if (!securityService.passwordMatches(password, company.getPassword())) {
            throw new RuntimeException("Invalid password.");
        }

        return company;
    }

    /**
     * Adds currency to the specific company's balance.
     *
     * @param companyId the UUID of the company to update
     * @param amount    the positive amount of currency to add
     * @return the new total currency balance
     * @throws RuntimeException if the amount is negative or company not found
     */
    public int addCompanyCurrency(UUID companyId, int amount) {
        if (amount < 0) {
            throw new RuntimeException("Amount must be positive: " + amount);
        }

        if (companyId == null) {
            throw new RuntimeException("Company ID must not be null.");
        }

        Company company = repo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));

        company.setCurrency(company.getCurrency() + amount);
        repo.save(company);
        return company.getCurrency();
    }

}