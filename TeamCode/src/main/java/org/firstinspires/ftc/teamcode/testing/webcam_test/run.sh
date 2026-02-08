#!/bin/bash

# AprilTag Webcam Scanner - Run Script
# This script compiles and runs the AprilTag scanner application
# 
# Usage: ./run.sh
# 
# Requirements:
# - Java 11 or higher
# - FTC Vision libraries in lib/ directory
# - External USB webcam connected

echo "=========================================="
echo "AprilTag Webcam Scanner - Launcher"
echo "=========================================="
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java 11 or higher from java.com"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "\K[^"]*')
echo "Java version: $JAVA_VERSION"
echo ""

# Navigate to script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

echo "Working directory: $(pwd)"
echo ""

# Check if lib directory exists
if [ ! -d "lib" ]; then
    echo "WARNING: lib/ directory not found"
    echo "Creating lib/ directory..."
    mkdir -p lib
    echo "Please add FTC Vision libraries to lib/ directory"
fi

# Check if src directory exists
if [ ! -d "src" ]; then
    echo "ERROR: src/ directory not found"
    exit 1
fi

echo "Compiling source files..."
echo ""

# Compile Java files
javac -cp lib/* src/*.java 2>&1

if [ $? -ne 0 ]; then
    echo ""
    echo "ERROR: Compilation failed"
    echo "Please check the error messages above"
    exit 1
fi

echo ""
echo "Compilation successful!"
echo ""
echo "=========================================="
echo "Starting AprilTag Webcam Scanner..."
echo "=========================================="
echo ""

# Run the application
java -cp lib/*:src AprilTagWebcamScanner

# Capture exit code
EXIT_CODE=$?

echo ""
echo "=========================================="
echo "Application terminated with code: $EXIT_CODE"
echo "=========================================="

exit $EXIT_CODE
