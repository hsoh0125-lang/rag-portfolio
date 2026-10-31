package com.example.backend.common;

public record ErrorResponse(int status, String error, String message) {
}
