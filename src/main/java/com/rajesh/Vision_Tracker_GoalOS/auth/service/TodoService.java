package com.rajesh.Vision_Tracker_GoalOS.auth.service;

import com.rajesh.Vision_Tracker_GoalOS.auth.dto.TodoDTO;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.TodoList;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.User;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.TodoListRepo;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TodoService {

    @Autowired
    private TodoListRepo todoListRepo;

    @Autowired
    private UserRepo userRepo;

    public List<TodoDTO> getTasksById(Integer userId) {

        List<TodoList> todoList = todoListRepo.findByUserId(userId);

        return todoList.stream()
                .map(tasks->new TodoDTO(
                        tasks.getId(),
                        tasks.getTitle(),
                        tasks.getStatus(),
                        tasks.getCreatedAt()
                )).toList();
    }

    public TodoList addTasks(TodoList tasks) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assert authentication != null;
        String username = authentication.getName();

        User user = userRepo.findByusername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User Not Found: " + username
                        )
                );

        tasks.setUser(user);

        LocalDateTime dateTime=LocalDateTime.now();
        tasks.setCreatedAt(dateTime);
        return todoListRepo.save(tasks);
    }


    public TodoList updateTodoList(Integer id,Integer userId, TodoList todoList) {
        TodoList existingTask = todoListRepo.findByIdAndUserId(id,userId)
                .orElseThrow(() ->
                        new RuntimeException("Todo task not found with id: " + id)
                );

        existingTask.setTitle(todoList.getTitle());
        existingTask.setStatus(todoList.getStatus());

        return todoListRepo.save(existingTask);
    }

    public void deleteTasks(Integer id, Integer userId) {
        TodoList todoList = todoListRepo.findByIdAndUserId(id,userId)
                .orElseThrow(() ->
                        new RuntimeException("Todo task not found with id: " + id)
                );
        todoListRepo.delete(todoList);
    }
}
