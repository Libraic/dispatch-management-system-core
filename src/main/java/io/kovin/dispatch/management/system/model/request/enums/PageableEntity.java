package io.kovin.dispatch.management.system.model.request.enums;

import io.kovin.dispatch.management.system.model.persistence.DispatcherEntity;
import io.kovin.dispatch.management.system.model.persistence.DriverEntity;
import io.kovin.dispatch.management.system.model.persistence.TrailerEntity;
import io.kovin.dispatch.management.system.model.persistence.TruckEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PageableEntity {

    DISPATCHER(DispatcherEntity.class),
    DRIVER(DriverEntity.class),
    TRUCK(TruckEntity.class),
    TRAILER(TrailerEntity.class);

    private final Class<?> clazz;

    public static Class<?> getClass(String entity) {
        if (entity == null) {
            return null;
        }

        for (PageableEntity value : PageableEntity.values()) {
            if (value.toString().equalsIgnoreCase(entity)) {
                return value.clazz;
            }
        }

        return null;
    }
}
