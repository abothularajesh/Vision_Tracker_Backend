package com.rajesh.Vision_Tracker_GoalOS.auth.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProgressDTO {

    private double overAllProgress;
    private int totalGoals;
    private int totalTasks;
    private int completedTasks;
    private int pendingTasks;
    private List<GoalProgressDTO> goals;
}
