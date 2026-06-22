package com.gotogether.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCreateDTO {

    private String name;

    private String password;

    private String email;

    private String street;

    private String houseNumber;

    private String zipCode;

    private String city;

    private double latitude;

    private double longitude;
}
