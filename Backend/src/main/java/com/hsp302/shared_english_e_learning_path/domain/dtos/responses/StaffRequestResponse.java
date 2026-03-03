package com.hsp302.shared_english_e_learning_path.domain.dtos.responses;

import com.hsp302.shared_english_e_learning_path.domain.enums.RequestStatus;

import java.time.Instant;
import java.util.UUID;

public record StaffRequestResponse(
        UUID id,
        String username,
        String email,
        RequestStatus status,
        Instant createdAt
) {}