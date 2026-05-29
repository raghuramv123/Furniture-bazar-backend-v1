package com.ram.demo.exception;

// ── AccessDeniedException.java ────────────────────────────────────────
//Note: extend Spring's own so Security picks it up automatically
public class AccessDeniedException
     extends org.springframework.security.access.AccessDeniedException {
 public AccessDeniedException(String message) {
     super(message);
 }
}