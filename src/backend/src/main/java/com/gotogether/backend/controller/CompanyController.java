package com.gotogether.backend.controller;

import com.gotogether.backend.dto.CompanyCreateDTO;
import com.gotogether.backend.dto.CompanyLoginDTO;
import com.gotogether.backend.services.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for managing companies.
 * <p>
 * Provides endpoints for company signup, login, retrieval, and currency
 * management.
 */
@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService service;

    /**
     * Constructs a new CompanyController with the given CompanyService.
     *
     * @param service the service used for company operations
     */
    public CompanyController(CompanyService service) {
        this.service = service;
    }

    /**
     * Retrieves a company by its unique identifier.
     *
     * @param id the UUID of the company to retrieve
     * @return a ResponseEntity containing the CompanyDTO if found, or a 404 NOT
     *         FOUND status with an error message
     * @throws RuntimeException if the company is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(service.getCompanyById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Retrieves a list of all companies.
     *
     * @return a ResponseEntity containing a list of CompanyDTOs, or a 500 INTERNAL
     *         SERVER ERROR status on failure
     * @throws RuntimeException if an error occurs during retrieval
     */
    @GetMapping()
    public ResponseEntity<?> getAllCompanies() {
        try {
            return ResponseEntity.ok(service.getAllCompanies());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Creates a new company account (signup).
     *
     * @param dto the data transfer object containing the company details
     * @return a ResponseEntity containing the UUID of the newly created company, or
     *         a 400 BAD REQUEST status on validation failure
     * @throws RuntimeException if the validation fails or creation errors occur
     */
    @PostMapping("/signup")
    public ResponseEntity<?> createCompany(@RequestBody CompanyCreateDTO dto) {
        try {
            UUID id = service.createCompany(dto.getName(), dto.getPassword(), dto.getEmail(),
                    dto.getStreet(), dto.getHouseNumber(), dto.getZipCode(), dto.getCity(),
                    dto.getLatitude(), dto.getLongitude());
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Authenticates a company and logs them in.
     *
     * @param dto the data transfer object containing login credentials
     * @return a ResponseEntity containing the UUID of the logged-in company, or a
     *         401 UNAUTHORIZED status if authentication fails
     * @throws RuntimeException if the authentication fails
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginCompany(@RequestBody CompanyLoginDTO dto) {
        try {
            UUID id = service.loginCompany(dto.getEmail(), dto.getPassword());
            return ResponseEntity.ok(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Adds currency to a company's account.
     *
     * @param companyId the UUID of the company
     * @param currency  the amount of currency to add
     * @return a ResponseEntity containing the updated currency balance, or a 400
     *         BAD REQUEST status on failure
     * @throws RuntimeException if the operation fails
     */
    @PostMapping("/currency/{companyId}")
    public ResponseEntity<?> addCurrency(@PathVariable UUID companyId, @RequestBody int currency) {
        try {
            int updatedCurrency = service.addCompanyCurrency(companyId, currency);
            return ResponseEntity.ok(updatedCurrency);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
