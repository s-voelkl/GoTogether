package com.gotogether.backend.mapper;

import com.gotogether.backend.dto.CompanyDTO;
import com.gotogether.backend.model.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting {@link Company} entities to Data Transfer Objects (DTOs).
 * <p>
 * This class handles mapping the internal {@link Company} representation, including
 * its embedded address and location, into a flattened {@link CompanyDTO} structure.
 */
@Component
@RequiredArgsConstructor
public class CompanyMapper {

    /**
     * Converts a {@link Company} entity to a {@link CompanyDTO}.
     *
     * @param company the {@link Company} entity to map
     * @return a flattened {@link CompanyDTO} representation of the company
     */
    public CompanyDTO toDTO(Company company) {
        return CompanyDTO.builder()
                .id(company.getId())
                .name(company.getName())
                .email(company.getEmail())
                .street(company.getAddress().getStreet())
                .houseNumber(company.getAddress().getHouseNumber())
                .zipCode(company.getAddress().getZipCode())
                .city(company.getAddress().getCity())
                .latitude(company.getLocation().getLatitude())
                .longitude(company.getLocation().getLongitude())
                .build();
    }
}