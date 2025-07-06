package cucumber.config;

import com.github.javafaker.Faker;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class BeanConfig {

    @Bean
    public TestRestTemplate testRestTemplate() {
        return new TestRestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Faker faker() {
        return new Faker();
    }
}
