package io.kovin.dispatch.management.system.model.entity;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.BAD_ROLE;

import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum Role {
    ADMIN("Administrator"),
    EMPLOYEE("Employee");

    private final String name;

    public static Role from(String role) {
        for (Role r : values()) {
            if (r.name.equals(role)) {
                return r;
            }
        }

        throw DispatchManagementSystemException.of(String.format(BAD_ROLE, role), HttpStatus.BAD_REQUEST);
    }
}
