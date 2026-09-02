package com.brahmosbhawan.controller;

import com.brahmosbhawan.dto.NoticeDtos;
import com.brahmosbhawan.security.UserPrincipal;
import com.brahmosbhawan.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    // Public / Student View
    @GetMapping("/notices")
    public ResponseEntity<List<NoticeDtos.NoticeResponse>> getActiveNotices() {
        return ResponseEntity.ok(noticeService.getAllActiveNotices());
    }

    // Admin Notice Controls
    @PostMapping("/admin/notices")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NoticeDtos.NoticeResponse> createNotice(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody NoticeDtos.NoticeRequest request) {
        NoticeDtos.NoticeResponse response = noticeService.createNotice(currentUser, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/notices/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }
}
