package io.kovin.dispatch.management.system.exception;

import java.util.List;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DispatchManagementSystemGroupException extends RuntimeException {

    private final List<ItemsGroup> itemsGroups;
    private final HttpStatus httpStatus;

    private DispatchManagementSystemGroupException(List<ItemsGroup> itemsGroups, HttpStatus httpStatus) {
        this.itemsGroups = itemsGroups;
        this.httpStatus = httpStatus;
    }

    public static DispatchManagementSystemGroupException of(List<ItemsGroup> itemsGroups, HttpStatus httpStatus) {
        throw new DispatchManagementSystemGroupException(itemsGroups, httpStatus);
    }
}
