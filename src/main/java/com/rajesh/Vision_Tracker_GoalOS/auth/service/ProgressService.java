package com.rajesh.Vision_Tracker_GoalOS.auth.service;

import com.rajesh.Vision_Tracker_GoalOS.auth.dto.GoalProgressDTO;
import com.rajesh.Vision_Tracker_GoalOS.auth.dto.MilestoneProgressDTO;
import com.rajesh.Vision_Tracker_GoalOS.auth.dto.ProgressDTO;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.Goal;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.MileStones;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.Tasks;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.GoalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProgressService {

    @Autowired
    private GoalRepo goalRepo;

    public ProgressDTO getData(Integer userId) {

        List<Goal> goals = goalRepo.findByUserId(userId);
        // Overall statistics
        int totalGoals = goals.size();
        int totalTasks = 0;
        int completedTasks = 0;

        List<GoalProgressDTO> goalProgressList = new ArrayList<>();

        // Loop through every goal
        for (Goal goal : goals) {

            int goalTotalTasks = 0;
            int goalCompletedTasks = 0;

            List<MilestoneProgressDTO> milestoneProgressList =
                    new ArrayList<>();


            // Loop through milestones
            for (MileStones milestone : goal.getMileStones()) {

                List<Tasks> tasks = milestone.getTasks();

                int milestoneTotalTasks = tasks.size();

                int milestoneCompletedTasks = (int) tasks.stream()
                        .filter(task ->
                                "COMPLETED".equals(task.getStatus()))
                        .count();

                // Add to goal statistics
                goalTotalTasks += milestoneTotalTasks;
                goalCompletedTasks += milestoneCompletedTasks;

                // Add to overall statistics
                totalTasks += milestoneTotalTasks;
                completedTasks += milestoneCompletedTasks;

                // Calculate milestone progress
                double milestoneProgress =
                        milestoneTotalTasks == 0
                                ? 0
                                : milestoneCompletedTasks * 100.0
                                / milestoneTotalTasks;


                MilestoneProgressDTO milestoneDTO =
                        new MilestoneProgressDTO();

                milestoneDTO.setMilestoneId(milestone.getId());
                milestoneDTO.setTitle(milestone.getTitle());
                milestoneDTO.setProgress(milestoneProgress);
                milestoneDTO.setTotalTasks(milestoneTotalTasks);
                milestoneDTO.setCompletedTasks(
                        milestoneCompletedTasks
                );

                milestoneProgressList.add(milestoneDTO);
            }

            // Calculate goal progress
            double goalProgress =
                    goalTotalTasks == 0
                            ? 0
                            : goalCompletedTasks * 100.0
                            / goalTotalTasks;


            GoalProgressDTO goalDTO =
                    new GoalProgressDTO();

            goalDTO.setGoalId(goal.getId());
            goalDTO.setTitle(goal.getTitle());
            goalDTO.setProgress(goalProgress);
            goalDTO.setTotalTasks(goalTotalTasks);
            goalDTO.setCompletedTasks(goalCompletedTasks);
            goalDTO.setMilestones(milestoneProgressList);

            goalProgressList.add(goalDTO);
        }


        // Calculate overall progress
        double overallProgress =
                totalTasks == 0
                        ? 0
                        : completedTasks * 100.0
                        / totalTasks;


        // Pending tasks
        int pendingTasks = totalTasks - completedTasks;


        // Create final response
        ProgressDTO response =
                new ProgressDTO();

        response.setOverAllProgress(overallProgress);
        response.setTotalGoals(totalGoals);
        response.setTotalTasks(totalTasks);
        response.setCompletedTasks(completedTasks);
        response.setPendingTasks(pendingTasks);
        response.setGoals(goalProgressList);

        return response;
    }
}
