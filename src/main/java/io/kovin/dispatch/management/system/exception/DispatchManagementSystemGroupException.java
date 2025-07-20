package io.kovin.dispatch.management.system.exception;

import io.kovin.dispatch.management.system.model.response.error.GroupsErrors;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DispatchManagementSystemGroupException extends RuntimeException {

    private final GroupsErrors groupsErrors;
    private final HttpStatus httpStatus;

    private DispatchManagementSystemGroupException(GroupsErrors groupsErrors, HttpStatus httpStatus) {
        this.groupsErrors = groupsErrors;
        this.httpStatus = httpStatus;
    }

    public static DispatchManagementSystemGroupException of(GroupsErrors groupsErrors, HttpStatus httpStatus) {
        throw new DispatchManagementSystemGroupException(groupsErrors, httpStatus);
    }
}
