package com.rajesh.Vision_Tracker_GoalOS.auth.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GoalResponseDTO {

    private Integer id;
    private String title;
    private String Description;
    private LocalDateTime targetDate;
    private String currentLevel;
    private String status;
    private double progress;
}
