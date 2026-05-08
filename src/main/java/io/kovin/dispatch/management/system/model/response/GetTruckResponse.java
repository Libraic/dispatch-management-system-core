package io.kovin.dispatch.management.system.model.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GetTruckResponse(
    String uuid,
    String truckNumber,
    String vinNumber,
    String model,
    String truckMake,
    String fuelType,
    LocalDateTime createdAt
) {
}
