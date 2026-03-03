package com.hsp302.shared_english_e_learning_path.controllers;

import com.hsp302.shared_english_e_learning_path.services.StaffRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/staff-requests")
@RequiredArgsConstructor
public class StaffRequestController {

    private final StaffRequestService staffRequestService;

    // MEMBER đăng ký làm staff
    @PostMapping
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<?> createRequest() {
        staffRequestService.createRequest();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // MANAGER xem danh sách chờ duyệt
    @GetMapping("/pending")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getPendingRequests() {
        return ResponseEntity.ok(staffRequestService.getPendingRequests());
    }

    // MANAGER duyệt
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> approve(@PathVariable UUID id) {
        staffRequestService.approve(id);
        return ResponseEntity.ok().build();
    }

    // MANAGER từ chối
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> reject(@PathVariable UUID id,
                                    @RequestBody(required = false) String note) {
        staffRequestService.reject(id, note);
        return ResponseEntity.ok().build();
    }
}