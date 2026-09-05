package com.rajesh.Vision_Tracker_GoalOS.auth.service;

import com.rajesh.Vision_Tracker_GoalOS.auth.repository.UserRepo;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepo repo;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user=repo.findByusername(username)
                .orElseThrow(()->new UsernameNotFoundException("No user found with this name: "+username));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()==null ?"":user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_"+ user.getRole())))
                .build();
    }

    public Optional<User> getUserDetails(Integer id) {
        return repo.findById(String.valueOf(id));
    }
}
