package com.hsp302.shared_english_e_learning_path.repositories;

import com.hsp302.shared_english_e_learning_path.domain.entities.Qualification;
import com.hsp302.shared_english_e_learning_path.domain.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QualificationRepository extends JpaRepository<Qualification, UUID> {

    List<Qualification> findByConsultantUsernameAndStatusOrderByYearDesc(String username, CourseStatus courseStatus);
}
