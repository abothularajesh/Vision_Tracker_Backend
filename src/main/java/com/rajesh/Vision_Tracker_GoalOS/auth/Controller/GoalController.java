package com.rajesh.Vision_Tracker_GoalOS.auth.Controller;

import com.rajesh.Vision_Tracker_GoalOS.auth.dto.*;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.Goal;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.Tasks;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.User;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.UserRepo;
import com.rajesh.Vision_Tracker_GoalOS.auth.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api")
public class GoalController {

    @Autowired
    private GoalService goalService;

    @Autowired
    private UserRepo userRepo;

    @GetMapping("goals")
    public ResponseEntity<List<GoalResponseDTO>> getGoals(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        String username=authentication.getName();
        User user=userRepo.findByusername(username).orElseThrow(()->
                new UsernameNotFoundException("UserName NotFound: "+username));
        Integer userId=user.getId();

        return ResponseEntity.ok(goalService.getGoalsByUserId(userId));
    }

    @PostMapping("goals")
    public ResponseEntity<Goal> addGoals(@RequestBody GoalRequestDTO requestDTO){
        return goalService.addGoals(requestDTO);
    }

    @PostMapping("goals/{id}")
    public ResponseEntity<Optional<Goal>> goalsById(@PathVariable Integer id){
        return ResponseEntity.ok(goalService.goalsById(id));
    }

    @PostMapping("goals/{id}/{conversationId}/generate-roadmap")
    public ResponseEntity<AiResponseDTO> generateRoadmap(@PathVariable Integer id, @PathVariable Integer conversationId){
        AiResponseDTO aiResponseDTO=goalService.generateRoadmap(id, conversationId);
        return ResponseEntity.ok(aiResponseDTO);
    }

    @PutMapping("goals/{id}")
    public ResponseEntity<GoalResponseDTO> updateGoal(@PathVariable Integer id,@RequestBody Goal goal){
        return ResponseEntity.ok(goalService.updateGoal(id, goal));
    }

    @DeleteMapping("goals/{id}")
    public ResponseEntity<String> deleteGoal(@PathVariable Integer id){
        goalService.deleteGoal(id);
        return ResponseEntity.ok("Deleted Successfully");
    }

    @GetMapping("goals/{id}/roadmap")
    public ResponseEntity<GoalroadmapResponse> getGoalRoadmap(@PathVariable Integer id){
        return goalService.getGoalRoadmap(id);
    }

    @PutMapping("tasks/{id}")
    public ResponseEntity<String> updateTasks(@PathVariable Integer id,@RequestBody TaskResponse tasks){
        //System.out.println("TASK Estimation hours REQUEST: " + tasks.getEstimatedHours());
//        System.out.println("TASK STATUS FROM REQUEST: " + tasks.getStatus());
        goalService.updateTasks(id, tasks);
        return ResponseEntity.ok("Tasks Updated");
    }


}
