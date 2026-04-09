package io.kovin.dispatch.management.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.response.load.GenericLoadResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@AllArgsConstructor
public class IngestionService {

    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;

    public GenericLoadResponse ingestDocument(MultipartFile file) {
        log.info("Starting ingesting the document.");
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            var response = openAiService.extractContractData(stripper.getText(document));
            log.debug("Extracted data from the document: [{}]", response);
            List<?> output = getOutput(response);
            if (output == null || output.isEmpty()) {
                log.debug("No output found in the response.");
                return null;
            }

            Map<?, ?> message = getMessage(output);
            if (message == null) {
                log.debug("No message found in the output.");
                return null;
            }

            List<?> content = getContent(message);
            if (content == null || content.isEmpty()) {
                log.debug("No content found in the message.");
                return null;
            }


            String jsonText = getJsonText(content);
            if (jsonText == null) {
                log.debug("No JSON text found in the content.");
                return null;
            }

            GenericLoadResponse genericLoadResponse = objectMapper.readValue(jsonText, GenericLoadResponse.class);
            log.debug("Converted the extracted data to GenericLoadResponse: [{}]", genericLoadResponse);
            return genericLoadResponse;
        } catch (IOException e) {
            throw DispatchManagementSystemException.ofInternal(e.getMessage());
        }
    }

    private List<?> getOutput(Map<String, Object> response) {
        Object output = response.get("output");
        if (output instanceof List) {
            return (List<?>) output;
        }
        return null;
    }

    private Map<?, ?> getMessage(List<?> output) {
        return output.stream()
            .filter(input -> input instanceof Map)
            .map(input -> (Map<?, ?>) input)
            .filter(o -> "message".equals(o.get("type")))
            .findFirst()
            .orElse(null);
    }

    private List<?> getContent(Map<?, ?> message) {
        Object o = message.get("content");
        if (o instanceof List) {
            return (List<?>) o;
        }
        return null;
    }

    private String getJsonText(List<?> content) {
        Object first = content.getFirst();
        if (first instanceof Map) {
            Object text = ((Map<?, ?>) first).get("text");
            if (text instanceof String textString) {
                return textString;
            }
        }

        return null;
    }
}
