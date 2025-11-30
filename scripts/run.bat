@echo off
echo Cleaning and compiling...
call mvn clean compile

echo.
echo Running F1 Fuseki Client...
echo.
mvn exec:java -Dexec.mainClass=F1FusekiManager

pause