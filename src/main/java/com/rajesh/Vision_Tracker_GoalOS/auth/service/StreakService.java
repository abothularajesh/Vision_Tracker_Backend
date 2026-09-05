package com.rajesh.Vision_Tracker_GoalOS.auth.service;

import com.rajesh.Vision_Tracker_GoalOS.auth.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class StreakService {

    public void updateStreak(User user) {

        LocalDate today = LocalDate.now();

        if (user.getLastLoginDate() == null) {
            // First login
            user.setLoginStreak(1);

        } else if (user.getLastLoginDate().equals(today)) {

            // Already logged in today
            // Don't increase streak
        } else if (user.getLastLoginDate().plusDays(1).equals(today)) {

            // Logged in yesterday
            user.setLoginStreak(user.getLoginStreak() + 1);

        } else {
            // Missed one or more days
            user.setLoginStreak(1);
        }
        user.setLastLoginDate(today);
    }
}
