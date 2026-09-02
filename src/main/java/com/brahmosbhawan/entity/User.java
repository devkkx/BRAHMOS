package com.brahmosbhawan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true, length = 50)
    private String studentId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HostelBlock block = HostelBlock.A_BLOCK;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.ROLE_STUDENT;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public User() {
    }

    public User(String studentId, String name, String email, String password, String roomNumber, HostelBlock block, Role role) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roomNumber = roomNumber;
        this.block = block != null ? block : HostelBlock.A_BLOCK;
        this.role = role != null ? role : Role.ROLE_STUDENT;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
