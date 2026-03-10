package io.kovin.dispatch.management.system.model.internal.load;

import java.util.Objects;

public record DriverDto(
    String uuid,
    String fullName
) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DriverDto driverDto = (DriverDto) o;
        return Objects.equals(uuid, driverDto.uuid) && Objects.equals(fullName, driverDto.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, fullName);
    }
}
