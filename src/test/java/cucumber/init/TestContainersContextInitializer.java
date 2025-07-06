package cucumber.init;

import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class TestContainersContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String DB_ADMIN_USERNAME = "postgres";
    private static final String DB_ADMIN_PASSWORD = "postgres";
    private static final String POSTGRES_IMAGE_NAME = "postgres:16.1";
    private static final String DATASOURCE_URL_PROPERTY = "spring.datasource.url";
    private static final String DATASOURCE_USERNAME_PROPERTY = "spring.datasource.username";
    private static final String DATASOURCE_PASSWORD_PROPERTY = "spring.datasource.password";

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        log.info("Initializing the Cucumber context.");
        Stream<String> propertiesStream = startMongoDBContainer().stream();
        TestPropertyValues testPropertyValues = TestPropertyValues.of(propertiesStream);
        testPropertyValues.applyTo(applicationContext);
    }

    private List<String> startMongoDBContainer() {
        log.info("Proceeding to starting the PostgreSQL container");
        PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE_NAME))
            .withUsername(DB_ADMIN_USERNAME)
            .withPassword(DB_ADMIN_PASSWORD);
        postgresContainer.start();
        log.info("Started Postgres TestContainer at: {}", postgresContainer.getJdbcUrl());
        return List.of(
            String.format("%s=%s", DATASOURCE_URL_PROPERTY, postgresContainer.getJdbcUrl()),
            String.format("%s=%s", DATASOURCE_USERNAME_PROPERTY, postgresContainer.getUsername()),
            String.format("%s=%s", DATASOURCE_PASSWORD_PROPERTY, postgresContainer.getPassword())
        );
    }
}
