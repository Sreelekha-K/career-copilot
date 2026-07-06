package com.careercopilot.backend.repository;

import com.careercopilot.backend.entity.ResumeEntity;
import com.careercopilot.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository
        extends JpaRepository<ResumeEntity, Long> {

    List<ResumeEntity> findByOwnerOrderByUploadedAtDesc(User owner);
}
