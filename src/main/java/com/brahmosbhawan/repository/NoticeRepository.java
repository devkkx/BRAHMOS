package com.brahmosbhawan.repository;

import com.brahmosbhawan.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByActiveTrueOrderByCreatedAtDesc();

    List<Notice> findByCategoryAndActiveTrueOrderByCreatedAtDesc(String category);
}
