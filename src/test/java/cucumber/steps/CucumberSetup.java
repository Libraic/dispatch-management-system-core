package cucumber.steps;

import cucumber.component.RestTemplate;
import cucumber.config.ITConfig;
import cucumber.init.TestContainersContextInitializer;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.spring.CucumberContextConfiguration;
import io.kovin.dispatch.management.system.DispatchManagementSystemCoreApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@Slf4j
@CucumberContextConfiguration
@ContextConfiguration(classes = ITConfig.class, initializers = TestContainersContextInitializer.class)
@SpringBootTest(classes = { DispatchManagementSystemCoreApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberSetup {

    @LocalServerPort
    private int localServerPort;

    @Autowired
    private RestTemplate restTemplate;

    @Before
    public void start(Scenario scenario) {
        restTemplate.setPort(localServerPort);
        log.info(scenario.getName());
    }
}