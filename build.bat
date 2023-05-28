@echo off
echo Compiling G2T...
dir /s /B *.java > sources.txt
javac -d ./bin @sources.txt
del sources.txt
if errorlevel 1 echo Error!
if not errorlevel 1 echo Done!