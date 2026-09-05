package com.rajesh.Vision_Tracker_GoalOS.auth.dto;

import com.rajesh.Vision_Tracker_GoalOS.auth.entity.Notes;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotesDTO {

    private Integer id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    //    public NotesDTO(Notes notes) {
//    }
}
