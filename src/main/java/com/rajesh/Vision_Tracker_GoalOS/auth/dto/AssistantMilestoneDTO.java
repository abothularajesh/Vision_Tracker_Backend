package com.rajesh.Vision_Tracker_GoalOS.auth.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssistantMilestoneDTO {
    private String title;
    private List<AssistantTaskDTO>tasks;
}
