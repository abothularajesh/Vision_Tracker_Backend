package com.rajesh.Vision_Tracker_GoalOS.auth.entity;

import com.rajesh.Vision_Tracker_GoalOS.auth.utils.CurrentLevel;
import com.rajesh.Vision_Tracker_GoalOS.auth.utils.GoalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String description;
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    private CurrentLevel currentLevel;

    @Enumerated(EnumType.STRING)
    private GoalStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "goal")
    private List<MileStones> mileStones;

}
