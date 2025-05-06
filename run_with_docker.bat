@echo off
set CONTAINER_NAME=sea_cruises

REM Check if Docker Desktop is running
tasklist /FI "IMAGENAME eq Docker Desktop.exe" 2>NUL | find /I /N "Docker Desktop.exe">NUL
if "%ERRORLEVEL%"=="1" (
    echo Starting Docker Desktop...
    start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"
    echo Waiting for Docker Desktop to start...
    timeout /t 30 /nobreak
)

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
