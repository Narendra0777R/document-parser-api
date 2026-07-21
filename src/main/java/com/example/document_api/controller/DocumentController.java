package com.example.document_api.controller;

import com.example.document_api.entity.DocumentMetadata;
import com.example.document_api.repository.DocumentRepository;
import com.example.document_api.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/documents")
@CrossOrigin(origins = {"http://localhost:5173", "https://document-parser-ui.vercel.app"})
//@CrossOrigin(origins = "*") // Allows your React frontend to connect
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentRepository documentRepository; // Needed for the GET endpoint

    // Injecting both the Service and the Repository
    public DocumentController(DocumentService documentService, DocumentRepository documentRepository) {
        this.documentService = documentService;
        this.documentRepository = documentRepository;
    }

    @PostMapping("/upload-bulk")
    public ResponseEntity<?> uploadBulkDocuments(
            @RequestParam("file") List<MultipartFile> files,
            // Catches the language string sent from React's FormData
            @RequestParam(value = "language", defaultValue = "eng") String language) {
        
        List<DocumentMetadata> processedDocuments = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                // Pass the file and the user's selected language to the Service
                DocumentMetadata metadata = documentService.processAndSaveDocument(file, language);
                processedDocuments.add(metadata);
            } catch (Exception e) {
                errors.add("Failed to process " + file.getOriginalFilename() + ": " + e.getMessage());
            }
        }
        
        // If everything failed, return a Bad Request with the list of errors
        if (processedDocuments.isEmpty() && !errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        // Return the successfully processed files with a 201 CREATED status
        return ResponseEntity.status(HttpStatus.CREATED).body(processedDocuments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDocumentMetadata(@PathVariable Long id) {
        Optional<DocumentMetadata> document = documentRepository.findById(id);
        
        if (document.isPresent()) {
            return ResponseEntity.ok(document.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found with ID: " + id);
        }
    }
}