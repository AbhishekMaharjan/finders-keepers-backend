package com.FindersKeepers.backend.repository;


import com.FindersKeepers.backend.model.auth.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByClientId(String clientId);

}