package cucumber.steps;

import cucumber.data.builder.DriverObjectsBuilder;
import java.util.Map;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.spring.ScenarioScope;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ScenarioScope
public class DriverSteps {

    private final DriverObjectsBuilder driverObjectsBuilder;

    @Given("CreateDriverRequest request is created")
    public void createCreateDriverRequest() {
        driverObjectsBuilder.registerCompleteCreateDriverRequest();
    }

    @Given("CreateDriverRequest request is created with following parameters:")
    public void createCreateDriverRequestWithParams(Map<String, String> params) {
        driverObjectsBuilder.registerCreateDriverRequestWithParams(params);
    }

    @Given("the expected DriverData is created")
    public void createExpectedDriverData() {
        driverObjectsBuilder.createdExpectedDriverData();
    }

    @Given("the expected DriverData objects are created from data:")
    public void createExpectedDriverDataObjects(DataTable dataTable) {
        driverObjectsBuilder.createExpectedDriversDataFromData(dataTable);
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
