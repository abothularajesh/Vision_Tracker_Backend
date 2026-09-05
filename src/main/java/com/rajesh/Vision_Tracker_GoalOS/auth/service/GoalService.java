package com.rajesh.Vision_Tracker_GoalOS.auth.service;

import com.rajesh.Vision_Tracker_GoalOS.auth.dto.*;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.Goal;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.MileStones;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.Tasks;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.User;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.GoalRepo;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.MileStoneRepo;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.TasksRepo;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.UserRepo;
import com.rajesh.Vision_Tracker_GoalOS.auth.utils.GoalStatus;
import com.rajesh.Vision_Tracker_GoalOS.auth.utils.MileStoneStatus;
import com.rajesh.Vision_Tracker_GoalOS.auth.utils.TaskPriority;
import com.rajesh.Vision_Tracker_GoalOS.auth.utils.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GoalService {

    @Autowired
    private GoalRepo repo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AiService aiService;

    @Autowired
    private MileStoneRepo mileStoneRepo;

    @Autowired
    private TasksRepo tasksRepo;

    public List<GoalResponseDTO> getGoalsByUserId(Integer userId) {
        List<Goal> goals = repo.findByUserId(userId);

        List<GoalResponseDTO> responseList = new ArrayList<>();

        for (Goal goal1 : goals) {
            GoalResponseDTO goalResponseDTO = new GoalResponseDTO();
            goalResponseDTO.setId(goal1.getId());
            goalResponseDTO.setTitle(goal1.getTitle());
            goalResponseDTO.setDescription(goal1.getDescription());
            goalResponseDTO.setStatus(String.valueOf(goal1.getStatus()));
            goalResponseDTO.setCurrentLevel(String.valueOf(goal1.getCurrentLevel()));

            if (goal1.getTargetDate() != null) {
                goalResponseDTO.setTargetDate(goal1.getTargetDate().atStartOfDay());
            }
            int totalTasks=0;
            int completedTasks=0;
            for(MileStones mileStones: goal1.getMileStones()){
                totalTasks+=mileStones.getTasks().size();
                completedTasks+=(int) mileStones.getTasks().stream()
                        .filter(tasks -> "COMPLETED".equals(tasks.getStatus()))
                        .count();
            }
            double progress=totalTasks==0?0:(completedTasks*100.0)/totalTasks;
            goalResponseDTO.setProgress(progress);

            responseList.add(goalResponseDTO);
        }
        return responseList;
    }

    public ResponseEntity<Goal> addGoals(GoalRequestDTO requestDTO) {
        Goal goal=new Goal();
        goal.setId(goal.getId());
        goal.setTitle(requestDTO.getTitle());
        goal.setDescription(requestDTO.getDescription());
        goal.setTargetDate(requestDTO.getTargetDate());
        goal.setCurrentLevel(requestDTO.getCurrentLevel());

        goal.setStatus(GoalStatus.ACTIVE);
        LocalDateTime dateTime = LocalDateTime.now();
        goal.setCreatedAt(dateTime);
        goal.setUpdatedAt(dateTime);

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        String username = authentication.getName();
        User user= userRepo.findByusername(username).orElseThrow(()->
                            new UsernameNotFoundException("User Not Found"));

        goal.setUser(user);

        return ResponseEntity.ok(repo.save(goal));
    }

    public Optional<Goal> goalsById(Integer id) {
        return repo.findById(id);
    }

    public GoalResponseDTO updateGoal(Integer id, Goal goal) {

        Goal existingGoal = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Goal not found with id: " + id
                        )
                );


        existingGoal.setTitle(goal.getTitle());
        existingGoal.setDescription(goal.getDescription());
        existingGoal.setCurrentLevel(goal.getCurrentLevel());
        existingGoal.setTargetDate(goal.getTargetDate());
        existingGoal.setStatus(existingGoal.getStatus());

        existingGoal.setUpdatedAt(LocalDateTime.now());

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null) {
            throw new RuntimeException(
                    "Authentication not found"
            );
        }

        String username = authentication.getName();

        User user = userRepo
                .findByusername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User Not Found"
                        )
                );

        existingGoal.setUser(user);


        Goal savedGoal = repo.save(existingGoal);


        GoalResponseDTO response = new GoalResponseDTO();

        response.setId(savedGoal.getId());
        response.setTitle(savedGoal.getTitle());
        response.setDescription(savedGoal.getDescription());
        response.setTargetDate(savedGoal.getTargetDate().atStartOfDay());
        response.setCurrentLevel(String.valueOf(savedGoal.getCurrentLevel()));
        response.setStatus(String.valueOf(savedGoal.getStatus()));

        // Keep your existing progress calculation here
        response.setProgress(calculateProgress(savedGoal.getId()));

        return response;
    }

    private double calculateProgress(Integer id) {
        Goal goal=repo.findById(id).orElseThrow(()->
                new RuntimeException("Goal not found "+id));
        int totalTasks=0;
        int completedTasks=0;
        for(MileStones mileStones: goal.getMileStones()){
            totalTasks+=mileStones.getTasks().size();
            completedTasks+=(int) mileStones.getTasks().stream()
                    .filter(tasks -> "COMPLETED".equals(tasks.getStatus()))
                    .count();
        }
        double progress=totalTasks==0?0:(completedTasks*100.0)/totalTasks;
        return progress;
    }

    public void deleteGoal(Integer id) {
        repo.deleteById(id);
    }

    @Transactional
    public AiResponseDTO generateRoadmap(Integer id, Integer conversationId) {
        Goal goal = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        String title=goal.getTitle();
        String description=goal.getDescription();
        LocalDateTime targetDate= goal.getTargetDate().atStartOfDay();
        String currentLevel= String.valueOf(goal.getCurrentLevel());

        //Generated Roadmap using - ai
        AiResponseDTO aiResponseDTO=aiService.generateRoadmap(title, description, targetDate, currentLevel, conversationId);

        //convert ai response to ->milestones + tasks
        List<MileStones> milestones = new ArrayList<>();
        List<Tasks> tasks = new ArrayList<>();

        for (MileStoneResponse aiMilestone :
                aiResponseDTO.getMilestones()) {

            MileStones milestone = new MileStones();

//            milestone.setId(milestone.getId());
            milestone.setGoal(goal);
            milestone.setTitle(aiMilestone.getTitle());
            milestone.setDescription(aiMilestone.getDescription());
            milestone.setOrderNumber(aiMilestone.getOrderNumber());
            milestone.setStatus(
                    String.valueOf(MileStoneStatus.PENDING)
            );

            LocalDateTime now = LocalDateTime.now();

            milestone.setCreatedAt(now);
            milestone.setUpdatedAt(now);

            milestones.add(milestone);

            for (TaskResponse aiTask :
                    aiMilestone.getTasks()) {

                Tasks task = new Tasks();

                task.setMileStones(milestone);
                task.setTitle(aiTask.getTitle());
                task.setDescription(aiTask.getDescription());

                task.setPriority(
                        String.valueOf(
                                TaskPriority.valueOf(
                                        aiTask.getPriority()
                                )
                        )
                );

                task.setEstimated_hours(
                        aiTask.getEstimatedHours()
                );

                task.setStatus(
                        String.valueOf(TaskStatus.PENDING)
                );

                task.setCreatedAt(now);
                task.setUpdatedAt(now);

                tasks.add(task);
            }
        }

        // Save milestones first
        mileStoneRepo.saveAll(milestones);

        // Then save tasks
        tasksRepo.saveAll(tasks);

        return aiResponseDTO;

    }
    @Transactional(readOnly = true)
    public ResponseEntity<GoalroadmapResponse> getGoalRoadmap(Integer id) {

        Goal goal = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Goals Not Found"));

        List<MileStoneResponse> mileStoneDTO = new ArrayList<>();

        // Used for overall goal progress
        int totalTasks = 0;
        int completedTasks = 0;
        for (MileStones mileStones : goal.getMileStones()) {
            // -----------------------------
            // Milestone task calculation
            // -----------------------------
            int milestoneTotalTasks = mileStones.getTasks().size();

            int milestoneCompletedTasks = (int) mileStones.getTasks()
                    .stream()
                    .filter(task ->
                            "COMPLETED".equals(task.getStatus()))
                    .count();

            // Add to overall goal calculation
            totalTasks += milestoneTotalTasks;
            completedTasks += milestoneCompletedTasks;

            // -----------------------------
            // Task DTOs
            // -----------------------------
            List<TaskResponse> taskResponse = new ArrayList<>();

            for (Tasks task : mileStones.getTasks()) {

                TaskResponse taskDTO = new TaskResponse();

                taskDTO.setTaskId(task.getId());
                taskDTO.setTitle(task.getTitle());
                taskDTO.setDescription(task.getDescription());
                taskDTO.setPriority(task.getPriority());
                taskDTO.setStatus(task.getStatus());
                taskDTO.setEstimatedHours(task.getEstimated_hours());

                taskResponse.add(taskDTO);
            }
            // -----------------------------
            // Milestone DTO
            // -----------------------------
            MileStoneResponse mileStoneRes = new MileStoneResponse();

            mileStoneRes.setTitle(mileStones.getTitle());
            mileStoneRes.setDescription(mileStones.getDescription());
            mileStoneRes.setOrderNumber(mileStones.getOrderNumber());
            mileStoneRes.setTasks(taskResponse);

            // Calculate THIS milestone's progress
            double milestoneProgress =
                    milestoneTotalTasks == 0
                            ? 0
                            : (milestoneCompletedTasks * 100.0)
                            / milestoneTotalTasks;

            mileStoneRes.setProgress(milestoneProgress);

            mileStoneDTO.add(mileStoneRes);
        }

        // -----------------------------
        // Overall Goal Progress
        // -----------------------------
        double goalProgress =
                totalTasks == 0
                        ? 0
                        : (completedTasks * 100.0) / totalTasks;

        // -----------------------------
        // Goal Roadmap Response
        // -----------------------------
        GoalroadmapResponse roadMapDTO = new GoalroadmapResponse();

        roadMapDTO.setGoalId(goal.getId());
        roadMapDTO.setTitle(goal.getTitle());
        roadMapDTO.setMilestones(mileStoneDTO);
        roadMapDTO.setProgress(goalProgress);

        return ResponseEntity.ok(roadMapDTO);
    }

    public Tasks updateTasks(Integer id, TaskResponse tasks) {

        Tasks existingTask = tasksRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Task not found"));
//        System.out.println("OLD estimation Hours: " + existingTask.getEstimated_hours());
//        System.out.println("NEW Estimation Hours: " + tasks.getEstimatedHours());

        existingTask.setTitle(tasks.getTitle());
        existingTask.setDescription(tasks.getDescription());
        existingTask.setPriority(tasks.getPriority());
        existingTask.setStatus(tasks.getStatus());
        existingTask.setEstimated_hours(tasks.getEstimatedHours());
        LocalDateTime dateTime=LocalDateTime.now();
        existingTask.setUpdatedAt(dateTime);

        Tasks savedTasks=tasksRepo.save(existingTask);

        //System.out.println("SAVED Estimation Hours: " + savedTasks.getEstimated_hours());

        return savedTasks;


    }

    public DashboardDTO dashboard(String username) {
        User user = userRepo.findByusername(username).orElseThrow(()->new RuntimeException("User Not Found"));
        List<Goal> goals=user.getGoals();
        Integer totalGoals=goals.size();

        long activeGoals = goals.stream()
                .filter(goal -> goal.getStatus() == GoalStatus.ACTIVE)
                .count();

        long completedGoals = goals.stream()
                .filter(goal -> goal.getStatus() == GoalStatus.COMPLETED)
                .count();

        int totalTasks=0;
        int completedTasks=0;
        for(Goal goal: user.getGoals()){
            for(MileStones mileStones: goal.getMileStones()){
//                totalTasks+=mileStones.getTasks().size();
                for(Tasks tasks: mileStones.getTasks()){
                    totalTasks++;
                    if ("COMPLETED".equals(tasks.getStatus())) {
                        completedTasks++;
                    }
                }
            }
        }
        double overAllProgress=totalTasks==0?0:(completedTasks*100.0)/totalTasks;
        DashboardDTO dashboardDTO=new DashboardDTO();
        dashboardDTO.setUsername(username);
        dashboardDTO.setTotalGoals(totalGoals);
        dashboardDTO.setCompletedTasks(completedTasks);
        dashboardDTO.setTotalTasks(totalTasks);
        dashboardDTO.setCompletedGoals(completedGoals);
        dashboardDTO.setActiveGoals(activeGoals);
        dashboardDTO.setOverallProgress(overAllProgress);

        return dashboardDTO;

    }
}
