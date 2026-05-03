package io.kovin.dispatch.management.system.exception;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class DispatchManagementSystemValidationException extends RuntimeException {

    private Map<String, String> errors;
    private HttpStatus httpStatus;

    public static DispatchManagementSystemValidationException ofBadRequest(Map<String, String> errors) {
        return new DispatchManagementSystemValidationException(errors, HttpStatus.BAD_REQUEST);
    }

    @Override
    public String toString() {
        return "DispatchManagementSystemValidationException{" +
            "errors=" + errors +
            ", httpStatus=" + httpStatus +
            '}';
    }
}
