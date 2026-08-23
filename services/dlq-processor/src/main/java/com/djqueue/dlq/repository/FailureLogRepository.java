package com.djqueue.dlq.repository;

import com.djqueue.dlq.infrastructure.db.FailureLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailureLogRepository extends JpaRepository<FailureLog, Long> {
}