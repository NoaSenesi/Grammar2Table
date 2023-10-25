@echo off
echo Building G2T...
dir /s /B *.java > sources.txt
javac -d ./bin @sources.txt

if not errorlevel 1 (
	del sources.txt
	jar cfm g2t.jar manifest.txt -C bin .

	if not errorlevel 1 (
		echo Done!
	) else (
		echo Error!
	)
) else (
	del sources.txt
	echo Error!
)
