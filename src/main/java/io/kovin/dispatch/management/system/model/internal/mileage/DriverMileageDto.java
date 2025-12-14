package io.kovin.dispatch.management.system.model.internal.mileage;

import java.util.List;
import java.util.Objects;

public record DriverMileageDto(
    DriverDto driver,
    DispatcherDto dispatcher,
    List<MileageDto> mileage
) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DriverMileageDto that = (DriverMileageDto) o;
        return Objects.equals(driver, that.driver) && Objects.equals(dispatcher, that.dispatcher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driver, dispatcher, mileage);
    }
}
