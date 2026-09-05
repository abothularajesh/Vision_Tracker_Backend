package com.rajesh.Vision_Tracker_GoalOS.auth.repository;

import com.rajesh.Vision_Tracker_GoalOS.auth.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepo extends JpaRepository<Goal, Integer> {
    List<Goal> findByUserId(Integer userId);

}
