//package com.ram.demo.exception;
//
//import java.time.LocalDateTime;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.web.HttpRequestMethodNotSupportedException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
//
//
////── GlobalExceptionHandler.java ───────────────────────────────────────
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
// // 404
// @ExceptionHandler(ResourceNotFoundException.class)
// public ResponseEntity<ErrorResponse> handleNotFound(
//         ResourceNotFoundException ex, HttpServletRequest req) {
//     return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), req);
// }
//
// // 409 — duplicate email
// @ExceptionHandler(EmailAlreadyExistsException.class)
// public ResponseEntity<ErrorResponse> handleEmailExists(
//         EmailAlreadyExistsException ex, HttpServletRequest req) {
//     return build(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), req);
// }
//
// // 409 — duplicate resource (slug, SKU, review)
// @ExceptionHandler(DuplicateResourceException.class)
// public ResponseEntity<ErrorResponse> handleDuplicate(
//         DuplicateResourceException ex, HttpServletRequest req) {
//     return build(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), req);
// }
//
// // 409 — insufficient stock
// @ExceptionHandler(InsufficientStockException.class)
// public ResponseEntity<ErrorResponse> handleStock(
//         InsufficientStockException ex, HttpServletRequest req) {
//     return build(HttpStatus.CONFLICT, "Insufficient Stock", ex.getMessage(), req);
// }
//
// // 400 — general bad request
// @ExceptionHandler(BadRequestException.class)
// public ResponseEntity<ErrorResponse> handleBadRequest(
//         BadRequestException ex, HttpServletRequest req) {
//     return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), req);
// }
//
// // 400 — payment signature mismatch
// @ExceptionHandler(PaymentVerificationException.class)
// public ResponseEntity<ErrorResponse> handlePayment(
//         PaymentVerificationException ex, HttpServletRequest req) {
//     return build(HttpStatus.BAD_REQUEST, "Payment Verification Failed",
//             ex.getMessage(), req);
// }
//
// // 401 — bad JWT or refresh token
// @ExceptionHandler(InvalidTokenException.class)
// public ResponseEntity<ErrorResponse> handleInvalidToken(
//         InvalidTokenException ex, HttpServletRequest req) {
//     return build(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), req);
// }
//
// // 401 — wrong password
// @ExceptionHandler(BadCredentialsException.class)
// public ResponseEntity<ErrorResponse> handleBadCredentials(
//         BadCredentialsException ex, HttpServletRequest req) {
//     return build(HttpStatus.UNAUTHORIZED, "Unauthorized",
//             "Invalid email or password", req);
// }
//
// // 403 — accessing someone else's resource
// @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
// public ResponseEntity<ErrorResponse> handleAccessDenied(
//         org.springframework.security.access.AccessDeniedException ex,
//         HttpServletRequest req) {
//     return build(HttpStatus.FORBIDDEN, "Forbidden",
//             "You don't have permission to access this resource", req);
// }
//
// // 400 — @Valid annotation failures (field-level)
// @ExceptionHandler(MethodArgumentNotValidException.class)
// public ResponseEntity<ErrorResponse> handleValidation(
//         MethodArgumentNotValidException ex, HttpServletRequest req) {
//
//     Map<String, String> fieldErrors = new LinkedHashMap<>();
//     ex.getBindingResult().getFieldErrors().forEach(fe ->
//             fieldErrors.put(fe.getField(), fe.getDefaultMessage()));
//
//     ErrorResponse body = ErrorResponse.builder()
//             .status(HttpStatus.BAD_REQUEST.value())
//             .error("Validation Failed")
//             .message("One or more fields are invalid")
//             .path(req.getRequestURI())
//             .timestamp(LocalDateTime.now())
//             .fieldErrors(fieldErrors)
//             .build();
//
//     return ResponseEntity.badRequest().body(body);
// }
//
// // 400 — @RequestParam / @PathVariable type mismatch
// @ExceptionHandler(MethodArgumentTypeMismatchException.class)
// public ResponseEntity<ErrorResponse> handleTypeMismatch(
//         MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
//     String msg = String.format("Parameter '%s' should be of type %s",
//             ex.getName(), ex.getRequiredType() != null
//                     ? ex.getRequiredType().getSimpleName() : "unknown");
//     return build(HttpStatus.BAD_REQUEST, "Type Mismatch", msg, req);
// }
//
// // 405 — wrong HTTP method
// @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
// public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
//         HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
//     return build(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed",
//             ex.getMessage(), req);
// }
//
// // 500 — anything unhandled
// @ExceptionHandler(Exception.class)
// public ResponseEntity<ErrorResponse> handleGeneric(
//         Exception ex, HttpServletRequest req) {
//     return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
//             "An unexpected error occurred", req);
// }
//
// // ── builder helper ────────────────────────────────────────────────
// private ResponseEntity<ErrorResponse> build(HttpStatus status, String error,
//                                              String message, HttpServletRequest req) {
//     ErrorResponse body = ErrorResponse.builder()
//             .status(status.value())
//             .error(error)
//             .message(message)
//             .path(req.getRequestURI())
//             .timestamp(LocalDateTime.now())
//             .build();
//     return ResponseEntity.status(status).body(body);
// }
//}