package com.example.workout.common.exception;

import com.example.workout.generated.model.ApiError;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiError> handle(AppException ex) {
        log.warn("[API] - ACTION: domainFailure: code: {}", ex.getError().code());
        ApiError body = error(ex.getError().code(), ex.getError().message());

        return ResponseEntity.status(ex.getError().status()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));
        ApiError body = error("VALIDATION_ERROR", "Request validation failed");
        body.setFieldErrors(fields);

        return ResponseEntity.badRequest().body(body);
    }

    private ApiError error(String code, String message) {
        ApiError body = new ApiError();
        body.setCode(code);
        body.setMessage(message);
        body.setTimestamp(OffsetDateTime.now());

        return body;
    }
}
