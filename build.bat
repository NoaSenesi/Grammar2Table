@echo off
echo Compiling G2T...
javac -d ./bin ./src/fr/senesi/g2t/*.java
if errorlevel 1 echo Error!
if not errorlevel 1 echo Done!