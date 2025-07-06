package cucumber.steps;

import static cucumber.utils.ITConstants.UUID_FIELD;
import static org.assertj.core.api.Assertions.assertThat;

import cucumber.utils.ClassUtils;
import java.lang.reflect.Field;
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

    /**
     * The method provides a generic interface to compare two objects of the same type. The name of the class must be
     * given, where each word that forms the name must be part of the original name of the class. Usually, in Java,
     * each class name contains multiple words, each starting with a capital letter. For example, DriverData. Therefore,
     * the string that depicts the name of the class must be given in the following format: "Driver Data". Basically each
     * keyword from the name of the original class must be a standalone word, with capital first letter as well, in this
     * plain class name parameter. The correspondence between the plain name of the class and the complete Java class
     * name is found in the ClassUtils utility class. A dedicated map for storing all the possible combinations needed
     * for the execution of the tests are found there.
     *
     * @param plainClassName the name of the class in plain text.
     * @throws IllegalAccessException if the field is not accessible, the following exception will be thrown.
     * @throws ClassNotFoundException if the class cannot be constructed from the provided class name, this exception
     *                                will be thrown.
     */
    @Then("the expected and actual {string} objects are equal")
    public void compareObjectsFieldByField(String plainClassName) throws IllegalAccessException, ClassNotFoundException {
        String fullClassName = ClassUtils.getFullClassName(plainClassName);
        assertThat(fullClassName)
            .withFailMessage("The %s class was not found.", fullClassName)
            .isNotNull();

        Class<?> clazz = Class.forName(fullClassName);
        Object actual = scenarioContext.getActual(clazz);
        Object expected = scenarioContext.getExpected(clazz);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(expected);
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // Since UUID is dynamically generated, comparing it will make no sense.
            if (field.getName().equals(UUID_FIELD)) {
                continue;
            }

            field.setAccessible(true);
            Object actualValue = field.get(actual);
            Object expectedValue = field.get(expected);

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
