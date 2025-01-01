package com.FindersKeepers.backend.repository;

import com.FindersKeepers.backend.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
