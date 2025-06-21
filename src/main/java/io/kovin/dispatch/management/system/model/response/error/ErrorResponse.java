package io.kovin.dispatch.management.system.model.response.error;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

@Builder
@Value
public class ErrorResponse {

    String message;
    String field;
    String area;
    HttpStatus status;

    public static ErrorResponse of(String message, HttpStatus status) {
        return ErrorResponse.builder()
            .message(message)
            .status(status)
            .build();
    }

    public static ErrorResponse of(String message, HttpStatus status, String field, String area) {
        return ErrorResponse.builder()
            .message(message)
            .status(status)
            .field(field)
            .area(area)
            .build();
    }
}
