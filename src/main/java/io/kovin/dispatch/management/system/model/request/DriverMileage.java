package io.kovin.dispatch.management.system.model.request;

import io.kovin.dispatch.management.system.model.global.Mileage;

import java.util.List;

public record DriverMileage(
    String mileageUuid,
    String dispatcherUuid,
    String driverUuid,
    String itemIdentifier,
    List<Mileage> mileage
) {
}
