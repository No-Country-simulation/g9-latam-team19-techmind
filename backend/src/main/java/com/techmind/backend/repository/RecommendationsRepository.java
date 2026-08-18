package com.techmind.backend.repository;

import com.techmind.backend.entity.Recomendacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationsRepository extends JpaRepository<Recomendacion, Long> {
}
