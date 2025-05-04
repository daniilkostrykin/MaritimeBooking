@echo off
set CONTAINER_NAME=sea_cruises

REM Check if the container is running
for /f "tokens=*" %%i in ('docker ps -q -f name=%CONTAINER_NAME%') do set CONTAINER_ID=%%i

if "%CONTAINER_ID%"=="" (
    echo Starting Docker container %CONTAINER_NAME%...
    docker start %CONTAINER_NAME%
) else (
    echo Docker container %CONTAINER_NAME% is already running.
)

echo Running Maven application...
call mvn clean javafx:run

echo Application stopped. Stopping Docker container %CONTAINER_NAME%...
docker stop %CONTAINER_NAME%
