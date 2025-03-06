package com.example.testing.Dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OtpDTO {
    private String otp;
    private LocalDateTime otpGeneratedAt;
    private LocalDateTime otpExpiresAt;
    private boolean verified;
    private String organizationEmail;
}