package com.FindersKeepers.backend.repository;


import com.FindersKeepers.backend.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String username);

    List<Users> findAllByIdAfter(Long id);

}
