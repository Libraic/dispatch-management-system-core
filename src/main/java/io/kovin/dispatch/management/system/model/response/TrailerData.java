package io.kovin.dispatch.management.system.model.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TrailerData(String uuid, String trailerNumber, LocalDateTime createdAt) {
}
