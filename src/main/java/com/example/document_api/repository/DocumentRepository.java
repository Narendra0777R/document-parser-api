package com.example.document_api.repository;

import com.example.document_api.entity.DocumentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentMetadata, Long> {
}