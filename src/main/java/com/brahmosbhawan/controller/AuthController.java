package com.brahmosbhawan.controller;

import com.brahmosbhawan.dto.AuthDtos;
import com.brahmosbhawan.security.UserPrincipal;
import com.brahmosbhawan.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDtos.AuthResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        AuthDtos.AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        AuthDtos.AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthDtos.UserDto> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        AuthDtos.UserDto userDto = authService.getCurrentUserDto(currentUser);
        return ResponseEntity.ok(userDto);
    }
}
