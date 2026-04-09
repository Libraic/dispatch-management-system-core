package io.kovin.dispatch.management.system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OpenAiConfig {

    private static final String OPEN_AI_API_URL = "https://api.openai.com/v1";

    @Bean
    public WebClient openAiClient(@Value("${openai.api-key}") String apiKey) {
        return WebClient.builder()
            .baseUrl(OPEN_AI_API_URL)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
}
