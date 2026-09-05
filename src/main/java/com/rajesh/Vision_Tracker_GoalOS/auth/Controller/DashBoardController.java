package com.rajesh.Vision_Tracker_GoalOS.auth.Controller;

import com.rajesh.Vision_Tracker_GoalOS.auth.dto.DashboardDTO;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.UserRepo;
import com.rajesh.Vision_Tracker_GoalOS.auth.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.stylesheets.LinkStyle;

import java.awt.*;

@Controller
@RequestMapping("api")
public class DashBoardController {

    @Autowired
    GoalService goalService;

    @Autowired
    UserRepo userRepo;

    @GetMapping("dashboard")
    public ResponseEntity<DashboardDTO> dashboard(Authentication authentication){
        String username=authentication.getName();
        return ResponseEntity.ok(goalService.dashboard(username));
    }
}
