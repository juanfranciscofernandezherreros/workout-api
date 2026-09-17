package com.example.workout.shoe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.workout.common.exception.AppException;
import com.example.workout.shoe.entity.RunningShoeEntity;
import com.example.workout.shoe.model.RunningShoe;
import com.example.workout.shoe.repository.RunningShoeRepository;
import com.example.workout.workout.repository.WorkoutRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
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

@ExtendWith(MockitoExtension.class)
class RunningShoeServiceImplTest {
    @Mock
    private RunningShoeRepository repository;

    @Mock
    private WorkoutRepository workoutRepository;

    @InjectMocks
    private RunningShoeServiceImpl service;

    @Test
    void create_get_search_patch_and_delete_ok() {
        var entity = entity();
        when(repository.save(any(RunningShoeEntity.class))).thenReturn(entity);
        var created = service.create(RunningShoe.builder()
                .withBrand("Nike")
                .withModel("Pegasus")
                .withInitialDistanceMeters(0L)
                .withActive(true)
                .build());
        assertThat(created.getId()).isEqualTo(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        assertThat(service.get(1L).getBrand()).isEqualTo("Nike");

        var pageable = PageRequest.of(0, 10);
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));
        assertThat(service.search(pageable).getTotalElements()).isEqualTo(1);

        var patch = RunningShoe.builder()
                .withBrand("Adidas")
                .withModel("Boston")
                .withNickname("speed")
                .withPurchaseDate(LocalDate.of(2026, 2, 1))
                .withInitialDistanceMeters(2000L)
                .withActive(false)
                .withNotes("updated")
                .build();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        var patched = service.patch(1L, patch, Set.of(
                "brand", "model", "nickname", "purchaseDate", "initialDistanceMeters", "active", "notes"));
        assertThat(patched.getBrand()).isEqualTo("Adidas");
        assertThat(patched.getModel()).isEqualTo("Boston");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(workoutRepository.existsByShoeId(1L)).thenReturn(false);
        service.delete(1L);
        verify(repository).delete(entity);
    }

    @Test
    void missing_and_referenced_shoe_ko() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(99L)).isInstanceOf(AppException.class);

        var entity = entity();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(workoutRepository.existsByShoeId(1L)).thenReturn(true);
        assertThatThrownBy(() -> service.delete(1L)).isInstanceOf(AppException.class);
        verify(repository, never()).delete(entity);
    }

    private RunningShoeEntity entity() {
        var now = OffsetDateTime.now();
        return RunningShoeEntity.builder()
                .withId(1L)
                .withBrand("Nike")
                .withModel("Pegasus")
                .withInitialDistanceMeters(0L)
                .withActive(true)
                .withCreatedAt(now)
                .withUpdatedAt(now)
                .build();
    }
}
