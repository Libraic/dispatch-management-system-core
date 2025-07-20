package cucumber.steps;

import cucumber.data.builder.WorkloadObjectsBuilder;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorkloadSteps {

    private final WorkloadObjectsBuilder workloadObjectsBuilder;

    @Given("an invalid CreateWorkloadRequest is created")
    public void createInvalidCreateWorkloadRequest() {
        workloadObjectsBuilder.registerInvalidCreateWorkloadRequest();
    }
}
