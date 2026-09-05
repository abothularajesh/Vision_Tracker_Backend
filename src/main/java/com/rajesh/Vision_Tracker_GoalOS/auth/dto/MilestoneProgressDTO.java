package com.rajesh.Vision_Tracker_GoalOS.auth.dto;

import lombok.Data;

@Data
public class MilestoneProgressDTO {

    private Integer milestoneId;
    private String title;
    private double progress;
    private int totalTasks;
    private int completedTasks;
}
