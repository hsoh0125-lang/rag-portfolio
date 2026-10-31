package com.example.backend.document;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentRepository documentRepository;

    public DocumentController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public record CreateDocumentRequest(
            @NotBlank(message = "파일 이름은 비워둘 수 없습니다") String fileName,
            @NotBlank(message = "파일 종류는 비워둘 수 없습니다")
            @Pattern(regexp = "pdf|txt", message = "파일 종류는 pdf 또는 txt만 가능합니다")
            String fileType) {
    }

    public record ChangeStatusRequest(
            @NotNull(message = "상태 값이 필요합니다") DocumentStatus status) {
    }

    @PostMapping
    public ResponseEntity<Document> create(@Valid @RequestBody CreateDocumentRequest request) {
        Document saved = documentRepository.save(
                new Document(request.fileName(), request.fileType()));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Document> list() {
        return documentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Document get(@PathVariable Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));
    }

    @PatchMapping("/{id}/status")
    public Document changeStatus(
            @PathVariable Long id, @Valid @RequestBody ChangeStatusRequest request) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));
        document.changeStatus(request.status());
        return documentRepository.save(document);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!documentRepository.existsById(id)) {
            throw new DocumentNotFoundException(id);
        }
        documentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
