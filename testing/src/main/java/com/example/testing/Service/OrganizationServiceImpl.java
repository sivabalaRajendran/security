package com.example.testing.Service;

import com.example.testing.Dto.OrganizationDTO;
import com.example.testing.Dto.OtpDTO;
import com.example.testing.Entity.Organization;
import com.example.testing.Entity.Otp;
import com.example.testing.repo.OtpRepository;
import com.example.testing.repo.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OtpRepository otpRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private JwtService jwtService;


    @Override
    public void register(OrganizationDTO organizationDTO) {
        // Map OrganizationDTO to Organization entity
        Organization organization = new Organization();
        organization.setName(organizationDTO.getName());
        organization.setEmail(organizationDTO.getEmail());
        organization.setFirstName(organizationDTO.getFirstName());
        organization.setLastName(organizationDTO.getLastName());
        organization.setPhoneNumber(organizationDTO.getPhoneNumber());
        organization.setAddress(organizationDTO.getAddress());
        organization.setPassword(new BCryptPasswordEncoder().encode(organizationDTO.getPassword()));  // Encrypt password
        organization.setVerified(false);
        try {
            // Save organization entity to the database
            organizationRepository.save(organization);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register organization", e);
        }
        String otp = String.format("%06d", new Random().nextInt(1000000)); // Generate 6-digit OTP
        Otp otpEntity = new Otp();
        otpEntity.setOtp(otp);
        otpEntity.setOtpGeneratedAt(LocalDateTime.now());
        otpEntity.setOtpExpiresAt(LocalDateTime.now().plusMinutes(5)); // OTP expires in 5 minutes
        otpEntity.setVerified(false);
        otpEntity.setOrganization(organization);  // Link OTP to the organization

        // Save OTP entity to the database
        otpRepository.save(otpEntity);

        // Send OTP email for verification
        sendVerificationEmail(organization.getEmail(), otp);

    }

    private void sendVerificationEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verify Your Account");
        message.setText("Your OTP is: " + otp);
        mailSender.send(message);
    }

    @Override
    public void changePassword(String email, String oldPassword, String newPassword) {
        Organization organization = organizationRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!new BCryptPasswordEncoder().matches(oldPassword, organization.getPassword())) {
            throw new BadCredentialsException("Old password is incorrect");
        }

        organization.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        organizationRepository.save(organization);
    }

    @Override
    public String login(String email, String password) {
        System.out.println("Attempting login for email: " + email);
        // 1. Authenticate the user by checking if the email exists in the repository
        Organization organization = organizationRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 2. Validate the password using BCryptPasswordEncoder
        if (!new BCryptPasswordEncoder().matches(password, organization.getPassword())) {
            System.out.println("Password mismatch.");

            throw new BadCredentialsException("Invalid credentials");
        }

        // 3. If authentication is successful, generate the JWT token
        return jwtService.generateToken(
                email,
                organization.getPhoneNumber(),
                organization.getId().intValue(),  // Assuming organizationKeyId is the ID
                (short) 1,  // Assuming organizationType is 1 (can be updated as needed)
                organization.getId()  // organizationKeyId is the same as organization ID
        );}

    @Override
    public boolean verifyOtp(String email, OtpDTO otpDTO) {
        Optional<Organization> orgOpt = organizationRepository.findByEmail(email);
        if (orgOpt.isPresent()) {
            Organization organization = orgOpt.get();
            Optional<Otp> otpEntityOpt = otpRepository.findByOrganizationAndOtp(organization, otpDTO.getOtp());

            if (otpEntityOpt.isPresent()) {
                Otp otpEntity = otpEntityOpt.get();
                // Check if OTP is not expired and hasn't been verified
                if (otpEntity.getOtpExpiresAt().isAfter(LocalDateTime.now()) && !otpEntity.isVerified()) {
                    otpEntity.setVerified(true);  // Mark OTP as verified
                    otpRepository.save(otpEntity);  // Save updated OTP entity
                    return true;
                }
            }
        }
        return false; // OTP is invalid or expired
    }

    @Override
    public void resendOtp(String email) {
        Optional<Organization> organizationOpt = organizationRepository.findByEmail(email);
        if (organizationOpt.isPresent()) {
            Organization organization = organizationOpt.get();
            String newOtp = String.valueOf(new Random().nextInt(999999)); // Generate a new OTP
            Otp otpEntity = new Otp();
            otpEntity.setOtp(newOtp);
            otpEntity.setOtpGeneratedAt(LocalDateTime.now());
            otpEntity.setOtpExpiresAt(LocalDateTime.now().plusMinutes(5)); // OTP expires in 5 minutes
            otpEntity.setVerified(false);
            otpEntity.setOrganization(organization);
            otpRepository.save(otpEntity);

            sendVerificationEmail(organization.getEmail(), newOtp);
        }
    }


    @Override
    public void forgotPassword(String email) {
        Optional<Organization> organizationOpt = organizationRepository.findByEmail(email);
        if (organizationOpt.isPresent()) {
            Organization organization = organizationOpt.get();
            String resetToken = generateResetToken();
            organization.setPasswordResetToken(resetToken);
            organization.setPasswordResetTokenExpiration(LocalDateTime.now().plusHours(1)); // Token expires in 1 hour
            organizationRepository.save(organization);

            sendResetPasswordEmail(organization.getEmail(), resetToken);
        }
    }

    // Helper method to send reset password email
    private void sendResetPasswordEmail(String email, String resetToken) {
        // Implement reset password email logic here
    }

    // Helper method to generate reset token
    private String generateResetToken() {
        return String.valueOf(new Random().nextInt(999999)); // Generate a 6-digit reset token
    }










}








