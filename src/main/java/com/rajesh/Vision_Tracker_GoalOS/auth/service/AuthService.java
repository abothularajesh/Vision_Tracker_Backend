package com.rajesh.Vision_Tracker_GoalOS.auth.service;

import com.rajesh.Vision_Tracker_GoalOS.auth.repository.UserRepo;
import com.rajesh.Vision_Tracker_GoalOS.auth.dto.AuthResponse;
import com.rajesh.Vision_Tracker_GoalOS.auth.dto.LoginRequest;
import com.rajesh.Vision_Tracker_GoalOS.auth.dto.RegisterRequest;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    UserRepo repo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    CustomUserDetailsService userDetailsService;
    @Autowired
    JwtService jwtService;
    @Autowired
    StreakService streakService;

    public AuthResponse register(RegisterRequest request) {
        if(repo.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException("An account with this email is already exists");
        }
        User user=User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .provider("LOCAL")
                .build();

        repo.save(user);

        String token = jwtService.generateToken(user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        User user = repo.findByusername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        streakService.updateStreak(user);
        repo.save(user);
        String token = jwtService.generateToken(userDetails.getUsername());
//        AuthResponse response=new AuthResponse();
//        response.set
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .loginStreak(String.valueOf(user.getLoginStreak()))
                .build();
    }
}
