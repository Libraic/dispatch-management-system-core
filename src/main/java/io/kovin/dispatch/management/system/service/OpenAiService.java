package io.kovin.dispatch.management.system.service;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService {

    private static final String OPEN_AI_API_MODEL = "gpt-5-mini";
    private static final String OPEN_AI_RESPONSES_URI = "/responses";

    private final WebClient openAiClient;

    public Map<String, Object> extractContractData(String contractText) {
        log.info("Extracting contract data from the contract text.");
        String prompt = getPrompt(contractText);
        Map<String, Object> body = getBody(prompt);
        return openAiClient
            .post()
            .uri(OPEN_AI_RESPONSES_URI)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
            })
            .block();
    }

    private Map<String, Object> getBody(String prompt) {
        return Map.of(
            "model", OPEN_AI_API_MODEL,
            "input", prompt
        );
    }

    private String getPrompt(String contractText) {
        return """
            You are an AI system that extracts structured data from transportation load confirmations and broker contracts.
            
              Your job is to read the contract text and extract load information.
            
              Return ONLY valid JSON matching the schema below.
            
              Rules:
              - Do NOT include explanations.
              - If a value cannot be found, return "" for strings and null for numbers/dates.
              - If multiple locations exist, include them in the locations list in chronological order.
              - The label field must be exactly one of:
                "Starting Point"
                "Delivery"
                "Pick Up"
                "Ending Point"
              - Most contracts contain Pick Up and Delivery locations.
            
              Definitions:
            
              Revenue:
              The total payment for the load (often labeled "Rate", "Total", "Carrier Pay", or "Revenue", but must be something else as well).
            
              Miles:
              Total miles for the load, if specified.
            
              Broker:
              The company that issued the load confirmation.
            
              Representative:
              The broker agent or contact person responsible for the load.
            
              Representative Contact Number:
              Phone number of the broker representative if present. You may have the extension of the phone number,
              something like "(415) 345-6789x1234". We only need the phone number in the following format: (415) 345-6789.
            
              Start Date:
              The earliest load-related date (usually the first Pick Up date).
            
              End Date:
              The final delivery date.
            
              Locations:
              A list of stops involved in the load, either to pick or deliver something.
            
              Each location should contain:
              - location: city and state; the location may be a full-fledged address, but right now, we only need the city and state, i.e. "Chicago, IL"
              - date: scheduled date for that stop
              - time: scheduled time if available (the time may be an internal; if that is the case, just extract the upper limit
                      of the interval, to have something like HH:mm)
              - label: "Pick Up", "Delivery", "Starting Point", or "Ending Point"
              - order: integer representing stop order starting from 1
            
              Example:
            
              If a contract contains:
            
              Pickup:
              Chicago, IL
              03/10/2026 08:00
            
              Delivery:
              Dallas, TX
              03/12/2026 14:00
            
              Then locations should be:
            
              [
                {
                  "location": "Chicago, IL",
                  "date": "2026-03-10",
                  "time": "08:00",
                  "label": "Pick Up",
                  "order": 1
                },
                {
                  "location": "Dallas, TX",
                  "date": "2026-03-12",
                  "time": "14:00",
                  "label": "Delivery",
                  "order": 2
                }
              ]
            
              If multiple pick ups or deliveries exist, include all of them.
            
              Return JSON in this exact format:
            
              {
                "revenue": null,
                "miles": null,
                "broker": "",
                "representative": "",
                "representativeContactNumber": "",
                "startDate": "",
                "endDate": "",
                "locations": [
                  {
                    "location": "",
                    "date": "",
                    "time": "",
                    "label": "",
                    "order": null
                  }
                ]
              }
            
              Contract text:
            
              %s
            """.formatted(contractText);
    }

    private String getDummyData() {
        try {
            Thread.sleep(5000);
            return "Dummy data";
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
