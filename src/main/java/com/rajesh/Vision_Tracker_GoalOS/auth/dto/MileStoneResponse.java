package com.rajesh.Vision_Tracker_GoalOS.auth.dto;

import lombok.Data;

import java.util.List;

@Data
public class MileStoneResponse {
    private String title;
    private String description;
    private Integer orderNumber;

    private double progress;

    private List<TaskResponse> tasks;
}
