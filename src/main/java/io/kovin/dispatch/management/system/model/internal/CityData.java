package io.kovin.dispatch.management.system.model.internal;

import java.util.Objects;

public record CityData(
    String zipCode,
    String city,
    String state,
    String timezone
) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CityData cityData = (CityData) o;
        return Objects.equals(city, cityData.city)
            && Objects.equals(state, cityData.state)
            && Objects.equals(timezone, cityData.timezone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, state);
    }
}
