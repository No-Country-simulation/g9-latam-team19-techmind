package com.techmind.backend.dto;

import java.util.List;

public record DataScienceResponseDto(
        String category,
        Double confidence,
        List<String> keywords
) {
}
