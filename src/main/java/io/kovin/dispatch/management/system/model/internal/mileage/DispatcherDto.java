package io.kovin.dispatch.management.system.model.internal.mileage;

import lombok.Builder;

import java.util.Objects;

@Builder
public record DispatcherDto(
    String uuid,
    String fullName
) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DispatcherDto that = (DispatcherDto) o;
        return Objects.equals(uuid, that.uuid) && Objects.equals(fullName, that.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, fullName);
    }
}
