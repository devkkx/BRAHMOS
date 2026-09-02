package com.brahmosbhawan.repository;

import com.brahmosbhawan.entity.Complaint;
import com.brahmosbhawan.entity.ComplaintCategory;
import com.brahmosbhawan.entity.ComplaintStatus;
import com.brahmosbhawan.entity.PriorityLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Complaint> findByIdAndUserId(Long id, Long userId);

    long countByStatus(ComplaintStatus status);

    List<Complaint> findAllByOrderByCreatedAtDesc();

    @Query("SELECT c FROM Complaint c WHERE " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:category IS NULL OR c.category = :category) AND " +
           "(:priority IS NULL OR c.priority = :priority) AND " +
           "(:searchTerm IS NULL OR LOWER(c.user.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.user.roomNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY c.createdAt DESC")
    List<Complaint> filterComplaints(
            @Param("status") ComplaintStatus status,
            @Param("category") ComplaintCategory category,
            @Param("priority") PriorityLevel priority,
            @Param("searchTerm") String searchTerm
    );
}
