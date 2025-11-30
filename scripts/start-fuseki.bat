@echo off
echo ========================================
echo    Starting Apache Jena Fuseki 5.6.0
echo ========================================
echo.

REM Check if Fuseki exists
if not exist "fuseki-server\apache-jena-fuseki-5.6.0" (
    echo ERROR: Fuseki 5.6.0 not found!
    echo.
    echo Please:
    echo 1. Download apache-jena-fuseki-5.6.0.zip
    echo 2. Extract to: fuseki-server\apache-jena-fuseki-5.6.0\
    echo 3. Run this script again
    echo.
    pause
    exit /b 1
)

REM Create configuration directory
if not exist "fuseki-server\apache-jena-fuseki-5.6.0\run\configuration" (
    mkdir "fuseki-server\apache-jena-fuseki-5.6.0\run\configuration"
)

REM Copy configuration
copy "fuseki-config.ttl" "fuseki-server\apache-jena-fuseki-5.6.0\run\configuration\" >nul

REM Start Fuseki
cd fuseki-server\apache-jena-fuseki-5.6.0
echo Starting Fuseki Server...
echo.
echo Access the web interface at: http://localhost:3030
echo Press Ctrl+C to stop the server
echo.
fuseki-server --config=run/configuration/fuseki-config.ttl

pause