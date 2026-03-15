package com.hsp302.shared_english_e_learning_path.repositories;

import com.hsp302.shared_english_e_learning_path.domain.entities.Course;
import com.hsp302.shared_english_e_learning_path.domain.entities.Enrollment;
import com.hsp302.shared_english_e_learning_path.domain.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    List<Enrollment> findByMemberUsername(String username);

    List<Enrollment> findByCourseCourseID(UUID courseId);

    @Query("SELECT e.course FROM Enrollment e " +
            "WHERE e.status = :status AND e.member.username = :username")
    List<Course> findEnrolledCoursesByStatusAndMember(@Param("status") EnrollmentStatus status,
            @Param("username") String username);

    Enrollment findByMemberUsernameAndCourseCourseID(String username, UUID courseID);

    boolean existsByMemberUsernameAndCourseCourseID(String username, UUID courseID);

    @Query("""
                SELECT e.member.ageGroup, COUNT(DISTINCT e.member.username)
                FROM Enrollment e
                WHERE e.status = 'COMPLETED'
                GROUP BY e.member.ageGroup
            """)
    List<Object[]> getCompletedEnrollmentCountByAgeGroup();
}
