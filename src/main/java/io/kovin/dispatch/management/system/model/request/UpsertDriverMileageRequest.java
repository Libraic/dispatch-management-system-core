package io.kovin.dispatch.management.system.model.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record UpsertDriverMileageRequest(
    String driverMileageUuid,
    String companyUuid,
    String dispatcherUuid,
    String driverUuid,
    LocalDate startDate,
    LocalDate endDate,
    LocalDate mileageDate,
    BigDecimal revenue,
    BigDecimal miles,
    String broker,
    String representative,
    String representativeContactNumber,
    List<CreateMileageLocationRequest> locations
) {

}
