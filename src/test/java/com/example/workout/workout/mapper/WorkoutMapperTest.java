package com.example.workout.workout.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.workout.generated.model.WorkoutCreateRequest;
import com.example.workout.generated.model.WorkoutLapRequest;
import com.example.workout.generated.model.WorkoutSegmentRequest;
import com.example.workout.generated.model.WorkoutSegmentType;
import com.example.workout.generated.model.WorkoutSource;
import com.example.workout.generated.model.WorkoutTrackPointRequest;
import com.example.workout.generated.model.WorkoutType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class WorkoutMapperTest {
    @Test
    void dto_entity_and_children_mapping_ok() {
        var segment = new WorkoutSegmentRequest();
        segment.setPosition(1);
        segment.setSegmentType(WorkoutSegmentType.INTERVAL);
        segment.setDistanceMeters(1000);
        segment.setDurationSeconds(240);
        segment.setRestDurationSeconds(90);

        var lap = new WorkoutLapRequest();
        lap.setPosition(1);
        lap.setDistanceMeters(1000);
        lap.setDurationSeconds(240);

        var point = new WorkoutTrackPointRequest();
        point.setPosition(1);
        point.setRecordedAt(OffsetDateTime.now());
        point.setLatitude(new BigDecimal("40.4168"));
        point.setLongitude(new BigDecimal("-3.7038"));

        var request = new WorkoutCreateRequest();
        request.setWorkoutDate(LocalDate.of(2026, 9, 20));
        request.setName("Intervals");
        request.setWorkoutType(WorkoutType.INTERVALS);
        request.setDistanceMeters(10000);
        request.setDurationSeconds(3000);
        request.setCompleted(true);
        request.setSource(WorkoutSource.MANUAL);
        request.setSegments(List.of(segment));
        request.setLaps(List.of(lap));
        request.setTrackPoints(List.of(point));

        var model = WorkoutMapper.fromCreateDto(request);
        model.setId(1L);
        model.setCreatedAt(OffsetDateTime.now());
        model.setUpdatedAt(model.getCreatedAt());
        assertThat(model.getSegments()).hasSize(1);
        assertThat(model.getLaps()).hasSize(1);
        assertThat(model.getTrackPoints()).hasSize(1);

        var entity = WorkoutEntityMapper.toEntity(model);
        assertThat(entity.getSegments()).hasSize(1);
        assertThat(entity.getLaps()).hasSize(1);
        assertThat(entity.getTrackPoints()).hasSize(1);

        var mappedBack = WorkoutEntityMapper.toModel(entity);
        var response = WorkoutMapper.toDto(mappedBack);
        var summary = WorkoutMapper.toSummaryDto(mappedBack);
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getSegments()).hasSize(1);
        assertThat(response.getLaps()).hasSize(1);
        assertThat(response.getTrackPoints()).hasSize(1);
        assertThat(summary.getWorkoutType()).isEqualTo(WorkoutType.INTERVALS);

        assertThat(WorkoutEntityMapper.toEntity(null)).isNull();
        assertThat(WorkoutEntityMapper.toModel(null)).isNull();
    }
}
