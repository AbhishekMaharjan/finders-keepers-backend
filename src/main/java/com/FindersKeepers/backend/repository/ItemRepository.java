package com.FindersKeepers.backend.repository;

import com.FindersKeepers.backend.model.Item;
import com.FindersKeepers.backend.model.ItemCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByItemStatusAndItemCategoryListContains(String itemStatus, ItemCategory itemCategory, Pageable pageable);

    Page<Item> findAllByItemStatus(String itemStatus, Pageable pageable);

    List<Item> findAllByItemStatusAndUsersId(String itemStatus, Long userId);

}
