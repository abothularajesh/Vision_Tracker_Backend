package com.rajesh.Vision_Tracker_GoalOS.auth.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssistantGoalDTO {
    private String title;

    private String description;

    private String status;

    private String currentLevel;

    private LocalDate targetDate;

    private double progress;

    private List<AssistantMilestoneDTO> milestones;
}
