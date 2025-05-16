package com.edstem.file_upload_storage.controller;

import com.edstem.file_upload_storage.model.Document;
import com.edstem.file_upload_storage.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
	private final DocumentService documentService;

	public DocumentController(DocumentService documentService) {
		this.documentService = documentService;
	}
	@PostMapping("/upload")
	public ResponseEntity<Document> uploadFile(@RequestParam("file") MultipartFile file,
											   @RequestParam("description") String description) {
		try {
			Document saved = documentService.uploadFile(file, description);
			return ResponseEntity.ok(saved);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/download/{id}")
	public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
		try {
			Resource resource = documentService.downloadFile(id);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(resource);
		} catch (IOException e) {
			return ResponseEntity.notFound().build();
		}


	}
	@GetMapping
	public List<Document> listAll() {
		return documentService.listAllDocuments();
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteFile(@PathVariable Long id) {
		try {
			documentService.deleteFile(id);
			return ResponseEntity.ok("Deleted successfully");
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Delete failed");
		}
	}
}
