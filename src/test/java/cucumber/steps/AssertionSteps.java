package cucumber.steps;

import static cucumber.utils.ITConstants.UUID_FIELD;
import static org.assertj.core.api.Assertions.assertThat;

import cucumber.utils.ClassUtils;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import io.cucumber.java.en.Then;
import io.cucumber.spring.ScenarioScope;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatusCode;

@RequiredArgsConstructor
@ScenarioScope
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

    private void compareFieldByFieldRecursively(Object actual, Object expected) throws IllegalAccessException {
        if (actual instanceof List<?> actualList && expected instanceof List<?> actualExpected) {
            for (int i = 0; i < actualList.size(); ++i) {
                Object currentActual = actualList.get(i);
                Object currentExpected = actualExpected.get(i);
                compareFieldByFieldRecursively(currentActual, currentExpected);
            }
        } else {
            if (actual == null && expected == null) {
                return;
            }

            Assertions.assertNotNull(actual);
            Assertions.assertNotNull(expected);
            Field[] fields = actual.getClass().getDeclaredFields();
            for (Field field : fields) {
                // Since UUID is dynamically generated, comparing it will make no sense.
                if (field.getName().equals(UUID_FIELD)) {
                    continue;
                }

                field.setAccessible(true);
                Object actualValue = field.get(actual);
                Object expectedValue = field.get(expected);
                if (!isInstanceOfPrimitiveClasses(actual)) {
                    compareFieldByFieldRecursively(actualValue, expectedValue);
                } else {
                    assertThat(expectedValue)
                        .withFailMessage(
                            "Expected value for field [%s] does not match the actual value [%s].",
                            field.getName(),
                            expectedValue,
                            actualValue
                        ).isEqualTo(actualValue);
                }
            }
        }
    }

    private boolean isInstanceOfPrimitiveClasses(Object object) {
        return object instanceof String
            || object instanceof Number
            || object instanceof LocalDate;
    }
}
