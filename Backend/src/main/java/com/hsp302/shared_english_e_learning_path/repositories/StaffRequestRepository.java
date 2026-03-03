package com.hsp302.shared_english_e_learning_path.repositories;


import com.hsp302.shared_english_e_learning_path.domain.entities.StaffRequest;
import com.hsp302.shared_english_e_learning_path.domain.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffRequestRepository
        extends JpaRepository<StaffRequest, UUID> {

    Optional<StaffRequest> findByIdAndStatus(UUID id, RequestStatus status);

    List<StaffRequest> findByStatus(RequestStatus status);

    boolean existsByUser_UsernameAndStatus(String username, RequestStatus status);
}