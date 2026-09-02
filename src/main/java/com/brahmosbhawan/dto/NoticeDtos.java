package com.brahmosbhawan.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class NoticeDtos {

    public static class NoticeRequest {
        @NotBlank(message = "Notice title is required")
        private String title;

        @NotBlank(message = "Notice content is required")
        private String content;

        private String category;
        private String priority;
        private String imageUrl;

        public NoticeRequest() {
        }

        public NoticeRequest(String title, String content, String category, String priority, String imageUrl) {
            this.title = title;
            this.content = content;
            this.category = category;
            this.priority = priority;
            this.imageUrl = imageUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    public static class NoticeResponse {
        private Long id;
        private String title;
        private String content;
        private String category;
        private String priority;
        private String imageUrl;
        private String postedBy;
        private LocalDateTime createdAt;
        private boolean active;

        public NoticeResponse() {
        }

        public NoticeResponse(Long id, String title, String content, String category, String priority,
                              String imageUrl, String postedBy, LocalDateTime createdAt, boolean active) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.category = category;
            this.priority = priority;
            this.imageUrl = imageUrl;
            this.postedBy = postedBy;
            this.createdAt = createdAt;
            this.active = active;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getPostedBy() {
            return postedBy;
        }

        public void setPostedBy(String postedBy) {
            this.postedBy = postedBy;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}
