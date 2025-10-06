package io.kovin.dispatch.management.system.model.request;

public record CreateTruckRequest(
    String companyUuid,
    String truckNumber,
    String vinNumber,
    String model,
    int truckYear,
    String truckMake,
    String fuelType,
    String color,
    int weight
) {
}
