package com.rajesh.Vision_Tracker_GoalOS.auth.dto;

import com.rajesh.Vision_Tracker_GoalOS.auth.utils.CurrentLevel;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GoalRequestDTO {

    private String title;
    private String description;
    private LocalDate targetDate;
    private CurrentLevel currentLevel;
}
