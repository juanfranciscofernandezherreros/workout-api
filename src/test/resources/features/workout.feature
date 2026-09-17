Feature: Workout API behavior
  Scenario: valid long-run POST
    Given request "POST /api/v1/workouts" with expected status 201
    When the request and response are printed
    Then the response status is 201

  Scenario: invalid POST
    Given request "POST /api/v1/workouts invalid" with expected status 400
    When the request and response are printed
    Then the response status is 400

  Scenario: GET existing workout
    Given request "GET /api/v1/workouts/1" with expected status 200
    When the request and response are printed
    Then the response status is 200

  Scenario: search workouts
    Given request "GET /api/v1/workouts?workoutType=LONG_RUN&page=0&size=20" with expected status 200
    When the request and response are printed
    Then the response status is 200

  Scenario: PATCH workout
    Given request "PATCH /api/v1/workouts/1" with expected status 200
    When the request and response are printed
    Then the response status is 200

  Scenario: DELETE workout
    Given request "DELETE /api/v1/workouts/1" with expected status 204
    When the request and response are printed
    Then the response status is 204

  Scenario: valid interval POST
    Given request "POST /api/v1/workouts intervals" with expected status 201
    When the request and response are printed
    Then the response status is 201

  Scenario: valid fartlek POST
    Given request "POST /api/v1/workouts fartlek" with expected status 201
    When the request and response are printed
    Then the response status is 201
