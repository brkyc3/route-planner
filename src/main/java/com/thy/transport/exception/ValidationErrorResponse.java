package com.thy.transport.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationErrorResponse {
    private int status;
    private String error;
    private Map<String, String> validationErrors;
    private LocalDateTime timestamp;
} 