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

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository repo;
    private final UserRepository userRepo;

    private final CompanyMapper companyMapper;
    private final SecurityService securityService;

    public CompanyDTO getCompanyById(UUID id) {
        return repo.findById(id)
                .map(companyMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }

    public List<CompanyDTO> getAllCompanies() {
        return repo.findAll().stream()
                .map(companyMapper::toDTO)
                .toList();
    }

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

    public UUID loginCompany(String email, String password) {
        Company company = authenticateCompany(email, password);

        repo.save(company);
        return company.getId();
    }

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

    public int addCompanyCurrency(UUID companyId, int amount) {
        if (amount < 0) {
            throw new RuntimeException("Amount must be positive: " + amount);
        }
        

        Company company = repo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));

        company.setCurrency(company.getCurrency() + amount);
        repo.save(company);
        return company.getCurrency();
    }

}