package com.hsp302.shared_english_e_learning_path.repositories;

import com.hsp302.shared_english_e_learning_path.domain.entities.Progress;
import com.hsp302.shared_english_e_learning_path.domain.enums.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, UUID> {
    List<Progress> findByEnrollmentEnrollmentID(UUID enrollmentID);

    List<Progress> findByEnrollmentEnrollmentIDAndStatus(UUID enrollmentID, ProgressStatus progressStatus);

    Progress findByEnrollmentEnrollmentIDAndLessonID(UUID enrollmentID, UUID lessonID);

//    List<Progress> findByEnrollmentEnrollmentIDAndLessonID(UUID enrollmentID, UUID lessonID);
}
