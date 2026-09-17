package com.example.workout.workout.model; import java.util.Set; public record WorkoutPatchPresence(Set<String> fields) { public boolean has(String field){return fields.contains(field);} }
