package com.gotogether.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyDTO {

    private String name;

    private String email;

    private String street;

    private String houseNumber;

    private String zipCode;

    private String city;

    private double latitude;

    private double longitude;
}
