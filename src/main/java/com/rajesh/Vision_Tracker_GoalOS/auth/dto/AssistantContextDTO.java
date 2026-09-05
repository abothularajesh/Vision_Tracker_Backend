package com.rajesh.Vision_Tracker_GoalOS.auth.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssistantContextDTO {

    private List<AssistantGoalDTO>goals;
    private List<AssistantTodosDTO>todoList;
    private double OverAllProgress;
}

