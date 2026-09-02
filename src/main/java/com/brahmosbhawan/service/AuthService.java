package com.brahmosbhawan.service;

import com.brahmosbhawan.dto.AuthDtos;
import com.brahmosbhawan.entity.HostelBlock;
import com.brahmosbhawan.entity.Role;
import com.brahmosbhawan.entity.User;
import com.brahmosbhawan.exception.CustomExceptions;
import com.brahmosbhawan.repository.ApprovedStudentRepository;
import com.brahmosbhawan.repository.UserRepository;
import com.brahmosbhawan.security.JwtTokenProvider;
import com.brahmosbhawan.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ApprovedStudentRepository approvedStudentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository, ApprovedStudentRepository approvedStudentRepository,
                       PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.approvedStudentRepository = approvedStudentRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        String cleanEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        String cleanName = request.getName() != null ? request.getName().trim() : "";

        if (userRepository.existsByEmail(cleanEmail)) {
            throw new CustomExceptions.BadRequestException("Email address is already in use!");
        }

        if (userRepository.existsByStudentId(request.getStudentId())) {
            throw new CustomExceptions.BadRequestException("Student ID is already registered!");
        }

        // Whitelist Verification: Matches Name (case-insensitive) AND Email (must be lowercase)
        if (approvedStudentRepository.count() > 0 && request.getRole() == Role.ROLE_STUDENT) {
            boolean approved = approvedStudentRepository.isPreApprovedByNameAndEmail(cleanName, cleanEmail);
            if (!approved) {
                throw new CustomExceptions.UnauthorizedAccessException(
                        "Registration Denied. Your Name and Email (" + cleanEmail + ") do not match any pre-approved boarder record uploaded by the Warden/Admin. (Note: Email must be lowercase)."
                );
            }
        }

        User user = new User(
                request.getStudentId(),
                cleanName,
                cleanEmail,
                passwordEncoder.encode(request.getPassword()),
                request.getRoomNumber(),
                request.getBlock() != null ? request.getBlock() : HostelBlock.A_BLOCK,
                request.getRole() != null ? request.getRole() : Role.ROLE_STUDENT
        );

        userRepository.save(user);

        // Authenticate immediately after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(cleanEmail, request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);
        AuthDtos.UserDto userDto = convertToDto(user);

        return new AuthDtos.AuthResponse(token, userDto);
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        String loginUsername = request.getUsername() != null ? request.getUsername().trim().toLowerCase() : "";
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUsername, request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("User not found"));

        AuthDtos.UserDto userDto = convertToDto(user);
        return new AuthDtos.AuthResponse(token, userDto);
    }

    public AuthDtos.UserDto getCurrentUserDto(UserPrincipal principal) {
        User user = userRepository.findByEmail(principal.getEmail())
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("User not found"));
        return convertToDto(user);
    }

    public User getUserByPrincipal(UserPrincipal principal) {
        return userRepository.findByEmail(principal.getEmail())
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("User not found"));
    }

    public AuthDtos.UserDto convertToDto(User user) {
        return new AuthDtos.UserDto(
                user.getId(),
                user.getStudentId(),
                user.getName(),
                user.getEmail(),
                user.getRoomNumber(),
                user.getBlock() != null ? user.getBlock().name() : "A_BLOCK",
                user.getRole().name()
        );
    }
}
