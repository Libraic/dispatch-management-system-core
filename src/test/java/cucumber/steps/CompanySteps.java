package cucumber.steps;

import cucumber.data.CompanyObjectsBuilder;
import io.cucumber.java.en.Given;
import io.cucumber.spring.ScenarioScope;
import io.kovin.dispatch.management.system.model.request.CreateCompanyRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ScenarioScope
public class CompanySteps {

    private final ScenarioContext scenarioContext;
    private final CompanyObjectsBuilder companyObjectsBuilder;

    @Given("CreateCompanyRequest request is created")
    public void createCreateCompanyRequest() {
        CreateCompanyRequest createCompanyRequest = companyObjectsBuilder.getCreateCompanyRequest();
        scenarioContext.addActual(CreateCompanyRequest.class, createCompanyRequest);
    }
}
