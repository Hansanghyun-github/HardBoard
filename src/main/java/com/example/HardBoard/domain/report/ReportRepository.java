package com.example.HardBoard.domain.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByStatusAndTargetId(TargetStatus status, Long targetId);

    Page<Report> findByUserId(Long userId, Pageable pageable);
}
