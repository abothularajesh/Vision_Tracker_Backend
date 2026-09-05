package com.rajesh.Vision_Tracker_GoalOS.auth.dto;

import lombok.Data;

@Data
public class TaskResponse {

    private Integer taskId;
    private String title;
    private String description;
    private String status;
    private String priority;
    private Integer estimatedHours;
}
