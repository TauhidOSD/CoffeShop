@echo off
title Coffee Shop Management System Launcher
echo ===================================================
echo     Coffee Shop Management System Launcher
echo ===================================================
echo.

echo [1/2] Compiling Java classes...
if not exist bin mkdir bin
powershell -Command "$files = Get-ChildItem -Path 'src' -Filter '*.java' -Recurse | Select-Object -ExpandProperty FullName; javac -cp 'lib/*' -d bin $files"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed! Please check your Java development kit setup.
    echo.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Launching Application GUI...
echo.
java -cp "bin;lib/*" com.coffeeshop.Main

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Application exited with error.
    echo.
    pause
)
