package cucumber.steps;

import cucumber.data.DriverObjectsBuilder;
import io.cucumber.java.en.Given;
import io.cucumber.spring.ScenarioScope;
import lombok.RequiredArgsConstructor;

@ScenarioScope
@RequiredArgsConstructor
public class DriverSteps {

    private final ScenarioContext scenarioContext;
    private final DriverObjectsBuilder driverObjectsBuilder;

    @Given("CreateDriverRequest request is created")
    public void createCreateDriverRequest() {
        driverObjectsBuilder.createCreateDriverRequest();
    }

    @Given("the expected DriverData is created")
    public void createExpectedDriverData() {
        driverObjectsBuilder.createdExpectedDriverData();
    }
}
