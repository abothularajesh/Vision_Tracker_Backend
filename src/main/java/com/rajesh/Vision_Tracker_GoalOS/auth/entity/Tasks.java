package com.rajesh.Vision_Tracker_GoalOS.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "milestone_id")
    private MileStones mileStones;
    private String title;
    private String description;
    private String priority;
    private Integer estimated_hours;
    private String status;
    private LocalDateTime due_date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
