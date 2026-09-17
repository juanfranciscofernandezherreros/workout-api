CREATE TABLE running_shoes (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    brand NVARCHAR(100) NOT NULL,
    model NVARCHAR(150) NOT NULL,
    nickname NVARCHAR(100) NULL,
    purchase_date DATE NULL,
    initial_distance_meters BIGINT NOT NULL CONSTRAINT DF_running_shoes_initial_distance DEFAULT 0,
    active BIT NOT NULL CONSTRAINT DF_running_shoes_active DEFAULT 1,
    notes NVARCHAR(1000) NULL,
    created_at DATETIMEOFFSET NOT NULL,
    updated_at DATETIMEOFFSET NOT NULL,
    CONSTRAINT CK_running_shoes_initial_distance CHECK (initial_distance_meters >= 0)
);

CREATE TABLE workouts (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    workout_date DATE NOT NULL,
    name NVARCHAR(200) NULL,
    workout_type VARCHAR(30) NOT NULL,
    description NVARCHAR(2000) NULL,
    notes NVARCHAR(5000) NULL,
    distance_meters INT NULL,
    duration_seconds INT NULL,
    moving_time_seconds INT NULL,
    average_pace_seconds_per_km INT NULL,
    best_pace_seconds_per_km INT NULL,
    average_heart_rate INT NULL, max_heart_rate INT NULL,
    average_cadence INT NULL, max_cadence INT NULL,
    average_power_watts INT NULL, max_power_watts INT NULL,
    elevation_gain_meters INT NULL, elevation_loss_meters INT NULL,
    minimum_elevation_meters DECIMAL(10,2) NULL, maximum_elevation_meters DECIMAL(10,2) NULL,
    calories INT NULL, average_stride_length_meters DECIMAL(10,3) NULL, vertical_oscillation_cm DECIMAL(10,2) NULL, ground_contact_time_ms INT NULL,
    perceived_effort INT NULL, feeling VARCHAR(20) NULL, completed BIT NOT NULL CONSTRAINT DF_workouts_completed DEFAULT 1,
    temperature_celsius DECIMAL(5,2) NULL, humidity_percent INT NULL, wind_speed_kph DECIMAL(8,2) NULL,
    weather NVARCHAR(100) NULL, location NVARCHAR(200) NULL, route_name NVARCHAR(200) NULL,
    source VARCHAR(20) NOT NULL, external_activity_id NVARCHAR(200) NULL, shoe_id BIGINT NULL,
    created_at DATETIMEOFFSET NOT NULL, updated_at DATETIMEOFFSET NOT NULL,
    CONSTRAINT FK_workouts_shoe FOREIGN KEY (shoe_id) REFERENCES running_shoes(id),
    CONSTRAINT CK_workouts_type CHECK (workout_type IN ('EASY_RUN','RECOVERY_RUN','LONG_RUN','TEMPO_RUN','THRESHOLD_RUN','INTERVALS','REPETITIONS','FARTLEK','PROGRESSION_RUN','HILL_REPEATS','RACE','OTHER')),
    CONSTRAINT CK_workouts_feeling CHECK (feeling IS NULL OR feeling IN ('VERY_BAD','BAD','NORMAL','GOOD','VERY_GOOD')),
    CONSTRAINT CK_workouts_source CHECK (source IN ('MANUAL','GARMIN','STRAVA','COROS','POLAR','SUUNTO','APPLE','OTHER')),
    CONSTRAINT CK_workouts_distance CHECK (distance_meters IS NULL OR distance_meters > 0),
    CONSTRAINT CK_workouts_duration CHECK (duration_seconds IS NULL OR duration_seconds > 0),
    CONSTRAINT CK_workouts_moving_time CHECK (moving_time_seconds IS NULL OR moving_time_seconds > 0),
    CONSTRAINT CK_workouts_effort CHECK (perceived_effort IS NULL OR perceived_effort BETWEEN 1 AND 10),
    CONSTRAINT CK_workouts_humidity CHECK (humidity_percent IS NULL OR humidity_percent BETWEEN 0 AND 100)
);
CREATE INDEX IX_workouts_workout_date ON workouts(workout_date);
CREATE INDEX IX_workouts_workout_type ON workouts(workout_type);
CREATE INDEX IX_workouts_shoe_id ON workouts(shoe_id);
CREATE INDEX IX_workouts_source ON workouts(source);

CREATE TABLE workout_segments (
    id BIGINT IDENTITY(1,1) PRIMARY KEY, workout_id BIGINT NOT NULL, position INT NOT NULL, segment_type VARCHAR(30) NOT NULL, name NVARCHAR(200) NULL,
    repetitions INT NULL, distance_meters INT NULL, duration_seconds INT NULL, target_pace_seconds_per_km INT NULL, actual_pace_seconds_per_km INT NULL,
    rest_distance_meters INT NULL, rest_duration_seconds INT NULL, average_heart_rate INT NULL, max_heart_rate INT NULL, average_cadence INT NULL, average_power_watts INT NULL,
    elevation_gain_meters INT NULL, description NVARCHAR(1000) NULL,
    CONSTRAINT FK_workout_segments_workout FOREIGN KEY (workout_id) REFERENCES workouts(id) ON DELETE CASCADE,
    CONSTRAINT UQ_workout_segments_position UNIQUE (workout_id, position),
    CONSTRAINT CK_workout_segments_position CHECK (position >= 1),
    CONSTRAINT CK_workout_segments_type CHECK (segment_type IN ('WARM_UP','RUN','INTERVAL','RECOVERY','TEMPO','THRESHOLD','FARTLEK_FAST','FARTLEK_RECOVERY','HILL','COOL_DOWN','OTHER')),
    CONSTRAINT CK_workout_segments_repetitions CHECK (repetitions IS NULL OR repetitions > 0),
    CONSTRAINT CK_workout_segments_distance CHECK (distance_meters IS NULL OR distance_meters > 0),
    CONSTRAINT CK_workout_segments_duration CHECK (duration_seconds IS NULL OR duration_seconds > 0)
);
CREATE TABLE workout_laps (
    id BIGINT IDENTITY(1,1) PRIMARY KEY, workout_id BIGINT NOT NULL, position INT NOT NULL, distance_meters INT NULL, duration_seconds INT NULL, pace_seconds_per_km INT NULL,
    average_heart_rate INT NULL, max_heart_rate INT NULL, average_cadence INT NULL, average_power_watts INT NULL, elevation_gain_meters INT NULL, elevation_loss_meters INT NULL,
    CONSTRAINT FK_workout_laps_workout FOREIGN KEY (workout_id) REFERENCES workouts(id) ON DELETE CASCADE,
    CONSTRAINT UQ_workout_laps_position UNIQUE (workout_id, position), CONSTRAINT CK_workout_laps_position CHECK (position >= 1)
);
CREATE TABLE workout_track_points (
    id BIGINT IDENTITY(1,1) PRIMARY KEY, workout_id BIGINT NOT NULL, position INT NOT NULL, recorded_at DATETIMEOFFSET NULL, latitude DECIMAL(10,7) NOT NULL, longitude DECIMAL(10,7) NOT NULL,
    elevation_meters DECIMAL(10,2) NULL, heart_rate INT NULL, cadence INT NULL, power_watts INT NULL,
    CONSTRAINT FK_workout_track_points_workout FOREIGN KEY (workout_id) REFERENCES workouts(id) ON DELETE CASCADE,
    CONSTRAINT UQ_workout_track_points_position UNIQUE (workout_id, position), CONSTRAINT CK_workout_track_points_position CHECK (position >= 1),
    CONSTRAINT CK_workout_track_points_latitude CHECK (latitude BETWEEN -90 AND 90), CONSTRAINT CK_workout_track_points_longitude CHECK (longitude BETWEEN -180 AND 180)
);
