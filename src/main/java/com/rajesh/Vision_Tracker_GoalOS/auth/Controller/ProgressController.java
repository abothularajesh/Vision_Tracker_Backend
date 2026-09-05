package com.rajesh.Vision_Tracker_GoalOS.auth.Controller;

import com.rajesh.Vision_Tracker_GoalOS.auth.dto.ProgressDTO;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.User;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.UserRepo;
import com.rajesh.Vision_Tracker_GoalOS.auth.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("api")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @Autowired
    private UserRepo userRepo;

    @GetMapping("progress")
    public ResponseEntity<ProgressDTO> getData(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        String username= authentication.getName();
        User user=userRepo.findByusername(username).orElseThrow(()->
                new UsernameNotFoundException("Username NotFound: "+username));

        Integer userId= user.getId();
        return ResponseEntity.ok(progressService.getData(userId));
    }
}
