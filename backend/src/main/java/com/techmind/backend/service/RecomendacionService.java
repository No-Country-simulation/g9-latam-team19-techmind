package com.techmind.backend.service;

import com.techmind.backend.dto.RecomendacionResponseDto;
import com.techmind.backend.repository.RecommendationsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import com.techmind.backend.entity.Recomendacion;

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
    public Optional<RecomendacionResponseDto> obtenerPorId(Long id) {
        return recommendationsRepository.findById(id)
                .filter(Recomendacion::getActivo)
                .map(RecomendacionResponseDto::deEntidad);
    }

    @Transactional(readOnly = true)
    public List<RecomendacionResponseDto> obtenerPorLanguage(String language) {
        return recommendationsRepository
                .findByLanguageIgnoreCaseAndActivoTrue(language)
                .stream()
                .map(RecomendacionResponseDto::deEntidad)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecomendacionResponseDto> obtenerPorLevel(String level) {
        return recommendationsRepository
                .findByLevelIgnoreCaseAndActivoTrue(level)
                .stream()
                .map(RecomendacionResponseDto::deEntidad)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecomendacionResponseDto> obtenerPorCategoria(String category) {
        return recommendationsRepository
                .findByCategoryRecsIgnoreCaseAndActivoTrue(category)
                .stream()
                .map(RecomendacionResponseDto::deEntidad)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecomendacionResponseDto> obtenerPorTipo(String type) {
        return recommendationsRepository
                .findByTypeIgnoreCaseAndActivoTrue(type)
                .stream()
                .map(RecomendacionResponseDto::deEntidad)
                .toList();
    }
}