# Webcam AprilTag Scanner - Quick Start Guide

## Overview

This directory contains a standalone Java application that uses your computer's webcam to detect and track AprilTags in real-time. It's a simplified, independent version of the FTC Robot Controller's AprilTag detection system.

## What is an AprilTag?

AprilTags are fiducial markers (visual tags) that can be detected and tracked by computer vision systems. They provide:
- **Unique ID**: Each tag has a unique identifier
- **Position**: X, Y, Z coordinates relative to the camera
- **Orientation**: Pitch, Roll, Yaw angles
- **Distance**: Range from camera to tag

## Directory Structure

```
webcam_test/
├── README.md                          # This file - Quick start guide
├── TUTORIAL.md                        # Detailed step-by-step tutorial
├── MAKING.md                          # Complete documentation of all files
├── src/
│   └── AprilTagWebcamScanner.java    # Main application file
├── lib/
│   └── (FTC Vision libraries)         # Required dependencies
└── resources/
    └── sample_apriltags.md            # AprilTag reference guide
```

## Quick Start (5 Minutes)

### Prerequisites
- Java 11 or higher installed
- External USB webcam connected to your computer
- AprilTag printed or displayed on screen

### Step 1: Verify Java Installation
```bash
java -version
```

### Step 2: Compile the Application
```bash
cd /Users/kv/Documents/ftc_main/frc/webcam_test
javac -cp lib/* src/AprilTagWebcamScanner.java
```

### Step 3: Run the Application
```bash
java -cp lib/*:src AprilTagWebcamScanner
```

### Step 4: Point Your Webcam at an AprilTag
- A window will open showing your webcam feed
- Point the camera at an AprilTag
- Detection data will appear in the console

## What You'll See

When an AprilTag is detected, you'll see output like:
```
AprilTag Detected!
ID: 1
Position: X=2.5, Y=1.2, Z=8.3 (inches)
Orientation: Pitch=5.2°, Roll=-1.3°, Yaw=12.4°
Distance: 8.5 inches
```

## Troubleshooting

| Problem | Solution |
|---------|----------|
| "Webcam not found" | Check USB connection, try different USB port |
| "No tags detected" | Ensure AprilTag is in focus, well-lit, and visible |
| "Java not found" | Install Java 11+ from java.com |
| "Library not found" | Verify lib/ directory contains FTC Vision JARs |

## Next Steps

- Read **TUTORIAL.md** for detailed setup instructions
- Read **MAKING.md** for complete file documentation
- Modify **AprilTagWebcamScanner.java** to customize behavior
- See **sample_apriltags.md** for AprilTag reference

## Key Features

✅ Real-time AprilTag detection  
✅ Position and orientation tracking  
✅ Multiple tag detection  
✅ Console output with detection data  
✅ Webcam feed visualization  
✅ Easy to modify and extend  

## Support

For issues or questions:
1. Check TUTORIAL.md for detailed setup
2. Review MAKING.md for file explanations
3. Verify all dependencies are installed
4. Check console output for error messages

---

**Last Updated**: 2024  
**Version**: 1.0  
**Status**: Ready to Use
