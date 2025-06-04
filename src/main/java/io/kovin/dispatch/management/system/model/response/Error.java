package io.kovin.dispatch.management.system.model.response;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

@Builder
@Value
public class Error {

    String message;
    HttpStatus status;

    public static Error of(String message, HttpStatus status) {
        return Error.builder()
            .message(message)
            .status(status)
            .build();
    }
}
