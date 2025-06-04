package io.kovin.dispatch.management.system.model.entity;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.BAD_POSITION;

import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum Position {

    FOUNDER_CEO("Founder/CEO"),
    ACCOUNTANT("Accountant"),
    FLATBED_DISPATCHER("Flatbed Dispatcher");

    private final String position;

    public static Position from(String position) {
        for (Position p : values()) {
            if (p.position.equals(position)) {
                return p;
            }
        }

        throw DispatchManagementSystemException.of(String.format(BAD_POSITION, position), HttpStatus.BAD_REQUEST);
    }
}
