package com.rajesh.Vision_Tracker_GoalOS.auth.repository;

import com.rajesh.Vision_Tracker_GoalOS.auth.entity.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotesRepo extends JpaRepository<Notes, Integer> {

    List<Notes> findByUserId(Integer userId);

    List<Notes> findByUserIdAndTitleContainingIgnoreCaseOrUserIdAndContentContainingIgnoreCase(
            Integer userId1,
            String titleKeyword,
            Integer userId2,
            String contentKeyword
    );

    @Query("""
    SELECT n FROM Notes n
    WHERE n.user.id = :userId
    AND (
        LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
""")
    List<Notes> searchNotes(
            @Param("userId") Integer userId,
            @Param("keyword") String keyword
    );
}
