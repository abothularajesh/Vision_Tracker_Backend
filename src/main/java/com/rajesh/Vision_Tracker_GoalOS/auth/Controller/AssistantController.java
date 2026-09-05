package com.rajesh.Vision_Tracker_GoalOS.auth.Controller;

import com.rajesh.Vision_Tracker_GoalOS.auth.dto.AssistantRequestDTO;
import com.rajesh.Vision_Tracker_GoalOS.auth.dto.AssistantResponseDTO;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.User;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.UserRepo;
import com.rajesh.Vision_Tracker_GoalOS.auth.service.AssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class AssistantController {

    @Autowired
    private AssistantService assistantService;

    @Autowired
    private UserRepo userRepo;

    public AssistantController(AssistantService assistantService){
        this.assistantService=assistantService;
    }

    @PostMapping("assistant/chat/{conversationId}")
    public ResponseEntity<AssistantResponseDTO> response(@RequestBody AssistantRequestDTO assistantRequestDTO, @PathVariable int conversationId){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        User user=userRepo.findByusername(username).orElseThrow(()->
                new UsernameNotFoundException("User Not Found"));
        int userId=user.getId();

        return ResponseEntity.ok(assistantService.getResponse(assistantRequestDTO, userId, conversationId));
    }
}
