package cucumber.steps;

import cucumber.data.builder.UserObjectsBuilder;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserSteps {

    private final UserObjectsBuilder userObjectsBuilder;

    @Given("an empty CreateUserRequest is created")
    public void createEmptyCreateUserRequest() {
        userObjectsBuilder.registerEmptyCreateUserRequest();
    }

    @Given("CreateUserRequest is created")
    public void createCreateUserRequest() {
        userObjectsBuilder.registerCreateUserRequest();
    }

    @Given("an invalid CreateSupervisorRequest is created")
    public void createInvalidCreateSupervisorRequest() {
        userObjectsBuilder.registerInvalidCreateSupervisorRequest();
    }
}
