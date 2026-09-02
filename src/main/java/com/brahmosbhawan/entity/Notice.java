package com.brahmosbhawan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 50)
    private String category = "GENERAL"; // GENERAL, MESS, MAINTENANCE, URGENT, EVENT

    @Column(length = 20)
    private String priority = "NORMAL"; // NORMAL, IMPORTANT, URGENT

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "posted_by", length = 100)
    private String postedBy = "Hostel Warden";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean active = true;

    public Notice() {
    }

    public Notice(String title, String content, String category, String priority, String imageUrl, String postedBy) {
        this.title = title;
        this.content = content;
        this.category = category != null ? category : "GENERAL";
        this.priority = priority != null ? priority : "NORMAL";
        this.imageUrl = imageUrl;
        this.postedBy = postedBy != null ? postedBy : "Hostel Warden";
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters & Setters
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
