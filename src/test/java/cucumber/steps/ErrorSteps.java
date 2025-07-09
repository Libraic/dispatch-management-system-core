package cucumber.steps;

import cucumber.data.ErrorResponseObjectsBuilder;
import cucumber.data.GroupErrorResponseObjectsBuilder;
import java.util.Map;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ErrorSteps {

    private final GroupErrorResponseObjectsBuilder groupErrorResponseObjectsBuilder;
    private final ErrorResponseObjectsBuilder errorResponseObjectsBuilder;

    @Given("the expected GroupErrorResponse list is created from the following data:")
    public void createGroupErrorResponsesFromData(Map<String, String> errorData) {
        groupErrorResponseObjectsBuilder.registerGroupErrorResponseFromData(errorData);
    }

    @Given("the expected ErrorResponse is created from the following data:")
    public void createErrorResponseFromData(Map<String, String> errorData) throws IllegalAccessException {
        errorResponseObjectsBuilder.registerErrorResponseFromData(errorData);
    }
}
