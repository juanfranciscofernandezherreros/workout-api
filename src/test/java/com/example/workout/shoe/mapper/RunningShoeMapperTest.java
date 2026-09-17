package com.example.workout.shoe.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.workout.generated.model.RunningShoeCreateRequest;
import com.example.workout.generated.model.RunningShoePatchRequest;
import com.example.workout.shoe.entity.RunningShoeEntity;
import com.example.workout.shoe.model.RunningShoe;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;

class RunningShoeMapperTest {
    @Test
    void dto_entity_and_patch_mapping_ok() {
        var create = new RunningShoeCreateRequest();
        create.setBrand("Nike");
        create.setModel("Pegasus");
        create.setNickname("daily");
        create.setPurchaseDate(LocalDate.of(2026, 1, 1));
        create.setInitialDistanceMeters(1000L);
        create.setActive(true);
        create.setNotes("notes");

        var model = RunningShoeMapper.fromCreate(create);
        model.setId(1L);
        model.setCreatedAt(OffsetDateTime.now());
        model.setUpdatedAt(model.getCreatedAt());
        var dto = RunningShoeMapper.toDto(model);
        assertThat(dto.getBrand()).isEqualTo("Nike");
        assertThat(dto.getModel()).isEqualTo("Pegasus");

        var entity = RunningShoeEntityMapper.toEntity(model);
        var mappedBack = RunningShoeEntityMapper.toModel(entity);
        assertThat(mappedBack.getId()).isEqualTo(1L);
        assertThat(mappedBack.getNickname()).isEqualTo("daily");

        var patch = new RunningShoePatchRequest();
        patch.setBrand(JsonNullable.of("Adidas"));
        patch.setModel(JsonNullable.of("Boston"));
        patch.setNickname(JsonNullable.of("speed"));
        patch.setPurchaseDate(JsonNullable.of(LocalDate.of(2026, 2, 1)));
        patch.setInitialDistanceMeters(JsonNullable.of(2000L));
        patch.setActive(JsonNullable.of(false));
        patch.setNotes(JsonNullable.of("patched"));
        var result = RunningShoeMapper.fromPatch(patch);
        assertThat(result.fields()).containsExactlyInAnyOrder(
                "brand", "model", "nickname", "purchaseDate", "initialDistanceMeters", "active", "notes");
        assertThat(result.shoe().getBrand()).isEqualTo("Adidas");

        assertThat(RunningShoeEntityMapper.toModel((RunningShoeEntity) null)).isNull();
        assertThat(RunningShoeEntityMapper.toEntity((RunningShoe) null)).isNull();
    }
}
