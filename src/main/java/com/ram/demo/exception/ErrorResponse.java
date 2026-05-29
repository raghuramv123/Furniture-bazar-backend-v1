package com.ram.demo.exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//── ErrorResponse.java ────────────────────────────────────────────────
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
 private int status;
 private String error;
 private String message;
 private String path;
 private LocalDateTime timestamp;
 private Map<String, String> fieldErrors; // populated only for validation errors
}