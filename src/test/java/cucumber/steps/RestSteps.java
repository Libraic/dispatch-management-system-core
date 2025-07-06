package cucumber.steps;

import static cucumber.utils.RestConstants.BASE_COMPANIES_API_URL;
import static cucumber.utils.RestConstants.BASE_DRIVERS_API_URL;

import cucumber.component.RestTemplate;
import io.cucumber.java.en.When;
import io.kovin.dispatch.management.system.model.request.CreateCompanyRequest;
import io.kovin.dispatch.management.system.model.request.CreateDriverRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.CompanyData;
import io.kovin.dispatch.management.system.model.response.DriverData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
public class RestSteps {

    private final RestTemplate restTemplate;
    private final ScenarioContext scenarioContext;
    private final ObjectMapper objectMapper;

    @When("the Company is registered in the system")
    public void saveCompany() {
        CreateCompanyRequest request = (CreateCompanyRequest) scenarioContext.getActual(CreateCompanyRequest.class);
        ResponseEntity<?> response = restTemplate.post(ApiResponse.class, BASE_COMPANIES_API_URL, request);
        saveCreatedEntityAndStatusCode(response, CompanyData.class);
    }

    @When("the Driver is registered in the system")
    public void saveDriver() {
        CreateDriverRequest request = (CreateDriverRequest) scenarioContext.getActual(CreateDriverRequest.class);
        ResponseEntity<?> response = restTemplate.post(ApiResponse.class, BASE_DRIVERS_API_URL, request);
        saveCreatedEntityAndStatusCode(response, DriverData.class);
    }

    private <T> void saveCreatedEntityAndStatusCode(ResponseEntity<?> response, Class<T> clazz) {
        if (response.getBody() != null) {
            var body = response.getBody();
            if (body instanceof ApiResponse<?, ?>) {
                var data = ((ApiResponse<?, ?>) body).getData();
                T companyData = objectMapper.convertValue(data, clazz);
                scenarioContext.addActual(clazz, companyData);
            }
        }
        scenarioContext.addActual(HttpStatusCode.class, response.getStatusCode());
    }
}
