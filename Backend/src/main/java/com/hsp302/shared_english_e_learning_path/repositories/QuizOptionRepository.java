package com.hsp302.shared_english_e_learning_path.repositories;

import com.hsp302.shared_english_e_learning_path.domain.entities.QuizOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuizOptionRepository extends JpaRepository<QuizOption, UUID> {}