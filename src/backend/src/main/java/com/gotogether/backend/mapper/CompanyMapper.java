package com.gotogether.backend.mapper;

import com.gotogether.backend.dto.CompanyDTO;
import com.gotogether.backend.model.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyMapper {

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