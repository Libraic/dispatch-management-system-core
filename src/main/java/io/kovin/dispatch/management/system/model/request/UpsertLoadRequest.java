package io.kovin.dispatch.management.system.model.request;

import java.math.BigDecimal;
import java.util.List;

public record UpsertLoadRequest(
    String loadUuid,
    String loadNumber,
    String relationUuid,
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
