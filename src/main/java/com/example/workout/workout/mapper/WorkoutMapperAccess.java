package com.example.workout.workout.mapper;

import com.example.workout.generated.model.WorkoutLapRequest;
import com.example.workout.generated.model.WorkoutSegmentRequest;
import com.example.workout.generated.model.WorkoutTrackPointRequest;
import com.example.workout.workout.model.WorkoutLap;
import com.example.workout.workout.model.WorkoutSegment;
import com.example.workout.workout.model.WorkoutSegmentType;
import com.example.workout.workout.model.WorkoutTrackPoint;

public final class WorkoutMapperAccess {

    private WorkoutMapperAccess() {
        throw new UnsupportedOperationException("This class should never be instantiated");
    }

    static WorkoutSegment segment(WorkoutSegmentRequest request) {
        WorkoutSegment segment = WorkoutSegment.builder()
                .withPosition(request.getPosition())
                .withSegmentType(WorkoutSegmentType.valueOf(request.getSegmentType().getValue()))
                .withName(request.getName())
                .withRepetitions(request.getRepetitions())
                .withDistanceMeters(request.getDistanceMeters())
                .withDurationSeconds(request.getDurationSeconds())
                .withRestDistanceMeters(request.getRestDistanceMeters())
                .withRestDurationSeconds(request.getRestDurationSeconds())
                .build();

        return segment;
    }

    static WorkoutLap lap(WorkoutLapRequest request) {
        WorkoutLap lap = WorkoutLap.builder()
                .withPosition(request.getPosition())
                .withDistanceMeters(request.getDistanceMeters())
                .withDurationSeconds(request.getDurationSeconds())
                .build();

        return lap;
    }

    static WorkoutTrackPoint point(WorkoutTrackPointRequest request) {
        WorkoutTrackPoint point = WorkoutTrackPoint.builder()
                .withPosition(request.getPosition())
                .withRecordedAt(request.getRecordedAt())
                .withLatitude(request.getLatitude())
                .withLongitude(request.getLongitude())
                .build();

        return point;
    }
}
