package com.techmind.backend.service;

import com.techmind.backend.dto.RecomendacionResponseDto;
import com.techmind.backend.repository.RecommendationsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
                .map(RecomendacionResponseDto::deEntidad)
                .toList();
    }
}