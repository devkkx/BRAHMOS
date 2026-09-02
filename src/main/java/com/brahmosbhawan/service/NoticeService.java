package com.brahmosbhawan.service;

import com.brahmosbhawan.dto.NoticeDtos;
import com.brahmosbhawan.entity.Notice;
import com.brahmosbhawan.entity.User;
import com.brahmosbhawan.exception.CustomExceptions;
import com.brahmosbhawan.repository.NoticeRepository;
import com.brahmosbhawan.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final AuthService authService;

    public NoticeService(NoticeRepository noticeRepository, AuthService authService) {
        this.noticeRepository = noticeRepository;
        this.authService = authService;
    }

    @Transactional
    public NoticeDtos.NoticeResponse createNotice(UserPrincipal currentUser, NoticeDtos.NoticeRequest request) {
        User admin = authService.getUserByPrincipal(currentUser);
        
        Notice notice = new Notice(
                request.getTitle(),
                request.getContent(),
                request.getCategory() != null ? request.getCategory() : "GENERAL",
                request.getPriority() != null ? request.getPriority() : "NORMAL",
                request.getImageUrl(),
                admin.getName() + " (Warden)"
        );

        Notice saved = noticeRepository.save(notice);
        return convertToDto(saved);
    }

    public List<NoticeDtos.NoticeResponse> getAllActiveNotices() {
        List<Notice> notices = noticeRepository.findByActiveTrueOrderByCreatedAtDesc();
        return notices.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Notice not found with ID: " + id));
        notice.setActive(false);
        noticeRepository.save(notice);
    }

    public NoticeDtos.NoticeResponse convertToDto(Notice n) {
        return new NoticeDtos.NoticeResponse(
                n.getId(),
                n.getTitle(),
                n.getContent(),
                n.getCategory(),
                n.getPriority(),
                n.getImageUrl(),
                n.getPostedBy(),
                n.getCreatedAt(),
                n.isActive()
        );
    }
}
