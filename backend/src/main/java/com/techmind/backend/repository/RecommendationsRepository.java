package com.techmind.backend.repository;

import com.techmind.backend.entity.Recomendacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RecommendationsRepository extends JpaRepository<Recomendacion, Long> {

    List<Recomendacion> findByLanguageIgnoreCaseAndActivoTrue(String language);

    List<Recomendacion> findByLevelIgnoreCaseAndActivoTrue(String level);

    List<Recomendacion> findByCategoryRecsIgnoreCaseAndActivoTrue(String categoryRecs);

    List<Recomendacion> findByTypeIgnoreCaseAndActivoTrue(String type);

}
