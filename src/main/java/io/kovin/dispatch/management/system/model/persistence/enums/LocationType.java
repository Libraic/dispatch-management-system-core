package io.kovin.dispatch.management.system.model.persistence.enums;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.LOCATION_LABEL_IS_MANDATORY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LocationType {

    STARTING_POINT("Starting Point"),
    DELIVERY("Delivery"),
    PICK_UP("Pick Up"),
    ENDING_POINT("Ending Point");

    private final String type;

    public static LocationType from(String type) {
        if (type == null) {
            throw DispatchManagementSystemException.of(LOCATION_LABEL_IS_MANDATORY, BAD_REQUEST);
        }

        for (LocationType lt : values()) {
            if (lt.type.equalsIgnoreCase(type)) {
                return lt;
            }
        }

        return null;
    }
}
