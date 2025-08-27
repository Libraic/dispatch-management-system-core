package io.kovin.dispatch.management.system.model.request;

import java.util.List;

public record UpsertDriverMileageRequest(
    String companyUuid,
    List<DriverMileage> driverMileageData
) {
}
