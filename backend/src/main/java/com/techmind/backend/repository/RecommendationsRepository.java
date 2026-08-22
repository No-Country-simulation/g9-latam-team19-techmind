package com.techmind.backend.repository;

import com.techmind.backend.entity.Recomendacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RecommendationsRepository extends JpaRepository<Recomendacion, Long> {

    // Método para verificar si la recomendación ya existe en BD mediante su ID de Data Science
    Optional<Recomendacion> findByExternalId(Long externalId);

    // Métodos para verificar si el valor existe en el catálogo activo
    List<Recomendacion> findByLanguageIgnoreCaseAndActivoTrue(String language);
    boolean existsByLanguageIgnoreCaseAndActivoTrue(String language);

    List<Recomendacion> findByLevelIgnoreCaseAndActivoTrue(String level);
    boolean existsByLevelIgnoreCaseAndActivoTrue(String level);

    List<Recomendacion> findByCategoryRecsIgnoreCaseAndActivoTrue(String categoryRecs);
    boolean existsByCategoryRecsIgnoreCaseAndActivoTrue(String categoryRecs);

    List<Recomendacion> findByTypeIgnoreCaseAndActivoTrue(String type);
    boolean existsByTypeIgnoreCaseAndActivoTrue(String type);
}
