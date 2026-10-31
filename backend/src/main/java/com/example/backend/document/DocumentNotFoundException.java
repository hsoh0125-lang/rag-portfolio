package com.example.backend.document;

public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException(Long id) {
        super("문서를 찾을 수 없습니다. id=" + id);
    }
}
