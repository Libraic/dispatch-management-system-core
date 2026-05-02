package io.kovin.dispatch.management.system.model.request;

import java.util.UUID;
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
     UUID companyUuid,
     UUID truckUuid,
     UUID trailerUuid,
     UUID dispatcherUuid
) {
}
