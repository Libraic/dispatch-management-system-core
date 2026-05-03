package io.kovin.dispatch.management.system.model.response.error;

import java.util.Map;
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
    private HttpStatus status;
    private ErrorType type;
    private Map<String, String> errors;

    public static ErrorResponse of(String message, HttpStatus status) {
        return ErrorResponse.builder()
            .message(message)
            .status(status)
            .build();
    }
}
