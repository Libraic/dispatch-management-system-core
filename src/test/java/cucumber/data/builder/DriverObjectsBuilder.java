package cucumber.data.builder;

import static cucumber.utils.ITConstants.DOT;
import static cucumber.utils.ITConstants.HYPHEN;
import static cucumber.utils.RandomUtils.generateEmailFromFirstAndLastName;

import com.github.javafaker.Faker;
import cucumber.steps.ScenarioContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import io.cucumber.datatable.DataTable;
import io.cucumber.spring.ScenarioScope;
import io.kovin.dispatch.management.system.model.entity.enums.DocumentStatus;
import io.kovin.dispatch.management.system.model.entity.enums.DriverPosition;
import io.kovin.dispatch.management.system.model.request.CreateDriverRequest;
import io.kovin.dispatch.management.system.model.response.CompanyData;
import io.kovin.dispatch.management.system.model.response.DriverData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ScenarioScope
@RequiredArgsConstructor
public class DriverObjectsBuilder {

    private final Faker faker;
    private final ScenarioContext scenarioContext;

    public void registerCompleteCreateDriverRequest() {
        CreateDriverRequest request = getFullCreateDriverRequest();
        scenarioContext.addActual(CreateDriverRequest.class, request);
    }

    public void registerCreateDriverRequestWithParams(Map<String, String> data) {
        CreateDriverRequest request = getFullCreateDriverRequest();
        CreateDriverRequest newRequest = constructNewObjectFromData(request, data);
        scenarioContext.addActual(CreateDriverRequest.class, newRequest);
    }

    public void registerCreateDriverRequestWithoutCompany() {
        CreateDriverRequest request = getCreateDriverRequestWithoutCompany();
        scenarioContext.addActual(CreateDriverRequest.class, request);
    }

    public void registerEmptyCreateDriverRequest() {
        scenarioContext.addActual(CreateDriverRequest.class, CreateDriverRequest.builder().build());
    }

    public void createdExpectedDriverData() {
        CreateDriverRequest request = (CreateDriverRequest) scenarioContext.getActual(CreateDriverRequest.class);
        DriverData driverData = DriverData.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .phoneNumber(request.phoneNumber())
            .documentsStatus(request.documentsStatus())
            .state(request.state())
            .city(request.city())
            .build();
        scenarioContext.addExpected(DriverData.class, driverData);
    }

    public void createExpectedDriversDataFromData() {
        List<?> requests = scenarioContext.getAllActualOfType(CreateDriverRequest.class);
        List<DriverData> expectedDriversData = new ArrayList<>();
        for (Object request : requests) {
            if (request instanceof CreateDriverRequest createDriverRequest) {
                expectedDriversData.add(constructDriverData(createDriverRequest, Map.of()));
            }
        }

        scenarioContext.addExpected(List.class, expectedDriversData);
    }

    public void createExpectedDriversDataFromData(DataTable dataTable) {
        List<Map<String, String>> driversData = dataTable.asMaps(String.class, String.class);
        List<DriverData> expectedDriversData = new ArrayList<>();
        for (Map<String, String> driverData : driversData) {
            expectedDriversData.add(constructDriverData(CreateDriverRequest.builder().build(), driverData));
        }

        scenarioContext.addExpected(List.class, expectedDriversData);
    }

    private CreateDriverRequest getCreateDriverRequestWithoutCompany() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String phoneNumber = faker.phoneNumber().cellPhone().replace(DOT, HYPHEN);
        return CreateDriverRequest.builder()
            .firstName(firstName)
            .lastName(lastName)
            .phoneNumber(phoneNumber)
            .email(generateEmailFromFirstAndLastName(firstName, lastName))
            .documentsStatus(DocumentStatus.CITIZEN.getType())
            .position(DriverPosition.COMPANY_DRIVER.getPosition())
            .state(faker.address().state())
            .city(faker.address().city())
            .build();
    }

    private CreateDriverRequest getFullCreateDriverRequest() {
        CompanyData companyData = (CompanyData) scenarioContext.getActual(CompanyData.class);
        CreateDriverRequest createDriverRequest = getCreateDriverRequestWithoutCompany();
        return createDriverRequest.toBuilder().companyUuid(companyData.getUuid()).build();
    }

    private CreateDriverRequest constructNewObjectFromData(CreateDriverRequest request, Map<String, String> data) {
        return request.toBuilder()
            .firstName(data.getOrDefault("firstName", request.firstName()))
            .lastName(data.getOrDefault("lastName", request.lastName()))
            .phoneNumber(data.getOrDefault("phoneNumber", request.phoneNumber()))
            .email(data.getOrDefault("email", request.email()))
            .build();
    }

    private DriverData constructDriverData(CreateDriverRequest request, Map<String, String> driverData) {
        return DriverData.builder()
            .firstName(driverData.getOrDefault("firstName", request.firstName()))
            .lastName(driverData.getOrDefault("lastName", request.lastName()))
            .email(driverData.getOrDefault("email", request.email()))
            .phoneNumber(driverData.getOrDefault("phoneNumber", request.phoneNumber()))
            .documentsStatus(driverData.getOrDefault("documentsStatus", request.documentsStatus()))
            .build();
    }
}
