package com.example.workout.common.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {
    @Test
    void domain_and_validation_errors_ok() {
        var handler = new GlobalExceptionHandler();
        var domain = handler.handle(new AppException(AppError.WORKOUT_NOT_FOUND));
        assertThat(domain.getStatusCode().value()).isEqualTo(404);
        assertThat(domain.getBody()).isNotNull();
        assertThat(domain.getBody().getCode()).isEqualTo("WORKOUT_NOT_FOUND");
        assertThat(domain.getBody().getTimestamp()).isNotNull();

        var binding = mock(BindingResult.class);
        when(binding.getFieldErrors()).thenReturn(List.of(new FieldError("workout", "name", "must not be blank")));
        var exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(binding);
        var validation = handler.validation(exception);
        assertThat(validation.getStatusCode().value()).isEqualTo(400);
        assertThat(validation.getBody()).isNotNull();
        assertThat(validation.getBody().getCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(validation.getBody().getFieldErrors()).containsEntry("name", "must not be blank");
    }
}
