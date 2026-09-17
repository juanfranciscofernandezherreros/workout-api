package com.example.workout.shoe.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.workout.generated.model.RunningShoeCreateRequest;
import com.example.workout.generated.model.RunningShoePatchRequest;
import com.example.workout.shoe.model.RunningShoe;
import com.example.workout.shoe.service.RunningShoeService;
import java.time.OffsetDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class RunningShoeControllerTest {
    @Mock
    private RunningShoeService service;

    @Test
    void crud_and_search_ok() {
        var controller = new RunningShoeController(service);
        var now = OffsetDateTime.now();
        var shoe = RunningShoe.builder()
                .withId(1L)
                .withBrand("Nike")
                .withModel("Pegasus")
                .withInitialDistanceMeters(0L)
                .withActive(true)
                .withCreatedAt(now)
                .withUpdatedAt(now)
                .build();

        var create = new RunningShoeCreateRequest();
        create.setBrand("Nike");
        create.setModel("Pegasus");
        create.setInitialDistanceMeters(0L);
        create.setActive(true);
        when(service.create(any(RunningShoe.class))).thenReturn(shoe);

        var created = controller.createShoe(create);
        assertThat(created.getStatusCode().value()).isEqualTo(201);
        assertThat(created.getBody()).isNotNull();
        assertThat(created.getBody().getId()).isEqualTo(1L);

        when(service.get(1L)).thenReturn(shoe);
        var found = controller.getShoe(1L);
        assertThat(found.getStatusCode().value()).isEqualTo(200);

        var pageable = PageRequest.of(0, 10);
        when(service.search(pageable)).thenReturn(new PageImpl<>(java.util.List.of(shoe), pageable, 1));
        var searched = controller.searchShoes(pageable);
        assertThat(searched.getBody()).isNotNull();
        assertThat(searched.getBody().getContent()).hasSize(1);
        assertThat(searched.getBody().getPage().getTotalElements()).isEqualTo(1L);

        var patch = new RunningShoePatchRequest();
        patch.setNickname(JsonNullable.of("daily"));
        when(service.patch(anyLong(), any(RunningShoe.class), any(Set.class))).thenReturn(shoe);
        var patched = controller.patchShoe(1L, patch);
        assertThat(patched.getStatusCode().value()).isEqualTo(200);

        doNothing().when(service).delete(1L);
        var deleted = controller.deleteShoe(1L);
        assertThat(deleted.getStatusCode().value()).isEqualTo(204);
        verify(service).delete(1L);
    }
}
