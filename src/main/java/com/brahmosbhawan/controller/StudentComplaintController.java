package com.brahmosbhawan.controller;

import com.brahmosbhawan.dto.ComplaintDtos;
import com.brahmosbhawan.security.UserPrincipal;
import com.brahmosbhawan.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/complaints")
public class StudentComplaintController {

    private final ComplaintService complaintService;

    public StudentComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public ResponseEntity<ComplaintDtos.ComplaintResponse> createComplaint(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ComplaintDtos.ComplaintRequest request) {
        ComplaintDtos.ComplaintResponse response = complaintService.createComplaint(currentUser, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ComplaintDtos.ComplaintResponse>> getMyComplaints(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<ComplaintDtos.ComplaintResponse> list = complaintService.getStudentComplaints(currentUser);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintDtos.ComplaintResponse> getComplaintById(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        ComplaintDtos.ComplaintResponse complaint = complaintService.getStudentComplaintById(currentUser, id);
        return ResponseEntity.ok(complaint);
    }
}
