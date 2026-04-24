package com.daw.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.daw.services.exceptions.TareaException;
import com.daw.services.exceptions.TareaNotFoundException;
import com.daw.services.exceptions.TareaSecurityException;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TareaNotFoundException.class)
    public ResponseEntity<?> handleNotFound(TareaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TareaSecurityException.class)
    public ResponseEntity<?> handleSecurity(TareaSecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TareaException.class)
    public ResponseEntity<?> handleBadRequest(TareaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }
}