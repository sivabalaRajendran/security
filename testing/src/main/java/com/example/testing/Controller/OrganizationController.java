package com.example.testing.Controller;


import com.example.testing.Dto.OrganizationDTO;
import com.example.testing.Dto.OtpDTO;
import com.example.testing.Service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrganizationController {
    @Autowired
    private OrganizationService organizationService;

    @PostMapping("/register")
    public ResponseEntity<String> register( @RequestBody OrganizationDTO organizationDTO) {
        try {
            organizationService.register(organizationDTO);
            return ResponseEntity.ok("Registration successful. Check email for OTP verification.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("registration failed");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestBody OtpDTO otpDTO) {
        if (organizationService.verifyOtp(email, otpDTO)) {
            return ResponseEntity.ok("Account verified successfully.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
    }


    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestParam String email, @RequestParam String oldPassword, @RequestParam String newPassword) {
        organizationService.changePassword(email, oldPassword, newPassword);
        return ResponseEntity.ok("Password changed successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        System.out.println("Login request - Email: " + email);

        // Generate the JWT token after validating the user's credentials
        String token = organizationService.login(email, password);
        System.out.println("Generated Token: " + token);
        if (token != null) {
            // Set the token in the Authorization header
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);

            // Return a response with the token in the header and a success message
            return new ResponseEntity<>("Login successful.", headers, HttpStatus.OK);
        } else {
            // Return Unauthorized (401) if the credentials are incorrect
            return new ResponseEntity<>("Invalid credentials.", HttpStatus.UNAUTHORIZED);
        } }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestParam String email) {
        organizationService.resendOtp(email);
        return ResponseEntity.ok("OTP resent successfully. Check email.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        organizationService.forgotPassword(email);
        return ResponseEntity.ok("Password reset link sent. Check your email.");
    }


}
