package io.kovin.dispatch.management.system.model.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreateDriverRequest(
     String firstName,
     String lastName,
     String phoneNumber,
     String email,
     String truckNumber,
     String trailerNumber,
     BigDecimal maxLegalWeightCapacity,
     String trailerType,
     BigDecimal trailerLength,
     String documentStatus,
     String position,
     String companyUuid
) {
}
