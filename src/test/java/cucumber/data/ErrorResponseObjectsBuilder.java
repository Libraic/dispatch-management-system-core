package cucumber.data;

import cucumber.steps.ScenarioContext;
import java.util.Map;
import java.util.Optional;
import io.cucumber.spring.ScenarioScope;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@RequiredArgsConstructor
public class ErrorResponseObjectsBuilder {

    private final ScenarioContext scenarioContext;

    public void registerErrorResponseFromData(Map<String, String> errorData) throws IllegalAccessException {
        String message = errorData.get("message");
        HttpStatus status = Optional.ofNullable(errorData.get("status")).map(HttpStatus::valueOf).orElse(null);
        ErrorResponse errorResponse = ErrorResponse.builder()
            .message(message)
            .status(status)
            .build();
        scenarioContext.addExpected(ErrorResponse.class, errorResponse);
    }
}
