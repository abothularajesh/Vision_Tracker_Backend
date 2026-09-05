package com.rajesh.Vision_Tracker_GoalOS.auth.dto;

import lombok.Data;

import java.util.List;

@Data
public class GoalroadmapResponse {
    private Integer goalId;
    private String title;

    private double progress;

    private List<MileStoneResponse> milestones;
}
