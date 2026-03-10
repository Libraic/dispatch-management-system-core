package io.kovin.dispatch.management.system.model.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record UpsertLoadRequest(
    String loadUuid,
    String companyUuid,
    String dispatcherUuid,
    String driverUuid,
    LocalDate startDate,
    LocalDate endDate,
    LocalDate loadDate,
    BigDecimal revenue,
    BigDecimal miles,
    String broker,
    String representative,
    String representativeContactNumber,
    List<CreateLoadLocationRequest> locations
) {

}
