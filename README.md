# 📄 Smart Document Parser & OCR Engine

A robust, Full-Stack web application engineered to automate the extraction of critical data from multi-page PDF documents. The system utilizes a hybrid parsing approach, intelligently switching between standard text extraction and deep Optical Character Recognition (OCR) to ensure zero data loss, even with scanned images.

This project features an automated data-mining engine that utilizes Regular Expressions (Regex) to instantly locate, isolate, and extract contact information (emails and phone numbers) from highly unstructured text payloads.

---
# Live Link: [Click Me](https://document-parser-ui.vercel.app/)

## 📑 Table of Contents
1. [Project Overview](#-project-overview)
2. [System Architecture](#-system-architecture)
3. [Tech Stack](#-tech-stack)
4. [Database Schema](#-database-schema)
5. [Core Features & Logic](#-core-features--logic)
6. [API Documentation](#-api-documentation)
7. [Installation & Setup](#-installation-&-setup)
8. [Future Enhancements](#-future-enhancements)

---

## 🚀 Project Overview

Manual data entry from PDFs is time-consuming and error-prone. This application solves this by providing a simple UI to bulk-upload documents. The backend processes these documents concurrently, extracts the text, identifies key business entities (emails/phones), and stores the structured metadata in a relational database for immediate retrieval.

---

## 🏗️ System Architecture

1. **Client Layer:** React.js frontend allows users to select and upload multiple PDF files via a `multipart/form-data` request.
2. **Controller Layer:** Spring Boot REST API receives the files and routes them to the Service layer, handling any potential file corruption or format exceptions gracefully.
3. **Processing Engine:**
   * **Phase 1 (Standard):** Apache PDFBox attempts to strip embedded text.
   * **Phase 2 (Fallback):** If the text density is suspiciously low (< 50 characters), the system assumes the PDF is a scanned image and routes it to the Tesseract OCR engine.
   * **Phase 3 (Mining):** Extracted text is fed through Regex patterns to identify emails and phone numbers.
4. **Persistence Layer:** Structured data is mapped to JPA Entities and saved to a remote Aiven MySQL database.

---

## 🛠️ Tech Stack

### Frontend
* **Framework:** React.js (Vite)
* **Languages:** HTML5, CSS3, JavaScript
* **Hosting:** Vercel

### Backend
* **Framework:** Spring Boot 3.x (Java 17)
* **Text Extraction:** Apache PDFBox
* **OCR Engine:** Tess4J (Tesseract wrapper for Java)
* **Hosting:** Render

### Database
* **Database:** MySQL 8
* **ORM:** Spring Data JPA / Hibernate
* **Hosting:** Aiven Cloud

---

## 🗄️ Database Schema

The application stores document data in the `document_metadata` table.

| Column Name      | Data Type    | Description                                      |
|------------------|--------------|--------------------------------------------------|
| `id`             | BIGINT (PK)  | Auto-incrementing unique identifier.             |
| `file_name`      | VARCHAR(255) | Original name of the uploaded file.              |
| `file_size_kb`   | BIGINT       | Size of the document in kilobytes.               |
| `word_count`     | INT          | Total number of words successfully extracted.    |
| `extracted_text` | LONGTEXT     | The full raw text extracted from the document.   |
| `detected_emails`| VARCHAR(500) | Comma-separated list of unique emails found.     |
| `detected_phones`| VARCHAR(500) | Comma-separated list of unique phones found.     |
| `upload_date`    | TIMESTAMP    | Auto-generated timestamp via `@PrePersist`.      |

---

## 🧠 Core Features & Logic

### 1. The OCR Fallback Mechanism
The system ensures efficiency by not running OCR on every document. It first uses PDFBox to strip text. If the result is less than 50 characters (indicating a scanned image rather than a text-based PDF), it automatically initializes Tesseract to read the pixels.

### 2. Regex Data Mining
Custom Regular Expressions are used to scan the extracted text. To prevent database bloat, the Java `Set<String>` collection is utilized to automatically filter out duplicate emails or phone numbers before joining them into a comma-separated string.
* **Email Pattern:** `[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}`
* **Phone Pattern:** `\b(?:\+?\d{1,3}[-.\s]?)?\(?\d{3}\)?[-.\s]?\d{3}[-.\s]?\d{4}\b`

### 3. Fault-Tolerant Bulk Uploads
If a user uploads 10 PDFs and 1 is corrupted, the system will not crash. The backend catches the exception for that specific file, logs the error, successfully processes the remaining 9 files, and returns a unified response detailing both the successes and the failures.

---

## 📡 API Documentation

### 1. Bulk Upload Documents
Uploads multiple PDF files for processing and data extraction.

* **Endpoint:** `/api/v1/documents/upload-bulk`
* **Method:** `POST`
* **Headers:** `Content-Type: multipart/form-data`
* **Request Parameters:**
  * `file` (Required): Array of `.pdf` files.
  * `language` (Optional): OCR language code (default: `eng`).
* **Success Response (201 CREATED):**
  ```json
  [
    {
      "id": 1,
      "fileName": "invoice_scan.pdf",
      "fileSizeKb": 1024,
      "wordCount": 350,
      "extractedText": "Invoice #1024... [Full Text]",
      "detectedEmails": "billing@company.com, admin@company.com",
      "detectedPhones": "123-456-7890",
      "uploadDate": "2026-07-22T10:00:00"
    }
  ]
Partial/Full Failure (400 BAD REQUEST): Returns an array of error strings if files fail to process.

---

## ⚙️ Installation & Setup

### Prerequisites
* Java 17+
* Node.js & npm
* MySQL Database (Local or Cloud)
* **Tesseract OCR Installed Locally:**
  * Windows: [Download Installer](https://github.com/UB-Mannheim/tesseract/wiki) (Install to `C:\Program Files\Tesseract-OCR`)
  * Mac: `brew install tesseract`
  * Linux: `sudo apt-get install tesseract-ocr`

### Backend Setup
1. Clone the repository and open the backend folder in your IDE.
2. Open `src/main/resources/application.properties` and add your database credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/your_db_name
   spring.datasource.username=root
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update



### Run the application:

Bash
mvn spring-boot:run
Frontend Setup
Open a new terminal and navigate to the frontend folder.

Install dependencies:

Bash
npm install
Start the development server:

Bash
npm run dev
Access the application at http://localhost:5173.

## 🔮 Future Enhancements
* **Authentication:** Implement Spring Security with JWT to allow users to create accounts and manage their own isolated document repositories.

* **Export Functionality:** Add endpoints to export the structured database records into downloadable CSV or Excel files.

* **Multi-Language OCR Support:** Expand the frontend UI to allow users to dynamically select from 100+ Tesseract-supported languages before uploading.

---

## 🔗 Related Repositories
* **Frontend UI:** [View the React Frontend Repository Here](https://github.com/Narendra0777R/document-parser-ui)

---
**&copy; 2026 [Narendra0777](https://github.com/Narendra0777R).**
