@echo off
setlocal
set MVN_VERSION=3.9.11
set BASE_DIR=%~dp0
set MVN_HOME=%BASE_DIR%.mvn\apache-maven-%MVN_VERSION%
if not exist "%MVN_HOME%\bin\mvn.cmd" (
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$u='https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MVN_VERSION%/apache-maven-%MVN_VERSION%-bin.zip'; $z='%BASE_DIR%.mvn\maven.zip'; New-Item -Force -ItemType Directory '%BASE_DIR%.mvn' | Out-Null; Invoke-WebRequest $u -OutFile $z; Expand-Archive -Force $z '%BASE_DIR%.mvn'; Remove-Item $z"
)
call "%MVN_HOME%\bin\mvn.cmd" %*
