package com.rajesh.Vision_Tracker_GoalOS.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Builder
@Table(name = "users")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(nullable = false)
    private String role;

    // Google's unique "sub" id - null for local-only accounts.
    @Column(nullable = false)
    private String provider;

    private String providerId;

    @OneToMany(mappedBy = "user")
    private List<Goal> goals;

    private Integer loginStreak=0;
    private LocalDate lastLoginDate;
}
