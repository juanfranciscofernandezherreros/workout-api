package com.example.workout.workout.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.workout.common.exception.AppException;
import com.example.workout.workout.entity.WorkoutEntity;
import com.example.workout.workout.model.Workout;
import com.example.workout.workout.model.WorkoutFilter;
import com.example.workout.workout.model.WorkoutPatchPresence;
import com.example.workout.workout.model.WorkoutSegment;
import com.example.workout.workout.model.WorkoutSegmentType;
import com.example.workout.workout.model.WorkoutSource;
import com.example.workout.workout.model.WorkoutType;
import com.example.workout.workout.repository.WorkoutRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceImplTest {
    @Mock
    private WorkoutRepository repository;

    @InjectMocks
    private WorkoutServiceImpl service;

    @Test
    void create_get_search_patch_and_delete_ok() {
        var entity = entity();
        when(repository.save(any(WorkoutEntity.class))).thenReturn(entity);
        var created = service.create(workout());
        assertThat(created.getId()).isEqualTo(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        assertThat(service.get(1L).getWorkoutType()).isEqualTo(WorkoutType.LONG_RUN);

        var pageable = PageRequest.of(0, 10);
        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 1));
        var filter = new WorkoutFilter(
                WorkoutType.LONG_RUN,
                LocalDate.of(2026, 9, 20),
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30),
                true,
                7L,
                WorkoutSource.MANUAL);
        assertThat(service.search(filter, pageable).getTotalElements()).isEqualTo(1);

        var patch = Workout.builder().withName("Updated").build();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(any(WorkoutEntity.class))).thenReturn(entity);
        var patched = service.patch(1L, patch, new WorkoutPatchPresence(Set.of("name")));
        assertThat(patched.getId()).isEqualTo(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        service.delete(1L);
        verify(repository).delete(entity);
    }

    @Test
    void missing_and_duplicate_positions_ko() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(99L)).isInstanceOf(AppException.class);

        var workout = Workout.builder()
                .withWorkoutDate(LocalDate.now())
                .withWorkoutType(WorkoutType.INTERVALS)
                .withCompleted(true)
                .withSource(WorkoutSource.MANUAL)
                .withSegments(new ArrayList<>(List.of(
                        WorkoutSegment.builder()
                                .withPosition(1)
                                .withSegmentType(WorkoutSegmentType.INTERVAL)
                                .build(),
                        WorkoutSegment.builder()
                                .withPosition(1)
                                .withSegmentType(WorkoutSegmentType.RECOVERY)
                                .build())))
                .build();
        assertThatThrownBy(() -> service.create(workout)).isInstanceOf(AppException.class);
        verify(repository, never()).save(any(WorkoutEntity.class));
    }

    private Workout workout() {
        return Workout.builder()
                .withWorkoutDate(LocalDate.of(2026, 9, 20))
                .withWorkoutType(WorkoutType.LONG_RUN)
                .withCompleted(true)
                .withSource(WorkoutSource.MANUAL)
                .build();
    }

    private WorkoutEntity entity() {
        var now = OffsetDateTime.now();
        var entity = new WorkoutEntity();
        entity.setId(1L);
        entity.setWorkoutDate(LocalDate.of(2026, 9, 20));
        entity.setWorkoutType(WorkoutType.LONG_RUN);
        entity.setCompleted(true);
        entity.setSource(WorkoutSource.MANUAL);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        return entity;
    }
}
