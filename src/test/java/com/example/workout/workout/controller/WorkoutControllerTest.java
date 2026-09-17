package com.example.workout.workout.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.workout.generated.model.WorkoutCreateRequest;
import com.example.workout.generated.model.WorkoutPatchRequest;
import com.example.workout.generated.model.WorkoutSource;
import com.example.workout.generated.model.WorkoutType;
import com.example.workout.workout.model.Workout;
import com.example.workout.workout.model.WorkoutFilter;
import com.example.workout.workout.model.WorkoutPatchPresence;
import com.example.workout.workout.service.WorkoutService;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class WorkoutControllerTest {
    @Mock
    private WorkoutService service;

    @Test
    void crud_and_search_ok() {
        var controller = new WorkoutController(service);
        var request = new WorkoutCreateRequest();
        request.setWorkoutDate(LocalDate.of(2026, 9, 20));
        request.setWorkoutType(WorkoutType.LONG_RUN);
        request.setCompleted(true);
        request.setSource(WorkoutSource.MANUAL);
        var created = workout();
        when(service.create(any(Workout.class))).thenReturn(created);

        var createResponse = controller.createWorkout(request);
        assertThat(createResponse.getStatusCode().value()).isEqualTo(201);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getId()).isEqualTo(1L);

        when(service.get(1L)).thenReturn(created);
        var getResponse = controller.getWorkout(1L);
        assertThat(getResponse.getStatusCode().value()).isEqualTo(200);

        var pageable = PageRequest.of(0, 10);
        when(service.search(any(WorkoutFilter.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(created), pageable, 1));
        var searchResponse = controller.searchWorkouts(
                WorkoutType.LONG_RUN,
                LocalDate.of(2026, 9, 20),
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30),
                true,
                7L,
                WorkoutSource.MANUAL,
                pageable);
        assertThat(searchResponse.getBody()).isNotNull();
        assertThat(searchResponse.getBody().getContent()).hasSize(1);
        assertThat(searchResponse.getBody().getPage().getTotalElements()).isEqualTo(1L);

        var patch = new WorkoutPatchRequest();
        patch.setName(JsonNullable.of("Updated"));
        when(service.patch(eq(1L), any(Workout.class), any(WorkoutPatchPresence.class))).thenReturn(created);
        var patchResponse = controller.patchWorkout(1L, patch);
        assertThat(patchResponse.getStatusCode().value()).isEqualTo(200);

        doNothing().when(service).delete(1L);
        var deleteResponse = controller.deleteWorkout(1L);
        assertThat(deleteResponse.getStatusCode().value()).isEqualTo(204);
        verify(service).delete(1L);
    }

    private Workout workout() {
        var now = OffsetDateTime.now();
        return Workout.builder()
                .withId(1L)
                .withWorkoutDate(LocalDate.of(2026, 9, 20))
                .withName("Long run")
                .withWorkoutType(com.example.workout.workout.model.WorkoutType.LONG_RUN)
                .withDistanceMeters(20000)
                .withDurationSeconds(6000)
                .withCompleted(true)
                .withSource(com.example.workout.workout.model.WorkoutSource.MANUAL)
                .withCreatedAt(now)
                .withUpdatedAt(now)
                .build();
    }
}
