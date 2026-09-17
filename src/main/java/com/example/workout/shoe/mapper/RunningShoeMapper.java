package com.example.workout.shoe.mapper;

import com.example.workout.generated.model.RunningShoeCreateRequest;
import com.example.workout.generated.model.RunningShoePatchRequest;
import com.example.workout.generated.model.RunningShoeResponse;
import com.example.workout.shoe.model.RunningShoe;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import org.openapitools.jackson.nullable.JsonNullable;

public final class RunningShoeMapper {
    private RunningShoeMapper() {
        throw new UnsupportedOperationException("This class should never be instantiated");
    }

    public static RunningShoe fromCreate(RunningShoeCreateRequest dto) {
        RunningShoe shoe = RunningShoe.builder()
                .withBrand(dto.getBrand())
                .withModel(dto.getModel())
                .withNickname(dto.getNickname())
                .withPurchaseDate(dto.getPurchaseDate())
                .withInitialDistanceMeters(dto.getInitialDistanceMeters())
                .withActive(dto.getActive())
                .withNotes(dto.getNotes())
                .build();

        return shoe;
    }

    public static RunningShoeResponse toDto(RunningShoe shoe) {
        RunningShoeResponse dto = new RunningShoeResponse();
        dto.setId(shoe.getId());
        dto.setBrand(shoe.getBrand());
        dto.setModel(shoe.getModel());
        dto.setNickname(shoe.getNickname());
        dto.setPurchaseDate(shoe.getPurchaseDate());
        dto.setInitialDistanceMeters(shoe.getInitialDistanceMeters());
        dto.setActive(shoe.getActive());
        dto.setNotes(shoe.getNotes());
        dto.setCreatedAt(shoe.getCreatedAt());
        dto.setUpdatedAt(shoe.getUpdatedAt());

        return dto;
    }

    public static PatchResult fromPatch(RunningShoePatchRequest dto) {
        RunningShoe shoe = new RunningShoe();
        Set<String> fields = new HashSet<>();
        copy("brand", dto.getBrand(), fields, shoe::setBrand);
        copy("model", dto.getModel(), fields, shoe::setModel);
        copy("nickname", dto.getNickname(), fields, shoe::setNickname);
        copy("purchaseDate", dto.getPurchaseDate(), fields, shoe::setPurchaseDate);
        copy("initialDistanceMeters", dto.getInitialDistanceMeters(), fields, shoe::setInitialDistanceMeters);
        copy("active", dto.getActive(), fields, shoe::setActive);
        copy("notes", dto.getNotes(), fields, shoe::setNotes);

        return new PatchResult(shoe, fields);
    }

    private static <T> void copy(String field, JsonNullable<T> value, Set<String> fields, Consumer<T> consumer) {
        if (value != null && value.isPresent()) {
            fields.add(field);
            consumer.accept(value.orElse(null));
        }
    }

    public record PatchResult(RunningShoe shoe, Set<String> fields) {}
}
