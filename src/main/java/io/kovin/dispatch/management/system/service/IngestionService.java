package io.kovin.dispatch.management.system.service;

import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class IngestionService {

    public void ingestDocument(MultipartFile file) {
        log.info("Starting ingesting the document.");
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            System.out.println(stripper.getText(document));
        } catch (IOException e) {
            throw DispatchManagementSystemException.ofInternal(e.getMessage());
        }
    }
}
