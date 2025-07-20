package cucumber.data.builder;

import static cucumber.utils.RandomUtils.generateEmailFromFirstAndLastName;

import com.github.javafaker.Faker;
import cucumber.steps.ScenarioContext;
import cucumber.utils.RandomUtils;
import java.util.List;
import io.cucumber.spring.ScenarioScope;
import io.kovin.dispatch.management.system.model.request.CreateSupervisorRequest;
import io.kovin.dispatch.management.system.model.request.CreateUserRequest;
import io.kovin.dispatch.management.system.model.request.CreateWorkloadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ScenarioScope
public class UserObjectsBuilder {

    private final ScenarioContext scenarioContext;
    private final Faker faker;

    public void registerEmptyCreateUserRequest() {
        scenarioContext.addActual(CreateUserRequest.class, CreateUserRequest.builder().build());
    }

    public void registerInvalidCreateSupervisorRequest() {
        scenarioContext.addActual(CreateSupervisorRequest.class, CreateSupervisorRequest.builder().build());
    }

    public void registerCreateUserRequest() {
        CreateWorkloadRequest createWorkloadRequest = (CreateWorkloadRequest) scenarioContext.getActual(CreateWorkloadRequest.class);
        CreateSupervisorRequest createSupervisorRequest = (CreateSupervisorRequest) scenarioContext.getActual(CreateSupervisorRequest.class);
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
            .firstName(firstName)
            .lastName(lastName)
            .email(generateEmailFromFirstAndLastName(firstName, lastName))
            .password(faker.harryPotter().character())
            .birthDate(RandomUtils.generateLittleEndianDate())
            .employmentDate(RandomUtils.generateLittleEndianDate())
            .workloads(createSupervisorRequest != null ? List.of(createWorkloadRequest) : List.of())
            .supervisor(createSupervisorRequest)
            .build();
        scenarioContext.addActual(CreateUserRequest.class, createUserRequest);
    }
}
