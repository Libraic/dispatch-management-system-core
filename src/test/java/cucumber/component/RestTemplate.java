package cucumber.component;

import static cucumber.utils.RestConstants.BASE_URL;
import static org.springframework.http.HttpMethod.GET;

import io.kovin.dispatch.management.system.model.response.ApiResponse;
import lombok.Setter;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

/**
 * The RestTemplate class is used to perform real-time API requests.
 */
@Component
public class RestTemplate {

    @Setter
    private int port;

    private final TestRestTemplate restTemplate;

    public RestTemplate(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> ResponseEntity<T> get(String path, Class<T> clazz) {
        String url = BASE_URL + port + path;
        return restTemplate.getForEntity(url, clazz);
    }

    public <R> ResponseEntity<?> post(Class<?> clazz, String endpoint, R request) {
        String url = BASE_URL + port + endpoint;
        return restTemplate.postForEntity(url, request, clazz);
    }

    public <T, E> ResponseEntity<ApiResponse<List<T>, E>> getEntities(String basePath, Map<String, String> queryParams) {
        String url = BASE_URL + port + buildUrlFromQueryParams(basePath, queryParams);
        return restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<>() {});
    }

    private String buildUrlFromQueryParams(String basePath, Map<String, String> queryParams) {
        UriComponentsBuilder url = UriComponentsBuilder.fromPath(basePath);
        if (queryParams.isEmpty()) {
            return url.toUriString();
        }

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            url.queryParam(entry.getKey(), entry.getValue());
        }

        return url.toUriString();
    }
}
