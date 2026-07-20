package com.imperium.ims.repository;

import com.imperium.ims.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    org.springframework.data.domain.Page<LoginHistory> findByUserIdOrderByLoginTimeDesc(Long userId,
            org.springframework.data.domain.Pageable pageable);
}
