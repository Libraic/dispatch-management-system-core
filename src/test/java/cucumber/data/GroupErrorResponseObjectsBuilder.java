package cucumber.data;

import cucumber.steps.ScenarioContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import io.cucumber.spring.ScenarioScope;
import io.kovin.dispatch.management.system.model.response.error.FieldErrorResponse;
import io.kovin.dispatch.management.system.model.response.error.GroupErrorResponse;
import io.kovin.dispatch.management.system.model.response.error.ItemErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@RequiredArgsConstructor
public class GroupErrorResponseObjectsBuilder {

    private final ScenarioContext scenarioContext;

    public void registerGroupErrorResponseFromData(Map<String, String> errorData) {
        List<GroupErrorResponse> groupErrorResponses = new ArrayList<>();
        for (Map.Entry<String, String> entry : errorData.entrySet()) {
            GroupErrorResponse groupErrorResponse = GroupErrorResponse.builder()
                .impactedGroup(entry.getKey())
                .errors(List.of(ItemErrorResponse.builder()
                    .groupItemFieldsErrors(List.of(FieldErrorResponse.builder()
                        .errorMessage(entry.getValue())
                        .build()
                    )).build()
                )).build();
            groupErrorResponses.add(groupErrorResponse);
        }

        scenarioContext.addExpected(GroupErrorResponse.class, groupErrorResponses);
    }
}
