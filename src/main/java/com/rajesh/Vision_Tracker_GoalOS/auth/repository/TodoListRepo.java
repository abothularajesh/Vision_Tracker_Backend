package com.rajesh.Vision_Tracker_GoalOS.auth.repository;

import com.rajesh.Vision_Tracker_GoalOS.auth.entity.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoListRepo extends JpaRepository<TodoList, Integer> {

    List<TodoList> findByUserId(Integer userId);

    Optional<TodoList> findByIdAndUserId(Integer id, Integer userId);
}
