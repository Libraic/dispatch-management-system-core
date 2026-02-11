package io.kovin.dispatch.management.system.model.request;

import lombok.Builder;

@Builder(toBuilder = true)
public record CreateDriverRequest(
     String firstName,
     String lastName,
     String phoneNumber,
     String email,
     String documentsStatus,
     String position,
     String state,
     String city,
     String companyUuid,
     String truckUuid,
     String trailerUuid,
     String dispatcherUuid
) {
}
