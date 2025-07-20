package cucumber.steps;

import static cucumber.utils.RestConstants.BASE_COMPANIES_API_URL;
import static cucumber.utils.RestConstants.BASE_DRIVERS_API_URL;
import static cucumber.utils.RestConstants.BASE_USERS_API_URL;

import cucumber.component.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import io.kovin.dispatch.management.system.model.request.CreateCompanyRequest;
import io.kovin.dispatch.management.system.model.request.CreateDriverRequest;
import io.kovin.dispatch.management.system.model.request.CreateUserRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.CompanyData;
import io.kovin.dispatch.management.system.model.response.DriverData;
import io.kovin.dispatch.management.system.model.response.UserData;
import io.kovin.dispatch.management.system.model.response.error.Error;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@ScenarioScope
public class RestSteps {

    private static final String GROUPS_ERROR_RESPONSE_KEYWORD = "errors";

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
        Map<String, String> modifiableQueryParams = new HashMap<>(queryParams);
        if (modifiableQueryParams.containsKey("companyId")) {
            CompanyData companyData = (CompanyData) scenarioContext.getActual(CompanyData.class);
            String companyUuid = companyData.getUuid();
            modifiableQueryParams.put("companyId", "join:" + companyUuid);
        }

        ResponseEntity<ApiResponse<List<DriverData>, ErrorResponse>> response = restTemplate.getEntities(
            BASE_DRIVERS_API_URL,
            modifiableQueryParams
        );
        saveCreatedEntitiesAndStatusCode(response, DriverData.class);
    }

    @When("the User is registered in the system")
    public void saveUser() {
        CreateUserRequest request = (CreateUserRequest) scenarioContext.getActual(CreateUserRequest.class);
        ResponseEntity<?> response = restTemplate.post(ApiResponse.class, BASE_USERS_API_URL, request);
        saveCreatedEntityAndStatusCode(response, UserData.class);
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
        if (apiErrors instanceof LinkedHashMap<?, ?> apiErrorsMap) {
            Object apiGroupsErrors = apiErrorsMap.get(GROUPS_ERROR_RESPONSE_KEYWORD);
            if (apiGroupsErrors instanceof LinkedHashMap<?, ?> apiGroupsErrorsMap) {
                Map<String, Object> errors = new HashMap<>();
                for (Map.Entry<?, ?> groupErrors : apiGroupsErrorsMap.entrySet()) {
                    if (groupErrors.getValue() instanceof List<?> fieldsErrors) {
                        List<Error> errs = new ArrayList<>();
                        for (Object fieldError : fieldsErrors) {
                            Error error = objectMapper.convertValue(fieldError, Error.class);
                            errs.add(error);
                        }
                        errors.put((String) groupErrors.getKey(), errs);
                    } else {
                        Error error = objectMapper.convertValue(groupErrors.getValue(), Error.class);
                        errors.put((String) groupErrors.getKey(), error);
                    }
                }
                scenarioContext.addActual(GroupsErrorResponse.class, errors);
            } else {
                ErrorResponse errorResponse = objectMapper.convertValue(apiErrorsMap, ErrorResponse.class);
                scenarioContext.addActual(ErrorResponse.class, errorResponse);
            }
        }
    }
}
