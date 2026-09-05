package com.rajesh.Vision_Tracker_GoalOS.auth.dto;

import lombok.Data;
import java.util.List;

@Data
public class GoalProgressDTO {

    private Integer goalId;

    private String title;

    private double progress;

    private int totalTasks;

    private int completedTasks;

    private List<MilestoneProgressDTO> milestones;
}
