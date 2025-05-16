package com.edstem.file_upload_storage.service;

import com.edstem.file_upload_storage.model.Document;
import com.edstem.file_upload_storage.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

@Service
public class DocumentService {

	@Value("${spring.file.upload-dir}")
	private String uploadDir;

	private final DocumentRepository documentRepository;

	public DocumentService(DocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}

	public Document uploadFile(MultipartFile multipartFile, String description) throws IOException {
		if (multipartFile.isEmpty()) {
			throw new RuntimeException("File is empty");
		}
		String fileName = multipartFile.getOriginalFilename();
		Path uploadPath = Paths.get(uploadDir);
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		Path filePath = uploadPath.resolve(fileName);
		Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		Document doc = new Document();
		doc.setName(multipartFile.getOriginalFilename());
		doc.setDescription(description);
		doc.setFilePath(filePath.toString());
		doc.setContentType(multipartFile.getContentType());
		doc.setSize(multipartFile.getSize());
		doc.setUploadDate(LocalDate.now());

		return documentRepository.save(doc);
	}

	public Resource downloadFile(Long id) throws IOException {
		Document doc = documentRepository.findById(id)
				.orElseThrow(() -> new FileNotFoundException("File not found"));

		Path path = Paths.get(doc.getFilePath());
		return new UrlResource(path.toUri());
	}

	public List<Document> listAllDocuments() {
		return documentRepository.findAll();
	}

	public void deleteFile(Long id) throws IOException {
		Document doc = documentRepository.findById(id)
				.orElseThrow(() -> new FileNotFoundException("File not found"));

		Path path = Paths.get(doc.getFilePath());
		Files.deleteIfExists(path);
		documentRepository.deleteById(id);
	}
}
