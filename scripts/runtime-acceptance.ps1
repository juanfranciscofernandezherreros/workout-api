$ErrorActionPreference = "Stop"
$base = "http://localhost:8080"
$stamp = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
docker compose config | Out-Host
docker compose up -d --build
for ($i=0; $i -lt 60; $i++) { try { Invoke-RestMethod "$base/actuator/health/readiness" | Out-Null; break } catch { Start-Sleep 2 } }
$shoeBody = @{brand="Acceptance";model="Persist-$stamp";initialDistanceMeters=0;active=$true} | ConvertTo-Json
$shoe = Invoke-RestMethod -Method Post -Uri "$base/api/v1/shoes" -ContentType "application/json" -Body $shoeBody
$longBody = @{workoutDate=(Get-Date).ToString("yyyy-MM-dd");name="Persist-$stamp";workoutType="LONG_RUN";distanceMeters=18000;completed=$true;source="MANUAL";shoeId=$shoe.id} | ConvertTo-Json -Depth 8
$long = Invoke-RestMethod -Method Post -Uri "$base/api/v1/workouts" -ContentType "application/json" -Body $longBody
$intervalBody = @{workoutDate=(Get-Date).ToString("yyyy-MM-dd");name="Intervals-$stamp";workoutType="INTERVALS";completed=$true;source="MANUAL";segments=@(@{position=1;segmentType="INTERVAL";repetitions=6;distanceMeters=1000;restDurationSeconds=120})} | ConvertTo-Json -Depth 8
$interval = Invoke-RestMethod -Method Post -Uri "$base/api/v1/workouts" -ContentType "application/json" -Body $intervalBody
$fartlekBody = @{workoutDate=(Get-Date).ToString("yyyy-MM-dd");name="Fartlek-$stamp";workoutType="FARTLEK";completed=$true;source="MANUAL";segments=@(@{position=1;segmentType="FARTLEK_FAST";repetitions=10;durationSeconds=60;restDurationSeconds=60})} | ConvertTo-Json -Depth 8
$fartlek = Invoke-RestMethod -Method Post -Uri "$base/api/v1/workouts" -ContentType "application/json" -Body $fartlekBody
$password = if ($env:MSSQL_SA_PASSWORD) { $env:MSSQL_SA_PASSWORD } else { "LocalPassw0rd!" }
docker compose exec -T sqlserver /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $password -C -d workoutdb -Q "SELECT id,name FROM workouts WHERE id IN ($($long.id),$($interval.id),$($fartlek.id)); SELECT workout_id,position FROM workout_segments WHERE workout_id IN ($($interval.id),$($fartlek.id));" | Out-Host
docker volume inspect workout-api-sqlserver-data | Out-Host
docker compose down
docker compose up -d
for ($i=0; $i -lt 60; $i++) { try { Invoke-RestMethod "$base/actuator/health/readiness" | Out-Null; break } catch { Start-Sleep 2 } }
Invoke-RestMethod "$base/api/v1/workouts/$($long.id)" | ConvertTo-Json -Depth 8 | Out-Host
docker compose exec -T sqlserver /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $password -C -d workoutdb -Q "SELECT id,name FROM workouts WHERE id=$($long.id);" | Out-Host
docker volume inspect workout-api-sqlserver-data | Out-Host
Write-Host "Persistence acceptance retained workout id $($long.id) in volume workout-api-sqlserver-data"
