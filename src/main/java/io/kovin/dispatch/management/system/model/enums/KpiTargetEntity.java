package io.kovin.dispatch.management.system.model.enums;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_KPI_TARGET_ENTITY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;

public enum KpiTargetEntity {

    DISPATCHER,
    DRIVER;

    public static KpiTargetEntity from(String input) {
        for (var targetEntity : KpiTargetEntity.values()) {
            if (targetEntity.name().equalsIgnoreCase(input.toLowerCase())) {
                return targetEntity;
            }
        }

        throw DispatchManagementSystemException.of(String.format(INVALID_KPI_TARGET_ENTITY, input), BAD_REQUEST);
    }
}
