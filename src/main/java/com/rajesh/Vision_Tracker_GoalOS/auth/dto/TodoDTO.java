package com.rajesh.Vision_Tracker_GoalOS.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoDTO {

    private Integer id;
    private String title;
    private String status;
    private LocalDateTime createdAt;
}
