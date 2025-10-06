package io.kovin.dispatch.management.system.model.request;

public record CreateTrailerRequest(
    String companyUuid,
    String trailerNumber,
    String vinNumber,
    int trailerYear,
    String trailerMake,
    String equipmentType,
    int equipmentSize,
    int palletCapacity,
    int maxWeight,
    String tireSize
) {
}
