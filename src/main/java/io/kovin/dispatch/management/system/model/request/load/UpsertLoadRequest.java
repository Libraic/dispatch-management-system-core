package io.kovin.dispatch.management.system.model.request.load;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record UpsertLoadRequest(
    UUID loadUuid,
    String loadNumber,
    UUID relationUuid,
    BigDecimal revenue,
    BigDecimal loadedMiles,
    BigDecimal emptyMiles,
    String broker,
    String representative,
    String representativeContactNumber,
    String loadStatus,
    List<CreateLoadLocationRequest> locations
) {

}
