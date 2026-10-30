package com.example.backend.document;

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

    public record CreateDocumentRequest(String fileName, String fileType) {
    }

    public record ChangeStatusRequest(DocumentStatus status) {
    }

    @PostMapping
    public ResponseEntity<Document> create(@RequestBody CreateDocumentRequest request) {
        Document saved = documentRepository.save(
                new Document(request.fileName(), request.fileType()));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Document> list() {
        return documentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> get(@PathVariable Long id) {
        return documentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Document> changeStatus(
            @PathVariable Long id, @RequestBody ChangeStatusRequest request) {
        return documentRepository.findById(id)
                .map(document -> {
                    document.changeStatus(request.status());
                    return ResponseEntity.ok(documentRepository.save(document));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!documentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        documentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
