package com.hsp302.shared_english_e_learning_path.repositories;

import com.hsp302.shared_english_e_learning_path.domain.entities.Blog;
import com.hsp302.shared_english_e_learning_path.domain.enums.AgeGroup;
import com.hsp302.shared_english_e_learning_path.domain.enums.BlogStatus;
import com.hsp302.shared_english_e_learning_path.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface BlogRepository extends JpaRepository<Blog, UUID> {

    List<Blog> findByMemberUsernameAndBlogStatusOrderByCreatedAtDesc(String username, BlogStatus blogStatus);

    List<Blog> findByBlogStatusOrderByCreatedAtDesc(BlogStatus status);

    List<Blog> findByBlogStatusAndCreatedAtBetween(BlogStatus status, Instant start, Instant end);

    List<Blog> findByAgeGroupAndBlogStatusOrderByCreatedAtDesc(AgeGroup ageGroup, BlogStatus blogStatus);

    List<Blog> findByMemberRoleOrderByCreatedAtDesc(Role role);

    long countByBlogStatus(BlogStatus status);

    @Query("SELECT COUNT(b) FROM Blog b WHERE YEAR(b.createdAt) = :year AND MONTH(b.createdAt) = :month")
    int countBlogsByMonth(@Param("year") int year, @Param("month") int month);

    long count();
}
