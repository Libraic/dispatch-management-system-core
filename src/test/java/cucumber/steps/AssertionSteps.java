package cucumber.steps;

import static cucumber.utils.ITConstants.UUID_FIELD;
import static org.assertj.core.api.Assertions.assertThat;

import cucumber.utils.ClassUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import io.cucumber.java.en.Then;
import io.cucumber.spring.ScenarioScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatusCode;

@RequiredArgsConstructor
@ScenarioScope
@Slf4j
public class AssertionSteps {

    private final ScenarioContext scenarioContext;

    @Then("the status code is {int}")
    public void assertStatusCode(int statusCode) {
        HttpStatusCode httpStatusCode = (HttpStatusCode) scenarioContext.getActual(HttpStatusCode.class);
        Assertions.assertEquals(statusCode, httpStatusCode.value());
    }

    @Then("the expected and actual {string} objects are equal")
    public void compareObjectsFieldByField(String plainClassName) throws ClassNotFoundException {
        String fullClassName = ClassUtils.getFullClassName(plainClassName);
        assertThat(fullClassName)
            .withFailMessage("The %s class was not found.", fullClassName)
            .isNotNull();

        Class<?> clazz = Class.forName(fullClassName);
        Object actual = scenarioContext.getActual(clazz);
        Object expected = scenarioContext.getExpected(clazz);

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields(UUID_FIELD)
            .isEqualTo(expected);
    }

    @Then("the expected and actual {string} lists are equal ignoring fields {string}")
    public void compareListsItemByItemIgnoringFields(String plainClassName, String fields) throws ClassNotFoundException {
        String fullClassName = ClassUtils.getFullClassName(plainClassName);
        assertThat(fullClassName)
            .withFailMessage("The %s class was not found.", fullClassName)
            .isNotNull();

        String[] f = fields.split(",");
        Class<?> clazz = Class.forName(fullClassName);
        List<?> actual = scenarioContext.getActualList(clazz);
        List<?> expected = scenarioContext.getExpectedList(clazz);
        assertListsAreEqualIgnoringOrder(actual, expected, f);
    }

    @Then("the expected and actual {string} lists are equal")
    public void compareListsItemByItem(String plainClassName) throws ClassNotFoundException {
        String fullClassName = ClassUtils.getFullClassName(plainClassName);
        assertThat(fullClassName)
            .withFailMessage("The %s class was not found.", fullClassName)
            .isNotNull();

        Class<?> clazz = Class.forName(fullClassName);
        List<?> actual = scenarioContext.getActualList(clazz);
        List<?> expected = scenarioContext.getExpectedList(clazz);
        assertListsAreEqualIgnoringOrder(actual, expected);
    }

    private void assertListsAreEqualIgnoringOrder(List<?> actual, List<?> expected, String... fields) {
        String[] fieldsToIgnore = getFieldsToIgnore(fields);
        assertThat(expected.size())
            .withFailMessage(String.format("The collections are not of the same size. Actual size=[%s]. Expected size=[%s].", actual.size(), expected.size()))
            .isEqualTo(actual.size());
        Set<Integer> visited = new HashSet<>();
        List<AssertionError> errors = new ArrayList<>();
        for (Object item : actual) {
            for (int i = 0; i < expected.size(); ++i) {
                if (!visited.contains(i)) {
                    try {
                        assertThat(item).usingRecursiveComparison()
                            .ignoringFields(fieldsToIgnore)
                            .isEqualTo(expected.get(i));
                        visited.add(i);
                        break;
                    } catch (AssertionError e) {
                        errors.add(e);
                    }
                }
            }
        }

        if (visited.size() != actual.size()) {
            throw errors.getFirst();
        }
    }

    private String[] getFieldsToIgnore(String... fields) {
        if (fields == null || fields.length == 0) {
            return new String[]{ UUID_FIELD };
        }

        String[] fieldsToIgnore = new String[fields.length + 1];
        System.arraycopy(fields, 0, fieldsToIgnore, 0, fields.length);
        fieldsToIgnore[fieldsToIgnore.length - 1] = UUID_FIELD;
        return fieldsToIgnore;
    }
}
