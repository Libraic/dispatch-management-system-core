package io.kovin.dispatch.management.system.model.response.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ErrorResponse {

    private String message;
    private String field;
    private String area;
    private HttpStatus status;

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
