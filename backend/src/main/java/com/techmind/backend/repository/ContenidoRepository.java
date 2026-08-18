package com.techmind.backend.repository;

import com.techmind.backend.entity.Contenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContenidoRepository extends JpaRepository<Contenido, Long> {

    // 1. Desactiva el contenido principal
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE contenido SET activo = false WHERE id = :id", nativeQuery = true)
    int desactivarContenidoPorId(@Param("id") Long id);

    // 2. Desactiva la predicción vinculada
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE prediccion SET activo = false WHERE contenido_id = :id", nativeQuery = true)
    int desactivarPrediccionPorContenidoId(@Param("id") Long id);

    // 3. Desactiva todas las keywords asociadas a esa predicción
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE keyword SET activo = false WHERE prediccion_id = (SELECT id FROM prediccion WHERE contenido_id = :id)", nativeQuery = true)
    int desactivarKeywordsPorContenidoId(@Param("id") Long id);

    // 1. Reactiva el contenido principal
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE contenido SET activo = true WHERE id = :id", nativeQuery = true)
    int restaurarContenidoPorId(@Param("id") Long id);

    // 2. Reactiva la predicción vinculada
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE prediccion SET activo = true WHERE contenido_id = :id", nativeQuery = true)
    int restaurarPrediccionPorContenidoId(@Param("id") Long id);

    // 3. Reactiva las keywords asociadas a esa predicción
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE keyword SET activo = true WHERE prediccion_id = (SELECT id FROM prediccion WHERE contenido_id = :id)", nativeQuery = true)
    int restaurarKeywordsPorContenidoId(@Param("id") Long id);

    // 4. Desactiva todas las recomendaciones asociadas a esa predicción
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE recomendacion SET activo = false WHERE prediccion_id = (SELECT id FROM prediccion WHERE contenido_id = :id)", nativeQuery = true)
    int desactivarRecomendacionesPorContenidoId(@Param("id") Long id);

    // 4. Reactiva las recomendaciones asociadas a esa predicción
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE recomendacion SET activo = true WHERE prediccion_id = (SELECT id FROM prediccion WHERE contenido_id = :id)", nativeQuery = true)
    int restaurarRecomendacionesPorContenidoId(@Param("id") Long id);
}