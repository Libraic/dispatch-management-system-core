package cucumber.data.builder;

import cucumber.steps.ScenarioContext;
import io.cucumber.spring.ScenarioScope;
import io.kovin.dispatch.management.system.model.request.CreateWorkloadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ScenarioScope
public class WorkloadObjectsBuilder {

    private final ScenarioContext scenarioContext;

    public void registerInvalidCreateWorkloadRequest() {
        scenarioContext.addActual(
            CreateWorkloadRequest.class,
            CreateWorkloadRequest.builder().commission(-10.0).build()
        );
    }
}
