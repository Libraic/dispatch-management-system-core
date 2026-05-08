package io.kovin.dispatch.management.system.model.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GetTrailerResponse(
    String uuid,
    String trailerNumber,
    String vinNumber,
    Short trailerYear,
    String trailerMake,
    String equipmentType,
    LocalDateTime createdAt
) {
}
