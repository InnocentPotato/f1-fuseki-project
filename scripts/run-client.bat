@echo off
echo ========================================
echo    Running F1 Fuseki Client
echo ========================================
echo.

cd /d "%~dp0\..\fuseki-client"

echo Compiling and running...
mvn clean compile exec:java -Dexec.mainClass=com.f1.fuseki.F1FusekiManager

pause