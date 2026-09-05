package com.rajesh.Vision_Tracker_GoalOS.auth.service;

import com.rajesh.Vision_Tracker_GoalOS.auth.dto.NotesDTO;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.Notes;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.User;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.NotesRepo;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotesService {

    @Autowired
    private NotesRepo notesRepo;

    @Autowired
    private UserRepo userRepo;

    public List<NotesDTO> getNotesByUserId(Integer id) {
        List<Notes> notesList=notesRepo.findByUserId(id);
        return notesList.stream()
                .map(notes ->
                        new NotesDTO(
                                notes.getId(),
                                notes.getTitle(),
                                notes.getContent(),
                                notes.getCreatedAt(),
                                notes.getUpdatedAt()
                        )).toList();
    }

    public NotesDTO addNotes(Notes notes) {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        User user=userRepo.findByusername(username).orElseThrow(()->
                new UsernameNotFoundException("User Not Found !"));
        notes.setUser(user);

        notesRepo.save(notes);
        NotesDTO dto = new NotesDTO();

        dto.setId(notes.getId());
        dto.setTitle(notes.getTitle());
        dto.setContent(notes.getContent());
        dto.setCreatedAt(notes.getCreatedAt());
        dto.setUpdatedAt(notes.getUpdatedAt());
        return dto;
    }

    public NotesDTO getNotesByid(Integer id) {
        return notesRepo.findById(id)
                .map(notes -> new NotesDTO(
                        notes.getId(),
                        notes.getTitle(),
                        notes.getContent(),
                        notes.getCreatedAt(),
                        notes.getUpdatedAt()
                )).orElseThrow(()->new RuntimeException("Notes Not Found With ID "+id));
    }

    public void updateNotes(Integer id, Notes notes) {
        Notes note=notesRepo.findById(id).orElseThrow(()->new RuntimeException("Notes Not Found With ID: "+ id));
        note.setTitle(notes.getTitle());
        note.setContent(notes.getContent());
        LocalDateTime dateTime=LocalDateTime.now();
        note.setUpdatedAt(dateTime);
        note.setCreatedAt(note.getCreatedAt());

        notesRepo.save(note);
    }

    public void deleteNotes(Integer id) {
        notesRepo.deleteById(id);
    }


    public List<NotesDTO> searchByKeyword(Integer id, String keyword) {
        List<Notes>notes=notesRepo.searchNotes(id, keyword);
        return notes.stream()
                .map(note->new NotesDTO(
                        note.getId(),
                        note.getTitle(),
                        note.getContent(),
                        note.getCreatedAt(),
                        note.getUpdatedAt()
                )).toList();
    }
}
