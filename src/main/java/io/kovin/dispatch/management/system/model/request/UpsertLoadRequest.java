package io.kovin.dispatch.management.system.model.request;

import java.math.BigDecimal;
import java.util.List;

public record UpsertLoadRequest(
    String loadUuid,
    String companyUuid,
    String dispatcherUuid,
    String driverUuid,
    BigDecimal revenue,
    BigDecimal miles,
    String broker,
    String representative,
    String representativeContactNumber,
    List<CreateLoadLocationRequest> locations
) {

}
