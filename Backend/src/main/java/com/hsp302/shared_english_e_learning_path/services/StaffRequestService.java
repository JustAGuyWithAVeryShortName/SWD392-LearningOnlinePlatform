package com.hsp302.shared_english_e_learning_path.services;

import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.StaffRequestResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.StaffRequest;
import com.hsp302.shared_english_e_learning_path.domain.entities.User;
import com.hsp302.shared_english_e_learning_path.domain.enums.RequestStatus;
import com.hsp302.shared_english_e_learning_path.domain.enums.Role;
import com.hsp302.shared_english_e_learning_path.exception.ResourceNotFoundException;
import com.hsp302.shared_english_e_learning_path.repositories.StaffRequestRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StaffRequestService {

    private final StaffRequestRepository staffRequestRepository;
    private final UserService userService;

    // MEMBER đăng ký làm staff
    @Transactional
    public void createRequest() {

        User currentUser = userService.getCurrentUser(); // 🔥 LẤY USER ĐANG LOGIN

        if (currentUser.getRole() != Role.MEMBER) {
            throw new IllegalStateException("Only MEMBER can request staff role");
        }

        boolean exists = staffRequestRepository
                .existsByUser_UsernameAndStatus(
                        currentUser.getUsername(),
                        RequestStatus.PENDING
                );

        if (exists) {
            throw new IllegalStateException("You already have a pending staff request");
        }

        StaffRequest request = StaffRequest.builder()
                .user(currentUser)
                .status(RequestStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        staffRequestRepository.save(request);
    }

    // MANAGER xem pending
    @Transactional(readOnly = true)
    public List<StaffRequestResponse> getPendingRequests() {

        return staffRequestRepository.findByStatus(RequestStatus.PENDING)
                .stream()
                .map(sr -> new StaffRequestResponse(
                        sr.getId(),
                        sr.getUser().getUsername(),   // resolve proxy trong TX
                        sr.getUser().getEmail(),
                        sr.getStatus(),
                        sr.getCreatedAt()
                ))
                .toList();
    }

    // MANAGER duyệt
    @Transactional
    public void approve(UUID id) {
        StaffRequest request = findPending(id);

        request.setStatus(RequestStatus.APPROVED);
        request.setReviewedAt(Instant.now());
        request.getUser().setRole(Role.STAFF);
    }

    // MANAGER từ chối
    @Transactional
    public void reject(UUID id, String note) {
        StaffRequest request = findPending(id);

        request.setStatus(RequestStatus.REJECTED);
        request.setReviewedAt(Instant.now());
        request.setAdminNote(note);
    }

    private StaffRequest findPending(UUID id) {
        return staffRequestRepository
                .findByIdAndStatus(id, RequestStatus.PENDING)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Staff request not found or already processed"));
    }
}