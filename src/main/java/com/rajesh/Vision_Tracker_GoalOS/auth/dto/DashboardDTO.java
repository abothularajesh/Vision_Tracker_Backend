package com.rajesh.Vision_Tracker_GoalOS.auth.dto;

import lombok.Data;

@Data
public class DashboardDTO {

    private String username;

    private int totalGoals;

    private long activeGoals;

    private long completedGoals;

    private int totalTasks;

    private int completedTasks;

    private double overallProgress;
}
