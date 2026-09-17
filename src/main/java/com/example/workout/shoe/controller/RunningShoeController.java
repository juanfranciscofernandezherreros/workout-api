package com.example.workout.shoe.controller;

import com.example.workout.generated.api.ShoesApi;
import com.example.workout.generated.model.PageMetadata;
import com.example.workout.generated.model.RunningShoeCreateRequest;
import com.example.workout.generated.model.RunningShoePageResponse;
import com.example.workout.generated.model.RunningShoePatchRequest;
import com.example.workout.generated.model.RunningShoeResponse;
import com.example.workout.shoe.mapper.RunningShoeMapper;
import com.example.workout.shoe.service.RunningShoeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RunningShoeController implements ShoesApi {
    private final RunningShoeService service;

    @Override
    public ResponseEntity<RunningShoeResponse> createShoe(RunningShoeCreateRequest request) {
        var model = RunningShoeMapper.fromCreate(request);
        var created = service.create(model);
        var response = RunningShoeMapper.toDto(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<RunningShoeResponse> getShoe(Long id) {
        var model = service.get(id);
        var response = RunningShoeMapper.toDto(model);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<RunningShoePageResponse> searchShoes(Pageable pageable) {
        var page = service.search(pageable);
        var metadata = pageMetadata(page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
        var response = new RunningShoePageResponse();
        response.setContent(page.getContent().stream().map(RunningShoeMapper::toDto).toList());
        response.setPage(metadata);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<RunningShoeResponse> patchShoe(Long id, RunningShoePatchRequest request) {
        var patch = RunningShoeMapper.fromPatch(request);
        var updated = service.patch(id, patch.shoe(), patch.fields());
        var response = RunningShoeMapper.toDto(updated);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteShoe(Long id) {
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
