package com.rajesh.Vision_Tracker_GoalOS.auth.repository;

import com.rajesh.Vision_Tracker_GoalOS.auth.entity.MileStones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MileStoneRepo extends JpaRepository<MileStones, Integer> {

}
