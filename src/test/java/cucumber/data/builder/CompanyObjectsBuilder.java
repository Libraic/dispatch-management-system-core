package cucumber.data.builder;

import com.github.javafaker.Faker;
import cucumber.utils.RandomUtils;
import io.kovin.dispatch.management.system.model.request.CreateCompanyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyObjectsBuilder {

    private final Faker faker;

    public CreateCompanyRequest getCreateCompanyRequest() {
        return CreateCompanyRequest.builder()
            .name(faker.company().name())
            .mcNumber(Integer.toString(faker.number().numberBetween(10000, 99999)))
            .address(faker.address().fullAddress())
            .serviceDate(RandomUtils.generateLittleEndianDate())
            .startDate(RandomUtils.generateLittleEndianDate())
            .build();
    }
}
