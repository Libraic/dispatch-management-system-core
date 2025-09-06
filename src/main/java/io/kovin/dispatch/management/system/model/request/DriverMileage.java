package io.kovin.dispatch.management.system.model.request;

import java.time.LocalDate;
import java.util.List;
import io.kovin.dispatch.management.system.model.global.Mileage;

public record DriverMileage(
    String mileageUuid,
    String dispatcherUuid,
    String driverUuid,
    String itemIdentifier,
    LocalDate startDate,
    LocalDate endDate,
    List<Mileage> mileage
) {
}
