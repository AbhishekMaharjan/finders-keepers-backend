package com.FindersKeepers.backend.repository;

import com.FindersKeepers.backend.model.ItemRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRecordRepository extends JpaRepository<ItemRecord, Long> {
    List<ItemRecord> findAllByClaimUserId(Long claimUserId);
}
