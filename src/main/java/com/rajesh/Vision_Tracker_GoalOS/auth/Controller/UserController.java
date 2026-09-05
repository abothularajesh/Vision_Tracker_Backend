package com.rajesh.Vision_Tracker_GoalOS.auth.Controller;

import com.rajesh.Vision_Tracker_GoalOS.auth.entity.User;
import com.rajesh.Vision_Tracker_GoalOS.auth.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api")
public class UserController {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("user")
    public Optional<User> details(@RequestParam Integer id){
        return userDetailsService.getUserDetails(id);
    }
}
