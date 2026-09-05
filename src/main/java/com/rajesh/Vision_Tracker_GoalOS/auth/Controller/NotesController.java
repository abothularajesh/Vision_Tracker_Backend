package com.rajesh.Vision_Tracker_GoalOS.auth.Controller;

import com.rajesh.Vision_Tracker_GoalOS.auth.dto.NotesDTO;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.Notes;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.User;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.UserRepo;
import com.rajesh.Vision_Tracker_GoalOS.auth.service.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api")
public class NotesController {

    @Autowired
    private NotesService notesService;

    @Autowired
    private UserRepo userRepo;

    @GetMapping("notes")
    public ResponseEntity<List<NotesDTO>> getNotes(){

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        String username = authentication.getName();

        User user = userRepo.findByusername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found Name: " + username));

        Integer userId = user.getId();

        return ResponseEntity.ok(notesService.getNotesByUserId(userId));
    }

    @PostMapping("notes")
    public ResponseEntity<NotesDTO> addNotes(@RequestBody Notes notes){
        return ResponseEntity.ok(notesService.addNotes(notes));
    }

    @PostMapping("notes/{id}")
    public ResponseEntity<NotesDTO> getNotesById(@PathVariable Integer id){
        return ResponseEntity.ok(notesService.getNotesByid(id));
    }

    @PutMapping("notes/{id}")
    public ResponseEntity<String> UpdateNotes(@PathVariable Integer id, @RequestBody Notes notes){
        notesService.updateNotes(id, notes);
        return ResponseEntity.ok("Updated Successfully");
    }

    @DeleteMapping("notes/{id}")
    public ResponseEntity<String> deleteNotes(@PathVariable Integer id){
        notesService.deleteNotes(id);
        return ResponseEntity.ok("Deleted Notes With id: "+id);
    }

    @GetMapping("notes/search")
    public ResponseEntity<List<NotesDTO>> searchByKeyword(@RequestParam String keyword){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        User user=userRepo.findByusername(username).orElseThrow(()->
                new UsernameNotFoundException("User Not Found Name: "+username));
        Integer id= user.getId();
        return ResponseEntity.ok(notesService.searchByKeyword(id, keyword));
    }

}
