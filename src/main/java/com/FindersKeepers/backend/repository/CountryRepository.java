package com.FindersKeepers.backend.repository;


import com.FindersKeepers.backend.model.Country;
import com.FindersKeepers.backend.projections.GetAllCountryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CountryRepository extends JpaRepository<Country, Integer> {

    @Query(value = "Select NEW com.FindersKeepers.backend.projections.GetAllCountryProjection(" +
            "a.id, " +
            "a.name," +
            " a.code) from Country a where LOWER(a.name) LIKE CONCAT('%', LOWER(:name), '%')")
    List<GetAllCountryProjection> getAll(@Param(value = "name") String name);
}
