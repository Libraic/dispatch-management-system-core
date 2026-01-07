package io.kovin.dispatch.management.system.model.request;

import java.util.List;

public record UpsertDriverMileageRequestOld(
    String companyUuid,
    List<DriverMileage> driverMileageData
) {
}
