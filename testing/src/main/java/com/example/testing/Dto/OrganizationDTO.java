package com.example.testing.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrganizationDTO {
    private String name;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String password;
}
