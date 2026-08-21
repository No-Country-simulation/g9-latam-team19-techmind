package com.techmind.backend.service;

import com.techmind.backend.dto.RecomendacionResponseDto;
import com.techmind.backend.entity.Recomendacion;
import com.techmind.backend.exception.ResourceNotFoundException;
import com.techmind.backend.repository.RecommendationsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RecomendacionService {

    private final RecommendationsRepository recommendationsRepository;

    public RecomendacionService(
            RecommendationsRepository recommendationsRepository
    ) {
        this.recommendationsRepository = recommendationsRepository;
    }

    @Transactional(readOnly = true)
    public List<RecomendacionResponseDto> obtenerTodas() {
        return recommendationsRepository.findAll()
                .stream()
                .filter(Recomendacion::getActivo)
                .map(RecomendacionResponseDto::deEntidad)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecomendacionResponseDto obtenerPorId(Long id) {
        return recommendationsRepository.findById(id)
                .filter(Recomendacion::getActivo)
                .map(RecomendacionResponseDto::deEntidad)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la recomendación con el ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<RecomendacionResponseDto> obtenerPorLanguage(String language) {
        if (!recommendationsRepository.existsByLanguageIgnoreCaseAndActivoTrue(language)) {
            throw new ResourceNotFoundException("No se encontraron recomendaciones para el idioma: " + language);
        }
        return recommendationsRepository
                .findByLanguageIgnoreCaseAndActivoTrue(language)
                .stream()
                .map(RecomendacionResponseDto::deEntidad)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecomendacionResponseDto> obtenerPorLevel(String level) {
        if (!recommendationsRepository.existsByLevelIgnoreCaseAndActivoTrue(level)) {
            throw new ResourceNotFoundException("No se encontraron recomendaciones para el nivel: " + level);
        }
        return recommendationsRepository
                .findByLevelIgnoreCaseAndActivoTrue(level)
                .stream()
                .map(RecomendacionResponseDto::deEntidad)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecomendacionResponseDto> obtenerPorCategoria(String category) {
        if (!recommendationsRepository.existsByCategoryRecsIgnoreCaseAndActivoTrue(category)) {
            throw new ResourceNotFoundException("No se encontraron recomendaciones para la categoría: " + category);
        }
        return recommendationsRepository
                .findByCategoryRecsIgnoreCaseAndActivoTrue(category)
                .stream()
                .map(RecomendacionResponseDto::deEntidad)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecomendacionResponseDto> obtenerPorTipo(String type) {
        if (!recommendationsRepository.existsByTypeIgnoreCaseAndActivoTrue(type)) {
            throw new ResourceNotFoundException("No se encontraron recomendaciones para el tipo: " + type);
        }
        return recommendationsRepository
                .findByTypeIgnoreCaseAndActivoTrue(type)
                .stream()
                .map(RecomendacionResponseDto::deEntidad)
                .toList();
    }
}