package cucumber.data;

import static cucumber.utils.ITConstants.AT_SIGN;
import static cucumber.utils.ITConstants.DOT;
import static cucumber.utils.ITConstants.GOOGLE_DOMAIN;
import static cucumber.utils.ITConstants.HYPHEN;

import com.github.javafaker.Faker;
import cucumber.steps.ScenarioContext;
import java.math.BigDecimal;
import io.cucumber.spring.ScenarioScope;
import io.kovin.dispatch.management.system.model.entity.enums.DocumentStatus;
import io.kovin.dispatch.management.system.model.entity.enums.DriverPosition;
import io.kovin.dispatch.management.system.model.entity.enums.TrailerType;
import io.kovin.dispatch.management.system.model.request.CreateDriverRequest;
import io.kovin.dispatch.management.system.model.response.CompanyData;
import io.kovin.dispatch.management.system.model.response.DriverData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@RequiredArgsConstructor
public class DriverObjectsBuilder {

    private final Faker faker;
    private final ScenarioContext scenarioContext;

    public void registerCompleteCreateDriverRequest() {
        CreateDriverRequest request = getFullCreateDriverRequest();
        scenarioContext.addActual(CreateDriverRequest.class, request);
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
            .build();
        scenarioContext.addExpected(DriverData.class, driverData);
    }

    private CreateDriverRequest getCreateDriverRequestWithoutCompany() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = firstName.toLowerCase() + DOT + lastName.toLowerCase() + AT_SIGN + GOOGLE_DOMAIN;
        String phoneNumber = faker.phoneNumber().cellPhone().replace(DOT, HYPHEN);
        return CreateDriverRequest.builder()
            .firstName(firstName)
            .lastName(lastName)
            .phoneNumber(phoneNumber)
            .email(email)
            .truckNumber(Integer.toString(faker.number().numberBetween(100, 999)))
            .trailerNumber(Integer.toString(faker.number().numberBetween(100, 999)))
            .maxLegalWeightCapacity(BigDecimal.valueOf(faker.number().numberBetween(10000, 50000)))
            .trailerType(TrailerType.FLATBED.getType())
            .trailerLength(BigDecimal.valueOf(faker.number().numberBetween(10, 50)))
            .documentStatus(DocumentStatus.CITIZEN.getType())
            .position(DriverPosition.COMPANY_DRIVER.getPosition())
            .build();
    }

    private CreateDriverRequest getFullCreateDriverRequest() {
        CompanyData companyData = (CompanyData) scenarioContext.getActual(CompanyData.class);
        CreateDriverRequest createDriverRequest = getCreateDriverRequestWithoutCompany();
        return createDriverRequest.toBuilder().companyUuid(companyData.getUuid()).build();
    }
}
