package io.kovin.dispatch.management.system.model.request;

import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record CreateDriverRequest(
     String firstName,
     String lastName,
     String phoneNumber,
     String email,
     String truckNumber,
     String trailerNumber,
     BigDecimal trailerHeight,
     BigDecimal maxLegalWeightCapacity,
     String trailerType,
     BigDecimal trailerLength,
     String documentsStatus,
     String position,
     String state,
     String city,
     String companyUuid
) {
}
