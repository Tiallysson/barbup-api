package com.barbup.barbup_api.exception;

import com.barbup.barbup_api.domain.dto.ErrorResponseDTO;
import com.barbup.barbup_api.domain.dto.ValidationErrorDetails;
import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationErrorDetails>> handleValidation(@NonNull MethodArgumentNotValidException ex) {
        List<ValidationErrorDetails> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> new ValidationErrorDetails(
                        f.getField(),
                        f.getDefaultMessage()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(@NonNull EntityNotFoundException ex) {
        var error = new ErrorResponseDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(@NonNull AccessDeniedException ex) {
        var error = new ErrorResponseDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(BadCredentialsException ex) {
        var error = new ErrorResponseDTO("Invalid email or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UsernameNotFoundException ex) {
        var error = new ErrorResponseDTO("Invalid email or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        var error = new ErrorResponseDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(EmailAlreadyVerifiedException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailAlreadyVerified(EmailAlreadyVerifiedException ex) {
        var error = new ErrorResponseDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidVerificationCode(InvalidVerificationCodeException ex) {
        var error = new ErrorResponseDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponseDTO> handleDisabled(DisabledException ex) {
        var error = new ErrorResponseDTO("Email not verified");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(InvalidBusinessHourException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidBusinessHour(InvalidBusinessHourException ex) {
        var error = new ErrorResponseDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BusinessHourConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessHourConflict(BusinessHourConflictException ex) {
        var error = new ErrorResponseDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        var error = new ErrorResponseDTO("Duplicate or invalid data");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        var error = new ErrorResponseDTO("Request body missing or malformed JSON");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        var error = new ErrorResponseDTO("Internal server error");
        return ResponseEntity.internalServerError().body(error);
    }
}
