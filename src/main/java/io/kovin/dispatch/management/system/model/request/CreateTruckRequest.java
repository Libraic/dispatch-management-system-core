package io.kovin.dispatch.management.system.model.request;

import java.util.UUID;

public record CreateTruckRequest(
    UUID companyUuid,
    String truckNumber,
    String vinNumber,
    String model,
    short truckYear,
    String truckMake,
    String fuelType,
    String color,
    int weight
) {
}
