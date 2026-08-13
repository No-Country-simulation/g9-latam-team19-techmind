package com.techmind.backend.repository;

import com.techmind.backend.entity.Prediccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrediccionRepository extends JpaRepository<Prediccion, Long> {

}