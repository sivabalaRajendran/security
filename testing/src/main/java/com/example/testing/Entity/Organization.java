package com.example.testing.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Data

@ToString
@AllArgsConstructor
@NoArgsConstructor


@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String phoneNumber;
    private String address;
   private String password;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Otp> otps;

    private boolean verified;

    private String passwordResetToken;
    private LocalDateTime passwordResetTokenExpiration;

}

