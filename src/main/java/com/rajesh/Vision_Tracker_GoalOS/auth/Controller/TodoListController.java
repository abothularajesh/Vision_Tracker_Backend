package com.rajesh.Vision_Tracker_GoalOS.auth.Controller;

import com.rajesh.Vision_Tracker_GoalOS.auth.dto.TodoDTO;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.TodoList;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.User;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.TodoListRepo;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.UserRepo;
import com.rajesh.Vision_Tracker_GoalOS.auth.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("api")
public class TodoListController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserRepo userRepo;

    @GetMapping("todos")
    public ResponseEntity<List<TodoDTO>> getTasks(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        String username=authentication.getName();
        User user=userRepo.findByusername(username).orElseThrow(()->
                new UsernameNotFoundException("Username NotFound: "+username));
        Integer userId=user.getId();
        return ResponseEntity.ok(todoService.getTasksById(userId));
    }

    @PostMapping("todos")
    public ResponseEntity<TodoDTO> addTasks(@RequestBody TodoList tasks) {

        TodoList savedTask = todoService.addTasks(tasks);

        TodoDTO todoDTO = new TodoDTO();

        todoDTO.setId(savedTask.getId());
        todoDTO.setTitle(savedTask.getTitle());
        todoDTO.setStatus(savedTask.getStatus());
        todoDTO.setCreatedAt(savedTask.getCreatedAt());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(todoDTO);
    }

    @PutMapping("todos/{id}")
    public ResponseEntity<String> updateTasks(@PathVariable Integer id, @RequestBody TodoList todoList){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        String username=authentication.getName();
        User user=userRepo.findByusername(username).orElseThrow(()->
                new UsernameNotFoundException("Username NotFound: "+username));
        Integer userId=user.getId();
        todoService.updateTodoList(id, userId, todoList);
        return ResponseEntity.ok("TodoList Updated success");
    }

    @DeleteMapping("todos/{id}")
    public ResponseEntity<String> deleteTasks(@PathVariable Integer id){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        String username= authentication.getName();
        User user=userRepo.findByusername(username).orElseThrow(()->
                new UsernameNotFoundException("UserName NotFound: "+username));
        Integer userId=user.getId();
        todoService.deleteTasks(id,userId);
        return ResponseEntity.ok("Task Deleted");
    }
}
