@echo off
echo Starting Apache Jena Fuseki Server...
echo.

REM Check if Fuseki is downloaded
if not exist "..\apache-jena-fuseki-4.8.0" (
    echo Error: Apache Jena Fuseki not found!
    echo Please download from https://jena.apache.org/download/
    echo and extract to 'apache-jena-fuseki-4.8.0' in the parent directory
    pause
    exit /b 1
)

REM Copy configuration
xcopy "..\fuseki-server\fuseki-config.ttl" "..\apache-jena-fuseki-4.8.0\" /Y >nul
xcopy "..\fuseki-server\data" "..\apache-jena-fuseki-4.8.0\data\" /E /Y >nul

REM Start Fuseki
cd ..\apache-jena-fuseki-4.8.0
echo Fuseki Server starting on http://localhost:3030
echo Press Ctrl+C to stop the server
echo.
fuseki-server --config=fuseki-config.ttl

pause