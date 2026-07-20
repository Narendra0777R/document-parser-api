package com.example.document_api.controller;

import com.example.document_api.entity.DocumentMetadata;
import com.example.document_api.repository.DocumentRepository;
import com.example.document_api.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/documents")
@CrossOrigin(origins = "*") // Crucial for when you eventually build a React frontend
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentRepository documentRepository;

    public DocumentController(DocumentService documentService, DocumentRepository documentRepository) {
        this.documentService = documentService;
        this.documentRepository = documentRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file) {
        // Validate that the user actually sent a file
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: File is empty.");
        }
        
        // Validate that the file is actually a PDF
        if (file.getOriginalFilename() == null || !file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Only PDF files are supported.");
        }

        try {
            // Hand the file off to the brain of the app (the Service)
            DocumentMetadata savedMetadata = documentService.processAndSaveDocument(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMetadata);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDocumentMetadata(@PathVariable Long id) {
        // Ask the database for the document using its ID
        Optional<DocumentMetadata> document = documentRepository.findById(id);
        
        if (document.isPresent()) {
            return ResponseEntity.ok(document.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found with ID: " + id);
        }
    }
}