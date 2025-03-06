package com.example.testing.Service;

import com.example.testing.Dto.OrganizationDTO;
import com.example.testing.Dto.OtpDTO;

public interface OrganizationService {


    void register(OrganizationDTO organizationDTO);

    boolean verifyOtp(String email, OtpDTO otpDTO);

    void changePassword(String email, String oldPassword, String newPassword);

    String login(String email, String password);

    void resendOtp(String email);

    void forgotPassword(String email);


}