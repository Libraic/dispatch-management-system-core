package cucumber.steps;

import cucumber.data.DriverObjectsBuilder;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DriverSteps {

    private final DriverObjectsBuilder driverObjectsBuilder;

    @Given("CreateDriverRequest request is created")
    public void createCreateDriverRequest() {
        driverObjectsBuilder.registerCompleteCreateDriverRequest();
    }

    @Given("the expected DriverData is created")
    public void createExpectedDriverData() {
        driverObjectsBuilder.createdExpectedDriverData();
    }

    @Given("an empty CreateDriverRequest is created")
    public void createEmptyCreateDriverRequest() {
        driverObjectsBuilder.registerEmptyCreateDriverRequest();
    }

    @Given("a CreateDriverRequest without Company is created")
    public void createCreateDriverRequestWithoutCompany() {
        driverObjectsBuilder.registerCreateDriverRequestWithoutCompany();
    }
}
