@echo off
REM AprilTag Webcam Scanner - Run Script (Windows)
REM This script compiles and runs the AprilTag scanner application
REM 
REM Usage: run.bat
REM 
REM Requirements:
REM - Java 11 or higher
REM - FTC Vision libraries in lib\ directory
REM - External USB webcam connected

echo.
echo ==========================================
echo AprilTag Webcam Scanner - Launcher
echo ==========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 11 or higher from java.com
    pause
    exit /b 1
)

REM Get Java version
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| find "version"') do set JAVA_VERSION=%%i
echo Java version: %JAVA_VERSION%
echo.

REM Get current directory
echo Working directory: %CD%
echo.

REM Check if lib directory exists
if not exist "lib" (
    echo WARNING: lib\ directory not found
    echo Creating lib\ directory...
    mkdir lib
    echo Please add FTC Vision libraries to lib\ directory
)

REM Check if src directory exists
if not exist "src" (
    echo ERROR: src\ directory not found
    pause
    exit /b 1
)

echo Compiling source files...
echo.

REM Compile Java files
javac -cp lib/* src/*.java

if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed
    echo Please check the error messages above
    pause
    exit /b 1
)

echo.
echo Compilation successful!
echo.
echo ==========================================
echo Starting AprilTag Webcam Scanner...
echo ==========================================
echo.

REM Run the application
java -cp lib/*;src AprilTagWebcamScanner

REM Capture exit code
set EXIT_CODE=%ERRORLEVEL%

echo.
echo ==========================================
echo Application terminated with code: %EXIT_CODE%
echo ==========================================
echo.

pause
exit /b %EXIT_CODE%
