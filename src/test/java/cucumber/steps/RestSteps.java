package cucumber.steps;

import static cucumber.utils.RestConstants.BASE_COMPANIES_API_URL;
import static cucumber.utils.RestConstants.BASE_DRIVERS_API_URL;

import cucumber.component.RestTemplate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import io.cucumber.java.en.When;
import io.kovin.dispatch.management.system.model.request.CreateCompanyRequest;
import io.kovin.dispatch.management.system.model.request.CreateDriverRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.CompanyData;
import io.kovin.dispatch.management.system.model.response.DriverData;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
import io.kovin.dispatch.management.system.model.response.error.GroupErrorResponse;
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

    @When("the Drivers are retrieved by the following query params:")
    public void fetchDrivers(Map<String, String> queryParams) {
        ResponseEntity<ApiResponse<List<DriverData>, ErrorResponse>> response = restTemplate.getEntities(
            BASE_DRIVERS_API_URL,
            queryParams
        );
        saveCreatedEntitiesAndStatusCode(response, DriverData.class);
    }

    private <T> void saveCreatedEntityAndStatusCode(
        ResponseEntity<?> response,
        Class<T> dataClazz
    ) {
        if (response.getBody() != null) {
            var body = response.getBody();
            if (body instanceof ApiResponse<?, ?>) {
                var data = ((ApiResponse<?, ?>) body).getData();
                var apiErrors = ((ApiResponse<?, ?>) body).getError();
                T convertedData = objectMapper.convertValue(data, dataClazz);
                scenarioContext.addActual(dataClazz, convertedData);
                registerErrorsIfPresent(apiErrors);
            }
        }
        scenarioContext.addActual(HttpStatusCode.class, response.getStatusCode());
    }

    private <T> void saveCreatedEntitiesAndStatusCode(
        ResponseEntity<?> response,
        Class<T> dataClazz
    ) {
        if (response.getBody() != null) {
            var body = response.getBody();
            if (body instanceof ApiResponse<?, ?>) {
                var data = ((ApiResponse<?, ?>) body).getData();
                var apiErrors = ((ApiResponse<?, ?>) body).getError();
                if (data instanceof List<?> list) {
                    List<T> convertedItems = new ArrayList<>();
                    for (Object item : list) {
                        T convertedData = objectMapper.convertValue(item, dataClazz);
                        convertedItems.add(convertedData);
                    }
                    scenarioContext.addActual(List.class, convertedItems);
                }
                registerErrorsIfPresent(apiErrors);
            }
        }
        scenarioContext.addActual(HttpStatusCode.class, response.getStatusCode());
    }

    private <E> void registerErrorsIfPresent(E apiErrors) {
        if (apiErrors instanceof ArrayList<?> apiErrorsList) {
            List<GroupErrorResponse> errors = new ArrayList<>();
            for (var error : apiErrorsList) {
                errors.add(objectMapper.convertValue(error, GroupErrorResponse.class));
            }
            scenarioContext.addActual(GroupErrorResponse.class, errors);
        } else if (apiErrors instanceof LinkedHashMap<?, ?> apiErrorsMap) {
            ErrorResponse errorResponse = objectMapper.convertValue(apiErrorsMap, ErrorResponse.class);
            scenarioContext.addActual(ErrorResponse.class, errorResponse);
        }
    }
}
