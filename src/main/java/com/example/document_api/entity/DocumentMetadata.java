package com.example.document_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class DocumentMetadata {

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public LocalDateTime getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(LocalDateTime uploadDate) {
		this.uploadDate = uploadDate;
	}

	public Integer getWordCount() {
		return wordCount;
	}

	public void setWordCount(Integer wordCount) {
		this.wordCount = wordCount;
	}

	public Long getFileSizeKb() {
		return fileSizeKb;
	}

	public void setFileSizeKb(Long fileSizeKb) {
		this.fileSizeKb = fileSizeKb;
	}

	public String getExtractedText() {
		return extractedText;
	}

	public void setExtractedText(String extractedText) {
		this.extractedText = extractedText;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    @Column(name = "word_count")
    private Integer wordCount;
    
    @Column(columnDefinition = "TEXT")
    private String detectedEmails;

    @Column(columnDefinition = "TEXT")
    private String detectedPhones;

    @Column(name = "file_size_kb")
    private Long fileSizeKb;

    // We use TEXT here because PDF content will be way longer than a standard 255-character string
    @Column(name = "extracted_text", columnDefinition = "LONGTEXT")
    private String extractedText;

    // You need an empty constructor for Hibernate to work
    public DocumentMetadata() {
    }

	public String getDetectedEmails() {
		return detectedEmails;
	}

	public void setDetectedEmails(String detectedEmails) {
		this.detectedEmails = detectedEmails;
	}

	public String getDetectedPhones() {
		return detectedPhones;
	}

	public void setDetectedPhones(String detectedPhones) {
		this.detectedPhones = detectedPhones;
	}

 

}