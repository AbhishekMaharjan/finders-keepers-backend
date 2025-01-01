package com.FindersKeepers.backend.repository;

import com.FindersKeepers.backend.model.ItemCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Long> {

    Page<ItemCategory> findAllByNameContainingIgnoreCaseAndParentCategoryIsNull(Pageable pageable, String name);

    List<ItemCategory> findAllByParentCategoryId(Long parentCategoryId);
}
