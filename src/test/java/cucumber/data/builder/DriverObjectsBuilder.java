package cucumber.data.builder;

import static cucumber.utils.ITConstants.DOT;
import static cucumber.utils.ITConstants.HYPHEN;
import static cucumber.utils.RandomUtils.generateEmailFromFirstAndLastName;

import com.github.javafaker.Faker;
import cucumber.steps.ScenarioContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import io.cucumber.datatable.DataTable;
import io.cucumber.spring.ScenarioScope;
import io.kovin.dispatch.management.system.model.entity.enums.DocumentStatus;
import io.kovin.dispatch.management.system.model.entity.enums.DriverPosition;
import io.kovin.dispatch.management.system.model.entity.enums.TrailerType;
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
            .truckNumber(request.truckNumber())
            .trailerNumber(request.trailerNumber())
            .documentsStatus(request.documentsStatus())
            .trailerHeight(request.trailerHeight())
            .maxLegalWeightCapacity(request.maxLegalWeightCapacity())
            .state(request.state())
            .city(request.city())
            .trailerType(TrailerType.FLATBED.getCode())
            .trailerLength(request.trailerLength())
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
            .trailerHeight(BigDecimal.valueOf(faker.number().numberBetween(90, 110)))
            .truckNumber(Integer.toString(faker.number().numberBetween(100, 999)))
            .trailerNumber(Integer.toString(faker.number().numberBetween(100, 999)))
            .maxLegalWeightCapacity(BigDecimal.valueOf(faker.number().numberBetween(10000, 50000)))
            .trailerType(TrailerType.FLATBED.getType())
            .trailerLength(BigDecimal.valueOf(faker.number().numberBetween(10, 50)))
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
            .trailerNumber(data.getOrDefault("trailerNumber", request.trailerNumber()))
            .truckNumber(data.getOrDefault("truckNumber", request.truckNumber()))
            .email(data.getOrDefault("email", request.email()))
            .trailerHeight(extractBigDecimalFieldByName(data, "trailerHeight", request.trailerHeight()))
            .maxLegalWeightCapacity(extractBigDecimalFieldByName(data, "maxLegalWeightCapacity", request.maxLegalWeightCapacity()))
            .build();
    }

    private DriverData constructDriverData(CreateDriverRequest request, Map<String, String> driverData) {
        return DriverData.builder()
            .firstName(driverData.getOrDefault("firstName", request.firstName()))
            .lastName(driverData.getOrDefault("lastName", request.lastName()))
            .email(driverData.getOrDefault("email", request.email()))
            .phoneNumber(driverData.getOrDefault("phoneNumber", request.phoneNumber()))
            .truckNumber(driverData.getOrDefault("truckNumber", request.truckNumber()))
            .trailerNumber(driverData.getOrDefault("trailerNumber", request.trailerNumber()))
            .trailerHeight(extractBigDecimalFieldByName(driverData, "trailerHeight", request.trailerHeight()))
            .maxLegalWeightCapacity(extractBigDecimalFieldByName(driverData, "maxLegalWeightCapacity", request.maxLegalWeightCapacity()))
            .documentsStatus(driverData.getOrDefault("documentsStatus", request.documentsStatus()))
            .build();
    }

    private BigDecimal extractBigDecimalFieldByName(Map<String, String> data, String name, BigDecimal fallbackValue) {
        String field = data.get(name);
        return field != null ? BigDecimal.valueOf(Double.parseDouble(field)) : fallbackValue;
    }
}
