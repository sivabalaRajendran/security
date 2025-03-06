package com.example.testing.repo;

import com.example.testing.Entity.Organization;
import com.example.testing.Entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByOrganizationAndOtp(Organization organization, String otp);
}

