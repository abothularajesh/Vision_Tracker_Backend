package com.rajesh.Vision_Tracker_GoalOS.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "mile_stones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MileStones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "goal_id")
    private Goal goal;
    private String title;
    private String description;
    private Integer orderNumber;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "mileStones")
    private List<Tasks> tasks;

}
