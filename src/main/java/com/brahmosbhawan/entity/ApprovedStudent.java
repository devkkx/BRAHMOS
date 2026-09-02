package com.brahmosbhawan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "approved_students")
public class ApprovedStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true, length = 50)
    private String studentId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "room_number", length = 20)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private HostelBlock block = HostelBlock.A_BLOCK;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ApprovedStudent() {
    }

    public ApprovedStudent(String studentId, String name, String email, String roomNumber, HostelBlock block) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.roomNumber = roomNumber;
        this.block = block != null ? block : HostelBlock.A_BLOCK;
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

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public HostelBlock getBlock() {
        return block;
    }

    public void setBlock(HostelBlock block) {
        this.block = block;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
