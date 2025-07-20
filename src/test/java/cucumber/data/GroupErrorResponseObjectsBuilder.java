package cucumber.data;

import cucumber.steps.ScenarioContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.cucumber.datatable.DataTable;
import io.cucumber.spring.ScenarioScope;
import io.kovin.dispatch.management.system.model.response.error.Error;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@RequiredArgsConstructor
public class GroupErrorResponseObjectsBuilder {

    private final ScenarioContext scenarioContext;

    public void registerGroupErrorResponseFromData(Map<String, String> errorData) {
        Map<String, Object> errors = new HashMap<>();
        for (Map.Entry<String, String> entry : errorData.entrySet()) {
            Error error = new Error(entry.getKey(), entry.getValue(), null);
            errors.put(entry.getKey(), error);
        }

        scenarioContext.addExpected(GroupsErrorResponse.class, errors);
    }

    @SuppressWarnings("unchecked")
    public void registerGroupErrorResponseFromData(DataTable dataTable) {
        List<Map<String, String>> errorsData = dataTable.asMaps(String.class, String.class);
        Map<String, Object> errors = new HashMap<>();
        for (Map<String, String> entry : errorsData) {
            String group = entry.get("group");
            String field = entry.get("field");
            String stringPosition = entry.get("position");
            int position = stringPosition == null ? -1 : Integer.parseInt(stringPosition);
            String errorMessage = entry.get("errorMessage");
            if (position >= 0) {
                Error error = new Error(field, errorMessage, null);
                List<Error> errorsContainer = (List<Error>) errors.computeIfAbsent(
                    group,
                    (k) -> new ArrayList<Error>()
                );
                errorsContainer.add(error);
            } else {
                errors.computeIfAbsent(group, (k) -> new Error(group, errorMessage, null));
            }
        }
        scenarioContext.addExpected(GroupsErrorResponse.class, errors);
    }
}
