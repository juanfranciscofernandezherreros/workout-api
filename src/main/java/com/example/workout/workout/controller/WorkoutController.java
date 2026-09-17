package com.example.workout.workout.controller;

import com.example.workout.generated.api.WorkoutsApi;
import com.example.workout.generated.model.PageMetadata;
import com.example.workout.generated.model.WorkoutCreateRequest;
import com.example.workout.generated.model.WorkoutPageResponse;
import com.example.workout.generated.model.WorkoutPatchRequest;
import com.example.workout.generated.model.WorkoutResponse;
import com.example.workout.generated.model.WorkoutSource;
import com.example.workout.generated.model.WorkoutType;
import com.example.workout.workout.mapper.WorkoutMapper;
import com.example.workout.workout.mapper.WorkoutPatchMapper;
import com.example.workout.workout.model.WorkoutFilter;
import com.example.workout.workout.service.WorkoutService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkoutController implements WorkoutsApi {
    private final WorkoutService service;

    @Override
    public ResponseEntity<WorkoutResponse> createWorkout(WorkoutCreateRequest request) {
        var model = WorkoutMapper.fromCreateDto(request);
        var created = service.create(model);
        var response = WorkoutMapper.toDto(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<WorkoutResponse> getWorkout(Long id) {
        var model = service.get(id);
        var response = WorkoutMapper.toDto(model);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<WorkoutPageResponse> searchWorkouts(
            WorkoutType type,
            LocalDate date,
            LocalDate from,
            LocalDate to,
            Boolean completed,
            Long shoeId,
            WorkoutSource source,
            Pageable pageable) {
        var filter = new WorkoutFilter(
                type == null ? null : com.example.workout.workout.model.WorkoutType.valueOf(type.getValue()),
                date,
                from,
                to,
                completed,
                shoeId,
                source == null ? null : com.example.workout.workout.model.WorkoutSource.valueOf(source.getValue()));
        var result = service.search(filter, pageable);
        var response = new WorkoutPageResponse();
        response.setContent(result.getContent().stream().map(WorkoutMapper::toSummaryDto).toList());
        response.setPage(pageMetadata(result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages()));

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<WorkoutResponse> patchWorkout(Long id, WorkoutPatchRequest request) {
        var patch = WorkoutPatchMapper.toModel(request);
        var updated = service.patch(id, patch.workout(), patch.presence());
        var response = WorkoutMapper.toDto(updated);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteWorkout(Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    private PageMetadata pageMetadata(int page, int size, long totalElements, int totalPages) {
        var metadata = new PageMetadata();
        metadata.setPage(page);
        metadata.setSize(size);
        metadata.setTotalElements(totalElements);
        metadata.setTotalPages(totalPages);

        return metadata;
    }
}
