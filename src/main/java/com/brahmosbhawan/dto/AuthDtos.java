package com.brahmosbhawan.dto;

import com.brahmosbhawan.entity.HostelBlock;
import com.brahmosbhawan.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public static class LoginRequest {
        @NotBlank(message = "Email or Student ID is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class RegisterRequest {
        @NotBlank(message = "Student ID is required")
        private String studentId;

        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        @NotBlank(message = "Room Number is required")
        private String roomNumber;

        private HostelBlock block = HostelBlock.A_BLOCK;

        private Role role = Role.ROLE_STUDENT;

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
    }

    public static class AuthResponse {
        private String token;
        private String tokenType = "Bearer";
        private UserDto user;

        public AuthResponse(String token, UserDto user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        public String getTokenType() {
            return tokenType;
        }

        public UserDto getUser() {
            return user;
        }
    }

    public static class UserDto {
        private Long id;
        private String studentId;
        private String name;
        private String email;
        private String roomNumber;
        private String block;
        private String role;

        public UserDto() {}

        public UserDto(Long id, String studentId, String name, String email, String roomNumber, String block, String role) {
            this.id = id;
            this.studentId = studentId;
            this.name = name;
            this.email = email;
            this.roomNumber = roomNumber;
            this.block = block;
            this.role = role;
        }

        public Long getId() {
            return id;
        }

        public String getStudentId() {
            return studentId;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        public String getBlock() {
            return block;
        }

        public String getRole() {
            return role;
        }
    }
}
