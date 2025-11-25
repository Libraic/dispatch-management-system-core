package io.kovin.dispatch.management.system.model.internal.mileage;

import java.util.List;

public record DriverMileageDto(
    DriverDto driver,
    DispatcherDto dispatcher,
    List<MileageDto> mileage
) {
}
