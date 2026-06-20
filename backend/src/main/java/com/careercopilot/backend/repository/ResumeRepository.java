package com.careercopilot.backend.repository;

import com.careercopilot.backend.entity.ResumeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository
        extends JpaRepository<ResumeEntity, Long> {

}