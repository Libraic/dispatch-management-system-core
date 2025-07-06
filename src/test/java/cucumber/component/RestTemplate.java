package cucumber.component;

import static cucumber.utils.RestConstants.BASE_URL;

import lombok.Setter;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * The RestTemplate class is used to perform real-time API requests.
 */
@Component
public class RestTemplate {

    @Setter
    private int port;

    private final TestRestTemplate testRestTemplate;

    public RestTemplate(TestRestTemplate testRestTemplate) {
        this.testRestTemplate = testRestTemplate;
    }

    public <T> ResponseEntity<T> get(String path, Class<T> clazz) {
        String url = BASE_URL + port + path;
        return testRestTemplate.getForEntity(url, clazz);
    }

    public <R> ResponseEntity<?> post(Class<?> clazz, String endpoint, R request) {
        String url = BASE_URL + port + endpoint;
        return testRestTemplate.postForEntity(url, request, clazz);
    }
}
