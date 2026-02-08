# Quick Start Guide - 5 Minutes to AprilTag Detection

## Step 1: Verify Prerequisites (1 minute)

### Check Java Installation
```bash
java -version
```

You should see Java 11 or higher. If not, install from [java.com](https://www.java.com)

### Connect Your Webcam
- Plug USB webcam into your computer
- Wait 2-3 seconds for recognition

### Prepare AprilTag
- Print or display an AprilTag (see resources/sample_apriltags.md)
- Ensure it's well-lit and clearly visible

## Step 2: Navigate to Project (1 minute)

```bash
cd /Users/kv/Documents/ftc_main/frc/webcam_test
```

## Step 3: Run the Application (3 minutes)

### On Mac/Linux:
```bash
chmod +x run.sh
./run.sh
```

### On Windows:
```cmd
run.bat
```

### Or Manually:
```bash
javac -cp lib/* src/*.java
java -cp lib/*:src AprilTagWebcamScanner
```

## Step 4: Point Your Webcam

1. A window will open showing your webcam feed
2. Point the camera at an AprilTag
3. Watch the console for detection output
4. Press 'Q' to quit

## Expected Output

```
========================================
AprilTag Webcam Scanner - RUNNING
========================================
Press 'Q' in the display window to quit
========================================

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
  Confidence: 0.95
```

## Troubleshooting

| Problem | Solution |
|---------|----------|
| "Java not found" | Install Java 11+ from java.com |
| "Webcam not found" | Check USB connection, try different port |
| "No tags detected" | Ensure tag is in focus, well-lit, and visible |
| "Library not found" | Add FTC Vision JARs to lib/ directory |

## Next Steps

1. Read **TUTORIAL.md** for detailed setup
2. Read **MAKING.md** for file documentation
3. Modify **AprilTagWebcamScanner.java** to customize
4. Check **resources/sample_apriltags.md** for tag reference

## Configuration

Edit `AprilTagWebcamScanner.java` to customize:

```java
// Change resolution (higher = better quality, slower)
private static final int CAMERA_WIDTH = 1280;
private static final int CAMERA_HEIGHT = 720;

// Change frame rate (higher = more responsive)
private static final int CAMERA_FPS = 60;

// Change detection speed (lower = more accurate)
private static final int APRILTAG_DECIMATION = 1;

// Enable debug output
private static final boolean DEBUG_MODE = true;
```

Then recompile and run.

## Key Files

- **AprilTagWebcamScanner.java** - Main application (edit this to customize)
- **CameraManager.java** - Webcam control
- **AprilTagProcessor.java** - Detection algorithm
- **DisplayManager.java** - Visualization
- **AprilTagDetection.java** - Data structure

## Support

- **TUTORIAL.md** - Complete setup guide
- **MAKING.md** - File documentation
- **README.md** - Project overview
- **resources/sample_apriltags.md** - AprilTag reference

---

**That's it! You're now detecting AprilTags with your webcam!**
