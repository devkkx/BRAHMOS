package com.brahmosbhawan.controller;

import com.brahmosbhawan.dto.ComplaintDtos;
import com.brahmosbhawan.entity.ComplaintCategory;
import com.brahmosbhawan.entity.ComplaintStatus;
import com.brahmosbhawan.entity.PriorityLevel;
import com.brahmosbhawan.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/complaints")
public class AdminComplaintController {

    private final ComplaintService complaintService;

    public AdminComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping
    public ResponseEntity<List<ComplaintDtos.ComplaintResponse>> getAllComplaints(
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) ComplaintCategory category,
            @RequestParam(required = false) PriorityLevel priority,
            @RequestParam(required = false) String search) {
        List<ComplaintDtos.ComplaintResponse> list = complaintService.getAdminFilteredComplaints(status, category, priority, search);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getComplaintStats() {
        Map<String, Long> stats = complaintService.getComplaintStatistics();
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ComplaintDtos.ComplaintResponse> updateComplaintStatus(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintDtos.ComplaintStatusUpdateRequest request) {
        ComplaintDtos.ComplaintResponse updated = complaintService.updateComplaintStatus(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.noContent().build();
    }
}
