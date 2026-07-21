package com.example.document_api.service;

import com.example.document_api.entity.DocumentMetadata;
import com.example.document_api.repository.DocumentRepository;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

 // 1. Add 'String language' to the method signature
    public DocumentMetadata processAndSaveDocument(MultipartFile file, String language) {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String extractedText = pdfStripper.getText(document).trim();

            if (extractedText.length() < 50) {
                // 2. Pass the language to the OCR method
                extractedText = performOcrExtraction(document, language);
            }

            int wordCount = extractedText.isEmpty() ? 0 : extractedText.split("\\s+").length;
            long fileSizeKb = file.getSize() / 1024;
            String emails = extractEmails(extractedText);
            String phones = extractPhoneNumbers(extractedText);
            
            DocumentMetadata metadata = new DocumentMetadata();
            metadata.setFileName(file.getOriginalFilename());
            metadata.setWordCount(wordCount);
            metadata.setFileSizeKb(fileSizeKb);
            metadata.setExtractedText(extractedText);
            
            metadata.setDetectedEmails(emails);
            metadata.setDetectedPhones(phones);

            return documentRepository.save(metadata);

        } catch (IOException e) {
            throw new RuntimeException("Failed to process the PDF document.", e);
        }
        
        
    }

    // 3. Add 'String language' here
    private String performOcrExtraction(PDDocument document, String language) {
        StringBuilder ocrText = new StringBuilder();
        Tesseract tesseract = new Tesseract();
        
        String tessDataPath = System.getenv("TESSDATA_PREFIX"); 
        if (tessDataPath == null || tessDataPath.isEmpty()) {
            tessDataPath = "C:\\Program Files\\Tesseract-OCR\\tessdata"; 
        }
        
        tesseract.setDatapath(tessDataPath);
        
        // 4. Inject the user's choice directly into the AI!
        tesseract.setLanguage(language); 

        PDFRenderer pdfRenderer = new PDFRenderer(document);

        try {
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                String textOnPage = tesseract.doOCR(bim);
                ocrText.append(textOnPage).append("\n");
            }
        } catch (IOException | TesseractException e) {
            System.err.println("OCR Extraction failed: " + e.getMessage());
        }

        return ocrText.toString().trim();
    } 

//Regex Helper 1: Finds Emails
private String extractEmails(String text) {
    // This pattern looks for [characters] @ [characters] . [domain]
    Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
    Matcher matcher = emailPattern.matcher(text);
    
    Set<String> emails = new HashSet<>(); // Using a Set prevents duplicate emails
    while (matcher.find()) {
        emails.add(matcher.group());
    }
    return String.join(", ", emails); // Returns a neat comma-separated string
}

// Regex Helper 2: Finds Phone Numbers
private String extractPhoneNumbers(String text) {
    // This pattern looks for common phone formats like (123) 456-7890, 123-456-7890, or +911234567890
    Pattern phonePattern = Pattern.compile("\\b(?:\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}\\b");
    Matcher matcher = phonePattern.matcher(text);
    
    Set<String> phones = new HashSet<>();
    while (matcher.find()) {
        phones.add(matcher.group());
    }
    return String.join(", ", phones);
} }