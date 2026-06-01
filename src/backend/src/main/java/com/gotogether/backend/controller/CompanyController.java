package com.gotogether.backend.controller;

import com.gotogether.backend.dto.CompanyCreateDTO;
import com.gotogether.backend.dto.CompanyLoginDTO;
import com.gotogether.backend.services.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService service;

    public CompanyController(CompanyService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(service.getCompanyById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllCompanies() {
        try {
            return ResponseEntity.ok(service.getAllCompanies());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

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

    @PostMapping("/login")
    public ResponseEntity<?> loginCompany(@RequestBody CompanyLoginDTO dto) {
        try {
            UUID id = service.loginCompany(dto.getEmail(), dto.getPassword());
            return ResponseEntity.ok(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // add currency method
    @PutMapping("/currency/{companyId}")
    public ResponseEntity<?> addCurrency(@PathVariable UUID companyId, @RequestBody int currency) {
        try {
            int updatedCurrency = service.addCompanyCurrency(companyId, currency);
            return ResponseEntity.ok(updatedCurrency);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
