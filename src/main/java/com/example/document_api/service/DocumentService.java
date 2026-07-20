package com.example.document_api.service;

import com.example.document_api.entity.DocumentMetadata;
import com.example.document_api.repository.DocumentRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    // Spring Boot automatically injects the repository here
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public DocumentMetadata processAndSaveDocument(MultipartFile file) throws IOException {
        String extractedText = "";
        
        // 1. Open the PDF file directly from the memory stream
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            extractedText = stripper.getText(document);
        }

        // 2. Calculate the metadata
        // We use split("\\s+") to count words based on whitespace. 
        // We must be careful to handle empty arrays correctly so we avoid off-by-one logic errors if the document is blank.
        String[] words = extractedText.trim().split("\\s+");
        int wordCount = extractedText.trim().isEmpty() ? 0 : words.length;
        
        long fileSizeKb = file.getSize() / 1024;

        // 3. Build the Entity object
        DocumentMetadata metadata = new DocumentMetadata();
        metadata.setFileName(file.getOriginalFilename());
        metadata.setUploadDate(LocalDateTime.now());
        metadata.setWordCount(wordCount);
        metadata.setFileSizeKb(fileSizeKb);
        metadata.setExtractedText(extractedText);

        // 4. Save it to MySQL using the Repository
        return documentRepository.save(metadata);
    }
}