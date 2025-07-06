package cucumber.steps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

/**
 * The ScenarioContext class acts as a holder for all the objects created during the execution of a certain scenario.
 * Since the steps are generic, a way to safely and reliably store the objects, so they can be used across steps,
 * is needed. The data is not shared across multiple tests, as this is guaranteed by the @ScenarioScope annotation.
 * Also, every class that is using the instance of the ScenarioContext object must use the @ScenarioScope annotation
 * as well.
 */
@Component
@ScenarioScope
public class ScenarioContext {

    private final Map<Class<?>, List<Object>> actualData;
    private final Map<Class<?>, List<Object>> expectedData;

    public ScenarioContext() {
        actualData = new HashMap<>();
        expectedData = new HashMap<>();
    }

    public void addActual(Class<?> clazz, Object object) {
        List<Object> items = actualData.computeIfAbsent(clazz, k -> new ArrayList<>());
        items.add(object);
    }

    public void addExpected(Class<?> clazz, Object object) {
        List<Object> items = expectedData.computeIfAbsent(clazz, k -> new ArrayList<>());
        items.add(object);
    }

    public Object getActual(Class<?> clazz) {
        List<Object> items = actualData.get(clazz);
        if (items == null) {
            throw new RuntimeException("No records were found for " + clazz.getPackageName() + "class.");
        }

        return items.getLast();
    }

    public Object getExpected(Class<?> clazz) {
        List<Object> items = expectedData.get(clazz);
        if (items == null) {
            throw new RuntimeException("No records were found for " + clazz.getPackageName() + "class.");
        }

        return items.getLast();
    }
}
