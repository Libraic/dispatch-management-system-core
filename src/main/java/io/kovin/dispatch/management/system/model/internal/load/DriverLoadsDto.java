package io.kovin.dispatch.management.system.model.internal.load;

import java.util.List;
import java.util.Objects;

public record DriverLoadsDto(
    DriverDto driver,
    DispatcherDto dispatcher,
    List<LoadDto> loads
) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DriverLoadsDto that = (DriverLoadsDto) o;
        return Objects.equals(driver, that.driver) && Objects.equals(dispatcher, that.dispatcher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driver, dispatcher, loads);
    }
}
