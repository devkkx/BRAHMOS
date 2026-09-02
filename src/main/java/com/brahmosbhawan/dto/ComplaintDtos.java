package com.brahmosbhawan.dto;

import com.brahmosbhawan.entity.ComplaintCategory;
import com.brahmosbhawan.entity.ComplaintStatus;
import com.brahmosbhawan.entity.PriorityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ComplaintDtos {

    public static class ComplaintRequest {
        @NotNull(message = "Category is required")
        private ComplaintCategory category;

        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Description is required")
        private String description;

        private PriorityLevel priority = PriorityLevel.MEDIUM;

        private String imageUrl;

        public ComplaintCategory getCategory() {
            return category;
        }

        public void setCategory(ComplaintCategory category) {
            this.category = category;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public PriorityLevel getPriority() {
            return priority;
        }

        public void setPriority(PriorityLevel priority) {
            this.priority = priority;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    public static class ComplaintResponse {
        private Long id;
        private String studentId;
        private String studentName;
        private String roomNumber;
        private String block;
        private ComplaintCategory category;
        private String title;
        private String description;
        private PriorityLevel priority;
        private ComplaintStatus status;
        private String imageUrl;
        private String adminRemark;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ComplaintResponse() {}

        public ComplaintResponse(Long id, String studentId, String studentName, String roomNumber, String block,
                                 ComplaintCategory category, String title, String description,
                                 PriorityLevel priority, ComplaintStatus status, String imageUrl, String adminRemark,
                                 LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.studentId = studentId;
            this.studentName = studentName;
            this.roomNumber = roomNumber;
            this.block = block;
            this.category = category;
            this.title = title;
            this.description = description;
            this.priority = priority;
            this.status = status;
            this.imageUrl = imageUrl;
            this.adminRemark = adminRemark;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public Long getId() {
            return id;
        }

        public String getStudentId() {
            return studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        public String getBlock() {
            return block;
        }

        public ComplaintCategory getCategory() {
            return category;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public PriorityLevel getPriority() {
            return priority;
        }

        public ComplaintStatus getStatus() {
            return status;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getAdminRemark() {
            return adminRemark;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }
    }

    public static class ComplaintStatusUpdateRequest {
        @NotNull(message = "Status is required")
        private ComplaintStatus status;

        private String adminRemark;

        public ComplaintStatus getStatus() {
            return status;
        }

        public void setStatus(ComplaintStatus status) {
            this.status = status;
        }

        public String getAdminRemark() {
            return adminRemark;
        }

        public void setAdminRemark(String adminRemark) {
            this.adminRemark = adminRemark;
        }
    }
}
