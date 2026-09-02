package com.brahmosbhawan.service;

import com.brahmosbhawan.dto.ComplaintDtos;
import com.brahmosbhawan.entity.Complaint;
import com.brahmosbhawan.entity.ComplaintCategory;
import com.brahmosbhawan.entity.ComplaintStatus;
import com.brahmosbhawan.entity.PriorityLevel;
import com.brahmosbhawan.entity.User;
import com.brahmosbhawan.exception.CustomExceptions;
import com.brahmosbhawan.repository.ComplaintRepository;
import com.brahmosbhawan.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final AuthService authService;

    public ComplaintService(ComplaintRepository complaintRepository, AuthService authService) {
        this.complaintRepository = complaintRepository;
        this.authService = authService;
    }

    @Transactional
    public ComplaintDtos.ComplaintResponse createComplaint(UserPrincipal currentUser, ComplaintDtos.ComplaintRequest request) {
        User user = authService.getUserByPrincipal(currentUser);
        Complaint complaint = new Complaint(
                user,
                request.getCategory(),
                request.getTitle(),
                request.getDescription(),
                request.getPriority() != null ? request.getPriority() : PriorityLevel.MEDIUM,
                request.getImageUrl()
        );

        Complaint saved = complaintRepository.save(complaint);
        return convertToDto(saved);
    }

    public List<ComplaintDtos.ComplaintResponse> getStudentComplaints(UserPrincipal currentUser) {
        User user = authService.getUserByPrincipal(currentUser);
        // STRICT PRIVACY RULE: Student receives ONLY their own complaints
        List<Complaint> complaints = complaintRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return complaints.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public ComplaintDtos.ComplaintResponse getStudentComplaintById(UserPrincipal currentUser, Long id) {
        User user = authService.getUserByPrincipal(currentUser);
        // Ownership Check
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Complaint not found with ID: " + id));

        if (!complaint.getUser().getId().equals(user.getId()) && !currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new CustomExceptions.UnauthorizedAccessException("Access denied. You can only view your own complaints.");
        }

        return convertToDto(complaint);
    }

    // Admin Operations
    public List<ComplaintDtos.ComplaintResponse> getAdminFilteredComplaints(ComplaintStatus status, ComplaintCategory category, PriorityLevel priority, String searchTerm) {
        List<Complaint> complaints = complaintRepository.filterComplaints(status, category, priority, searchTerm);
        return complaints.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public ComplaintDtos.ComplaintResponse updateComplaintStatus(Long id, ComplaintDtos.ComplaintStatusUpdateRequest request) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Complaint not found with ID: " + id));

        complaint.setStatus(request.getStatus());
        if (request.getAdminRemark() != null) {
            complaint.setAdminRemark(request.getAdminRemark());
        }

        Complaint updated = complaintRepository.save(complaint);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteComplaint(Long id) {
        if (!complaintRepository.existsById(id)) {
            throw new CustomExceptions.ResourceNotFoundException("Complaint not found with ID: " + id);
        }
        complaintRepository.deleteById(id);
    }

    public Map<String, Long> getComplaintStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", complaintRepository.count());
        stats.put("pending", complaintRepository.countByStatus(ComplaintStatus.PENDING));
        stats.put("inProgress", complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS));
        stats.put("resolved", complaintRepository.countByStatus(ComplaintStatus.RESOLVED));
        stats.put("rejected", complaintRepository.countByStatus(ComplaintStatus.REJECTED));
        return stats;
    }

    public ComplaintDtos.ComplaintResponse convertToDto(Complaint c) {
        return new ComplaintDtos.ComplaintResponse(
                c.getId(),
                c.getUser().getStudentId(),
                c.getUser().getName(),
                c.getUser().getRoomNumber(),
                c.getUser().getBlock() != null ? c.getUser().getBlock().name() : "A_BLOCK",
                c.getCategory(),
                c.getTitle(),
                c.getDescription(),
                c.getPriority(),
                c.getStatus(),
                c.getImageUrl(),
                c.getAdminRemark(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}
