package com.FindersKeepers.backend.repository;

import com.FindersKeepers.backend.model.auth.Authorities;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthoritiesRepository extends JpaRepository<Authorities, Long> {
    Optional<Authorities> findByAuthority(String authority);
}
