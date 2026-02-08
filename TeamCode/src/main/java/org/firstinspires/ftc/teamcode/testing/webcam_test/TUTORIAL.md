# AprilTag Webcam Scanner - Complete Setup Tutorial

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [System Setup](#system-setup)
3. [Installation Steps](#installation-steps)
4. [Running the Application](#running-the-application)
5. [Understanding the Output](#understanding-the-output)
6. [Troubleshooting](#troubleshooting)
7. [Advanced Configuration](#advanced-configuration)

---

## Prerequisites

Before you begin, ensure you have the following:

### Hardware Requirements
- **Computer**: Mac, Windows, or Linux
- **Webcam**: External USB webcam (recommended) or built-in camera
- **AprilTag**: Printed or displayed on screen (see resources/sample_apriltags.md)

### Software Requirements
- **Java**: Version 11 or higher
- **Git**: For cloning repositories (optional)
- **Terminal/Command Prompt**: For running commands

### Verify Java Installation

**On Mac/Linux:**
```bash
java -version
```

**On Windows:**
```cmd
java -version
```

You should see output like:
```
java version "11.0.x" or higher
```

If Java is not installed, download it from [java.com](https://www.java.com)

---

## System Setup

### Step 1: Connect Your Webcam

1. **Plug in your USB webcam** to an available USB port
2. **Wait 2-3 seconds** for the system to recognize it
3. **Verify connection**:
   - **Mac**: System Preferences → Security & Privacy → Camera (check if app is listed)
   - **Windows**: Device Manager → Cameras (should see your webcam listed)
   - **Linux**: `lsusb` command should show your webcam

### Step 2: Prepare Your AprilTag

You need at least one AprilTag to test. Options:

**Option A: Print an AprilTag**
1. Go to `resources/sample_apriltags.md`
2. Download or print an AprilTag (ID 1-12 recommended)
3. Print at least 4x4 inches
4. Ensure good lighting and contrast

**Option B: Display on Screen**
1. Open `resources/sample_apriltags.md`
2. Display an AprilTag on a second monitor or phone screen
3. Ensure it's well-lit and clearly visible

**Option C: Use Online Generator**
1. Visit: https://april.eecs.umich.edu/software/apriltag/
2. Generate a tag with ID 1-12
3. Print or display it

### Step 3: Prepare Your Workspace

Create a clean workspace:
```bash
# Navigate to the webcam_test directory
cd /Users/kv/Documents/ftc_main/frc/webcam_test

# Create necessary directories
mkdir -p src lib resources output logs
```

---

## Installation Steps

### Step 1: Copy FTC Libraries

The application requires FTC Vision libraries. Copy them from the FTC Robot Controller:

```bash
# Navigate to webcam_test directory
cd /Users/kv/Documents/ftc_main/frc/webcam_test

# Copy FTC libraries (if available)
# These should be in your FTC Robot Controller build directory
cp -r /Users/kv/Documents/ftc_main/frc/FtcRobotController/build/libs/* lib/
```

**If libraries are not available:**
1. Download FTC SDK from: https://github.com/FIRST-Tech-Challenge/FtcRobotController
2. Extract the libraries from the build directory
3. Place them in the `lib/` folder

### Step 2: Verify Library Structure

Check that your `lib/` directory contains:
```
lib/
├── ftc-vision-*.jar
├── opencv-*.jar
├── apriltag-*.jar
└── (other FTC libraries)
```

If not, download from the FTC GitHub repository.

### Step 3: Copy Source Files

The main Java files should already be in `src/`:
```
src/
├── AprilTagWebcamScanner.java
├── CameraManager.java
├── AprilTagProcessor.java
├── DisplayManager.java
└── AprilTagDetection.java
```

If any files are missing, they will be created in the next steps.

---

## Running the Application

### Method 1: Quick Start (Recommended)

**Step 1: Open Terminal**
```bash
cd /Users/kv/Documents/ftc_main/frc/webcam_test
```

**Step 2: Compile**
```bash
javac -cp lib/* src/*.java
```

**Step 3: Run**
```bash
java -cp lib/*:src AprilTagWebcamScanner
```

**Step 4: Point Your Webcam**
- A window will open showing your webcam feed
- Point the camera at an AprilTag
- Watch the console for detection output

### Method 2: Using a Script (Mac/Linux)

Create a file called `run.sh`:
```bash
#!/bin/bash
cd /Users/kv/Documents/ftc_main/frc/webcam_test
javac -cp lib/* src/*.java
java -cp lib/*:src AprilTagWebcamScanner
```

Make it executable:
```bash
chmod +x run.sh
```

Run it:
```bash
./run.sh
```

### Method 3: Using a Batch File (Windows)

Create a file called `run.bat`:
```batch
@echo off
cd C:\Users\YourUsername\Documents\ftc_main\frc\webcam_test
javac -cp lib/* src/*.java
java -cp lib/*:src AprilTagWebcamScanner
```

Run it by double-clicking the file.

---

## Understanding the Output

### Console Output

When the application starts, you'll see:
```
========================================
AprilTag Webcam Scanner v1.0
========================================

[INIT] AprilTag Webcam Scanner initializing...
[INIT] Initializing camera manager...
[INIT] Initializing AprilTag processor...
[INIT] Initializing display manager...
[INIT] All components initialized successfully
[START] AprilTag scanner starting...

========================================
AprilTag Webcam Scanner - RUNNING
========================================
Press 'Q' in the display window to quit
========================================
```

### Detection Output

When an AprilTag is detected:
```
--- AprilTag Detection Results ---
Detected: 1 tag(s)

[Tag 1]
  ID: 1
  Position (inches):
    X (Right):   2.50
    Y (Forward): 1.20
    Z (Up):      8.30
  Orientation (degrees):
    Pitch: 5.20
    Roll:  -1.30
    Yaw:   12.40
  Distance: 8.50 inches
  Bearing:  15.30°
  Elevation: 8.20°

----------------------------------
```

### Understanding the Values

| Value | Meaning | Range |
|-------|---------|-------|
| **ID** | Unique tag identifier | 0-587 |
| **X** | Horizontal distance (right) | -∞ to +∞ |
| **Y** | Forward distance | -∞ to +∞ |
| **Z** | Vertical distance (up) | -∞ to +∞ |
| **Pitch** | Rotation around X-axis | -180° to 180° |
| **Roll** | Rotation around Y-axis | -180° to 180° |
| **Yaw** | Rotation around Z-axis | -180° to 180° |
| **Distance** | Total distance from camera | 0 to ∞ |
| **Bearing** | Horizontal angle to tag | -180° to 180° |
| **Elevation** | Vertical angle to tag | -90° to 90° |

### Display Window

A window will open showing:
- **Live webcam feed** with tag outlines
- **Tag ID** displayed on detected tags
- **Crosshair** showing tag center
- **Coordinate axes** showing tag orientation

---

## Troubleshooting

### Problem: "Webcam not found"

**Cause**: Camera not detected or not connected

**Solutions**:
1. Check USB connection
2. Try a different USB port
3. Restart the application
4. Check System Preferences → Security & Privacy → Camera permissions
5. Try a different webcam

**Test command**:
```bash
# Mac/Linux
ls /dev/video*

# Windows
wmic logicaldisk get name
```

### Problem: "No tags detected"

**Cause**: AprilTag not visible or not recognized

**Solutions**:
1. Ensure AprilTag is in focus
2. Improve lighting (avoid shadows)
3. Move closer to the tag (within 2-3 feet)
4. Ensure tag is straight (not at extreme angles)
5. Check tag quality (not blurry or damaged)
6. Verify tag ID is in supported range (1-12 for standard tags)

**Test**: Print a high-quality AprilTag and hold it directly in front of camera

### Problem: "Library not found" or "ClassNotFoundException"

**Cause**: FTC libraries not in lib/ directory

**Solutions**:
1. Verify lib/ directory exists
2. Check that JAR files are present
3. Rebuild FTC Robot Controller to generate libraries
4. Download libraries from FTC GitHub

**Verify libraries**:
```bash
ls -la lib/
```

### Problem: "Java not found"

**Cause**: Java not installed or not in PATH

**Solutions**:
1. Install Java 11+ from java.com
2. Add Java to system PATH
3. Use full path to Java: `/usr/libexec/java_home -v 11`

**Verify Java**:
```bash
java -version
javac -version
```

### Problem: Application crashes immediately

**Cause**: Missing dependencies or configuration error

**Solutions**:
1. Check console output for specific error
2. Verify all source files are present
3. Ensure lib/ directory has all required JARs
4. Try recompiling: `javac -cp lib/* src/*.java`

**Debug mode**:
Edit `AprilTagWebcamScanner.java` and set:
```java
private static final boolean DEBUG_MODE = true;
```

### Problem: Low frame rate or slow detection

**Cause**: High resolution or slow computer

**Solutions**:
1. Reduce camera resolution in `AprilTagWebcamScanner.java`:
   ```java
   private static final int CAMERA_WIDTH = 320;  // was 640
   private static final int CAMERA_HEIGHT = 240; // was 480
   ```

2. Increase decimation factor:
   ```java
   private static final int APRILTAG_DECIMATION = 4; // was 3
   ```

3. Close other applications to free CPU

---

## Advanced Configuration

### Customizing Camera Settings

Edit `AprilTagWebcamScanner.java`:

```java
// Change resolution
private static final int CAMERA_WIDTH = 1280;   // Higher = better quality
private static final int CAMERA_HEIGHT = 720;

// Change frame rate
private static final int CAMERA_FPS = 60;       // Higher = more responsive

// Change decimation (affects speed vs accuracy)
private static final int APRILTAG_DECIMATION = 2; // Lower = more accurate
```

### Enabling Debug Output

Set debug mode to true:
```java
private static final boolean DEBUG_MODE = true;
```

This will print detailed information about:
- Initialization steps
- Frame processing
- Detection results
- Performance metrics

### Changing Update Interval

Adjust how often the display updates:
```java
private static final long UPDATE_INTERVAL_MS = 16; // ~60 FPS (was 33)
```

### Using Built-in Camera Instead of Webcam

Change this line:
```java
private static final boolean USE_WEBCAM = false; // was true
```

---

## Next Steps

1. **Read MAKING.md** for detailed file documentation
2. **Modify AprilTagWebcamScanner.java** to customize behavior
3. **Experiment with different AprilTags** (IDs 1-12)
4. **Integrate with your own code** by copying the modules
5. **Check sample_apriltags.md** for AprilTag reference

---

## Quick Reference

| Task | Command |
|------|---------|
| Compile | `javac -cp lib/* src/*.java` |
| Run | `java -cp lib/*:src AprilTagWebcamScanner` |
| Clean | `rm -rf src/*.class` |
| Debug | Set `DEBUG_MODE = true` |
| Quit | Press 'Q' in display window |

---

## Support Resources

- **FTC AprilTag Docs**: https://ftc-docs.firstinspires.org/apriltag
- **AprilTag Official**: https://april.eecs.umich.edu/software/apriltag/
- **OpenCV Docs**: https://docs.opencv.org/
- **Java Documentation**: https://docs.oracle.com/javase/11/

---

**Last Updated**: 2024  
**Version**: 1.0  
**Status**: Complete
