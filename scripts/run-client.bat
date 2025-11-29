@echo off
echo Running F1 Fuseki Client...
cd ../fuseki-client
mvn compile exec:java -Dexec.mainClass="com.f1.fuseki.F1FusekiManager"
pause