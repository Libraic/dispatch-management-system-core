package io.kovin.dispatch.management.system.model.persistence.enums;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_LOAD_STATUS_BE;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_LOAD_STATUS_CLIENT;

import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Getter
@Slf4j
public enum LoadStatus {
    BOOKED("Booked"),
    DISPATCHED("Dispatched"),
    TRANSIT("Transit"),
    DELIVERED("Delivered"),
    SUBMITTED("Docs Sent"),
    INVOICED("Invoiced"),
    PAID("Paid");

    private final String status;

    public static LoadStatus from(String status) {
        if (status == null) {
            return null;
        }

        for (LoadStatus ls : values()) {
            if (ls.status.equals(status)) {
                return ls;
            }
        }

        String errorMessage = String.format(INVALID_LOAD_STATUS_BE, status);
        log.error(errorMessage);
        throw DispatchManagementSystemException.ofBadRequest(INVALID_LOAD_STATUS_CLIENT);
    }
}
