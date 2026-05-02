package io.kovin.dispatch.management.system.model.request;

import java.util.UUID;

public record CreateTrailerRequest(
    UUID companyUuid,
    String trailerNumber,
    String vinNumber,
    short trailerYear,
    String trailerMake,
    String equipmentType,
    int equipmentSize,
    int palletCapacity,
    int maxWeight,
    String tireSize
) {
}
