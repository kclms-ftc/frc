# MAKING.md - Complete Project Documentation

## Overview

This document provides comprehensive documentation of the webcam_test project structure, explaining every file, its purpose, and what can be edited to customize the application.

## Table of Contents

1. [Project Structure](#project-structure)
2. [File Descriptions](#file-descriptions)
3. [Editable Parameters](#editable-parameters)
4. [Module Descriptions](#module-descriptions)
5. [Customization Guide](#customization-guide)
6. [Integration Guide](#integration-guide)

---

## Project Structure

```
webcam_test/
│
├── README.md                          # Quick start guide
├── TUTORIAL.md                        # Detailed setup instructions
├── MAKING.md                          # This file - complete documentation
│
├── src/                               # Source code directory
│   ├── AprilTagWebcamScanner.java    # Main application entry point
│   ���── CameraManager.java            # Webcam capture and management
│   ├── AprilTagProcessor.java        # AprilTag detection algorithm
│   ├── DisplayManager.java           # Visualization and display
│   └── AprilTagDetection.java        # Detection data structure
│
├── lib/                               # External libraries (JARs)
│   ├── ftc-vision-*.jar              # FTC Vision library
│   ├── opencv-*.jar                  # OpenCV library
│   ├── apriltag-*.jar                # AprilTag library
│   └── (other dependencies)
│
├── resources/                         # Resource files
│   ├── sample_apriltags.md           # AprilTag reference guide
│   ├── apriltags/                    # AprilTag images
│   │   ├── tag_1.png
│   │   ├── tag_2.png
│   │   └── ...
│   └── config/                       # Configuration files
│       └── default_config.properties
│
├── output/                            # Output directory
│   ├── detections.log                # Detection log file
│   ├── frames/                       # Captured frames
│   └── statistics.txt                # Performance statistics
│
└── logs/                              # Log files
    ├── debug.log                     # Debug output
    ├── errors.log                    # Error messages
    └── performance.log               # Performance metrics
```

---

## File Descriptions

### Root Directory Files

#### `README.md`
- **Purpose**: Quick start guide for new users
- **Content**: 
  - Overview of the project
  - Quick start instructions (5 minutes)
  - Troubleshooting table
  - Key features list
- **Editable**: Yes
  - Update version number
  - Add custom features
  - Modify quick start steps
- **When to Edit**: When adding new features or changing quick start process

#### `TUTORIAL.md`
- **Purpose**: Comprehensive setup and usage guide
- **Content**:
  - Prerequisites and system setup
  - Step-by-step installation
  - Running instructions for different platforms
  - Output explanation
  - Troubleshooting guide
  - Advanced configuration
- **Editable**: Yes
  - Add platform-specific instructions
  - Update troubleshooting solutions
  - Add new configuration options
  - Include custom examples
- **When to Edit**: When adding new features or improving documentation

#### `MAKING.md`
- **Purpose**: Complete project documentation (this file)
- **Content**:
  - Project structure
  - File descriptions
  - Editable parameters
  - Module descriptions
  - Customization guide
- **Editable**: Yes
  - Add new sections
  - Document new files
  - Update parameter descriptions
  - Add examples
- **When to Edit**: When adding new files or parameters

---

### Source Code Files (`src/`)

#### `AprilTagWebcamScanner.java`
- **Purpose**: Main application entry point and control center
- **Responsibility**: 
  - Initialize all components
  - Manage main detection loop
  - Handle user input
  - Display statistics
  - Coordinate shutdown
- **Key Methods**:
  - `main(String[] args)`: Entry point
  - `initialize()`: Initialize all components
  - `start()`: Start the scanner
  - `mainLoop()`: Main detection loop
  - `displayDetections()`: Show detection results
  - `displayStatistics()`: Show performance stats
  - `shutdown()`: Clean up resources

**Editable Parameters** (lines 30-60):
```java
// Enable/disable webcam
private static final boolean USE_WEBCAM = true;

// Camera resolution
private static final int CAMERA_WIDTH = 640;
private static final int CAMERA_HEIGHT = 480;

// Camera frame rate
private static final int CAMERA_FPS = 30;

// AprilTag decimation (1=accurate, 3=fast)
private static final int APRILTAG_DECIMATION = 3;

// Debug output
private static final boolean DEBUG_MODE = true;

// Update interval (milliseconds)
private static final long UPDATE_INTERVAL_MS = 33;
```

**What You Can Edit**:
- Camera resolution (higher = better quality, slower)
- Frame rate (higher = more responsive, more CPU)
- Decimation factor (lower = more accurate, slower)
- Debug mode (true = verbose output)
- Update interval (lower = faster updates)

**Example Customizations**:
```java
// For high-speed detection
private static final int CAMERA_WIDTH = 320;
private static final int CAMERA_HEIGHT = 240;
private static final int APRILTAG_DECIMATION = 4;
private static final int CAMERA_FPS = 60;

// For high-accuracy detection
private static final int CAMERA_WIDTH = 1280;
private static final int CAMERA_HEIGHT = 720;
private static final int APRILTAG_DECIMATION = 1;
private static final int CAMERA_FPS = 30;
```

---

#### `CameraManager.java`
- **Purpose**: Handle webcam initialization and frame capture
- **Responsibility**:
  - Initialize camera device
  - Set resolution and frame rate
  - Capture frames continuously
  - Handle camera errors
  - Clean up camera resources
- **Key Methods**:
  - `CameraManager(boolean useWebcam, int width, int height, int fps)`: Constructor
  - `initialize()`: Initialize camera
  - `captureFrame()`: Get next frame
  - `close()`: Release camera
  - `isAvailable()`: Check if camera is ready

**Editable Parameters**:
- Camera device selection (webcam vs built-in)
- Resolution settings
- Frame rate
- Buffer size
- Timeout values

**What You Can Edit**:
- Camera selection logic
- Resolution constraints
- Frame rate limits
- Error handling behavior
- Timeout durations

**Example Customizations**:
```java
// Use specific camera device
private static final String CAMERA_DEVICE = "/dev/video0"; // Linux
private static final String CAMERA_DEVICE = "COM3"; // Windows

// Adjust buffer size
private static final int FRAME_BUFFER_SIZE = 5; // was 3

// Change timeout
private static final long CAPTURE_TIMEOUT_MS = 5000; // was 3000
```

---

#### `AprilTagProcessor.java`
- **Purpose**: Perform AprilTag detection on camera frames
- **Responsibility**:
  - Initialize AprilTag detector
  - Process frames for tag detection
  - Extract tag information
  - Handle detection errors
  - Manage processor resources
- **Key Methods**:
  - `AprilTagProcessor(int decimation)`: Constructor
  - `initialize()`: Initialize detector
  - `processFrame(byte[] frameData)`: Detect tags in frame
  - `close()`: Release resources
  - `setDecimation(int decimation)`: Adjust detection speed

**Editable Parameters**:
- Decimation factor (affects speed vs accuracy)
- Tag family (TAG_36h11, TAG_16h5, etc.)
- Detection threshold
- Pose estimation method
- Output units (inches, cm, meters)

**What You Can Edit**:
- Detection algorithm parameters
- Tag family selection
- Pose estimation settings
- Output unit conversion
- Error thresholds

**Example Customizations**:
```java
// Use different tag family
private static final String TAG_FAMILY = "TAG_16h5"; // was TAG_36h11

// Adjust detection threshold
private static final double DETECTION_THRESHOLD = 0.3; // was 0.5

// Change output units
private static final String OUTPUT_UNITS = "CENTIMETERS"; // was INCHES

// Adjust pose estimation
private static final boolean USE_FAST_POSE = true; // was false
```

---

#### `DisplayManager.java`
- **Purpose**: Visualize camera feed and detection results
- **Responsibility**:
  - Create display window
  - Render camera frames
  - Draw tag outlines and information
  - Handle user input
  - Update display in real-time
- **Key Methods**:
  - `DisplayManager(int width, int height)`: Constructor
  - `initialize()`: Create display window
  - `displayFrame(byte[] frameData, List<AprilTagDetection> detections)`: Show frame
  - `shouldQuit()`: Check for quit command
  - `close()`: Close display window

**Editable Parameters**:
- Window size
- Display colors
- Font sizes
- Drawing styles
- Overlay information

**What You Can Edit**:
- Window title and size
- Color scheme for tags
- Font sizes and styles
- Information displayed on tags
- Overlay elements (crosshairs, axes, etc.)

**Example Customizations**:
```java
// Change window size
private static final int WINDOW_WIDTH = 1280; // was 640
private static final int WINDOW_HEIGHT = 720; // was 480

// Change colors
private static final Color TAG_COLOR = Color.GREEN; // was RED
private static final Color TEXT_COLOR = Color.WHITE; // was YELLOW

// Adjust font size
private static final int FONT_SIZE = 16; // was 12

// Show/hide elements
private static final boolean SHOW_AXES = true;
private static final boolean SHOW_CROSSHAIR = true;
private static final boolean SHOW_ID = true;
```

---

#### `AprilTagDetection.java`
- **Purpose**: Data structure for storing detection information
- **Responsibility**:
  - Store tag ID
  - Store position (X, Y, Z)
  - Store orientation (Pitch, Roll, Yaw)
  - Store distance and angles
  - Provide getter methods
- **Key Methods**:
  - `getId()`: Get tag ID
  - `getX()`, `getY()`, `getZ()`: Get position
  - `getPitch()`, `getRoll()`, `getYaw()`: Get orientation
  - `getRange()`: Get distance
  - `getBearing()`, `getElevation()`: Get angles
  - `toString()`: Get string representation

**Editable Parameters**:
- Data precision (decimal places)
- Unit conversions
- Validation ranges
- Default values

**What You Can Edit**:
- Add new fields for additional data
- Change precision of calculations
- Add validation logic
- Modify string representation
- Add helper methods

**Example Customizations**:
```java
// Add confidence score
private double confidence;

// Add timestamp
private long timestamp;

// Add frame number
private int frameNumber;

// Add custom getter
public boolean isHighConfidence() {
    return confidence > 0.8;
}
```

---

### Resource Files (`resources/`)

#### `sample_apriltags.md`
- **Purpose**: Reference guide for AprilTags
- **Content**:
  - AprilTag explanation
  - Supported tag IDs
  - Tag images
  - Printing instructions
  - Quality guidelines
- **Editable**: Yes
  - Add new tag images
  - Update printing instructions
  - Add quality guidelines
  - Include custom tags
- **When to Edit**: When adding new AprilTags or improving documentation

#### `apriltags/` Directory
- **Purpose**: Store AprilTag images
- **Content**: PNG images of AprilTags (tag_1.png, tag_2.png, etc.)
- **Editable**: Yes
  - Add new tag images
  - Replace low-quality images
  - Add high-resolution versions
- **When to Edit**: When adding support for new tags

#### `config/default_config.properties`
- **Purpose**: Configuration file for default settings
- **Content**: Key-value pairs for application settings
- **Editable**: Yes
  - Change default values
  - Add new configuration options
  - Modify behavior settings
- **When to Edit**: When adding new configurable parameters

---

### Output and Log Files

#### `output/detections.log`
- **Purpose**: Log of all detected AprilTags
- **Content**: Timestamp, tag ID, position, orientation
- **Auto-generated**: Yes
- **Editable**: No (automatically generated)
- **Use**: Analyze detection history

#### `output/frames/`
- **Purpose**: Store captured camera frames
- **Content**: PNG images of frames with detections
- **Auto-generated**: Yes (if enabled)
- **Editable**: No (automatically generated)
- **Use**: Debug and analysis

#### `logs/debug.log`
- **Purpose**: Debug output and diagnostic information
- **Content**: Initialization steps, processing details, errors
- **Auto-generated**: Yes (if DEBUG_MODE = true)
- **Editable**: No (automatically generated)
- **Use**: Troubleshooting

---

## Editable Parameters

### In AprilTagWebcamScanner.java

| Parameter | Default | Range | Effect |
|-----------|---------|-------|--------|
| `USE_WEBCAM` | true | true/false | Use external webcam or built-in camera |
| `CAMERA_WIDTH` | 640 | 320-1920 | Camera resolution width |
| `CAMERA_HEIGHT` | 480 | 240-1080 | Camera resolution height |
| `CAMERA_FPS` | 30 | 15-60 | Frames per second |
| `APRILTAG_DECIMATION` | 3 | 1-4 | Detection speed (1=slow/accurate, 4=fast/rough) |
| `DEBUG_MODE` | true | true/false | Enable verbose output |
| `UPDATE_INTERVAL_MS` | 33 | 16-100 | Display update interval |

### In CameraManager.java

| Parameter | Default | Effect |
|-----------|---------|--------|
| `FRAME_BUFFER_SIZE` | 3 | Number of frames to buffer |
| `CAPTURE_TIMEOUT_MS` | 3000 | Timeout for frame capture |
| `CAMERA_DEVICE` | Auto | Specific camera device to use |

### In AprilTagProcessor.java

| Parameter | Default | Effect |
|-----------|---------|--------|
| `TAG_FAMILY` | TAG_36h11 | AprilTag family to detect |
| `DETECTION_THRESHOLD` | 0.5 | Minimum confidence for detection |
| `OUTPUT_UNITS` | INCHES | Units for position output |
| `USE_FAST_POSE` | false | Use faster pose estimation |

### In DisplayManager.java

| Parameter | Default | Effect |
|-----------|---------|--------|
| `WINDOW_WIDTH` | 640 | Display window width |
| `WINDOW_HEIGHT` | 480 | Display window height |
| `TAG_COLOR` | RED | Color for tag outlines |
| `TEXT_COLOR` | YELLOW | Color for text labels |
| `FONT_SIZE` | 12 | Font size for labels |
| `SHOW_AXES` | true | Display coordinate axes |
| `SHOW_CROSSHAIR` | true | Display crosshair |
| `SHOW_ID` | true | Display tag ID |

---

## Module Descriptions

### Module 1: Camera Capture Module

**Files**: `CameraManager.java`

**Purpose**: Capture video frames from webcam

**Functionality**:
- Initialize camera device
- Set resolution and frame rate
- Capture frames continuously
- Handle camera errors
- Release resources

**Customization Options**:
- Select specific camera device
- Adjust resolution
- Change frame rate
- Modify buffer size
- Adjust timeout values

**Example Usage**:
```java
CameraManager camera = new CameraManager(true, 640, 480, 30);
camera.initialize();
byte[] frame = camera.captureFrame();
camera.close();
```

---

### Module 2: AprilTag Detection Module

**Files**: `AprilTagProcessor.java`

**Purpose**: Detect AprilTags in video frames

**Functionality**:
- Initialize AprilTag detector
- Process frames for tag detection
- Extract tag information
- Calculate pose estimation
- Handle detection errors

**Customization Options**:
- Change tag family
- Adjust detection threshold
- Modify pose estimation method
- Change output units
- Adjust decimation factor

**Example Usage**:
```java
AprilTagProcessor processor = new AprilTagProcessor(3);
processor.initialize();
List<AprilTagDetection> detections = processor.processFrame(frameData);
processor.close();
```

---

### Module 3: Display Module

**Files**: `DisplayManager.java`

**Purpose**: Visualize camera feed and detections

**Functionality**:
- Create display window
- Render camera frames
- Draw tag outlines
- Display tag information
- Handle user input
- Update display in real-time

**Customization Options**:
- Change window size
- Modify colors
- Adjust font sizes
- Change displayed information
- Add custom overlays

**Example Usage**:
```java
DisplayManager display = new DisplayManager(640, 480);
display.initialize();
display.displayFrame(frameData, detections);
if (display.shouldQuit()) { /* exit */ }
display.close();
```

---

### Module 4: Data Structure Module

**Files**: `AprilTagDetection.java`

**Purpose**: Store and access detection data

**Functionality**:
- Store tag ID
- Store position coordinates
- Store orientation angles
- Store distance and bearing
- Provide data access methods

**Customization Options**:
- Add new fields
- Modify precision
- Add validation
- Add helper methods
- Change string representation

**Example Usage**:
```java
AprilTagDetection detection = detections.get(0);
int id = detection.getId();
double x = detection.getX();
double distance = detection.getRange();
```

---

## Customization Guide

### Customization 1: Change Detection Speed

**Goal**: Make detection faster (for real-time applications)

**Steps**:
1. Open `AprilTagWebcamScanner.java`
2. Find line with `APRILTAG_DECIMATION`
3. Change from `3` to `4`
4. Optionally reduce resolution:
   ```java
   private static final int CAMERA_WIDTH = 320;
   private static final int CAMERA_HEIGHT = 240;
   ```
5. Recompile and run

**Effect**: Faster detection but less accurate

---

### Customization 2: Change Detection Accuracy

**Goal**: Make detection more accurate (for precise positioning)

**Steps**:
1. Open `AprilTagWebcamScanner.java`
2. Find line with `APRILTAG_DECIMATION`
3. Change from `3` to `1`
4. Optionally increase resolution:
   ```java
   private static final int CAMERA_WIDTH = 1280;
   private static final int CAMERA_HEIGHT = 720;
   ```
5. Recompile and run

**Effect**: More accurate detection but slower

---

### Customization 3: Add Custom Output

**Goal**: Save detection data to file

**Steps**:
1. Open `AprilTagWebcamScanner.java`
2. Add import: `import java.io.*;`
3. Add field: `private FileWriter detectionLog;`
4. In `initialize()` method, add:
   ```java
   detectionLog = new FileWriter("detections.log", true);
   ```
5. In `displayDetections()` method, add:
   ```java
   detectionLog.write("Detection: " + detection.getId() + "\n");
   detectionLog.flush();
   ```
6. In `shutdown()` method, add:
   ```java
   detectionLog.close();
   ```

---

### Customization 4: Change Display Colors

**Goal**: Customize the display appearance

**Steps**:
1. Open `DisplayManager.java`
2. Find color definitions (around line 50)
3. Change colors:
   ```java
   private static final Color TAG_COLOR = Color.GREEN;
   private static final Color TEXT_COLOR = Color.WHITE;
   ```
4. Recompile and run

**Available Colors**: RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA, WHITE, BLACK

---

### Customization 5: Add Multiple Camera Support

**Goal**: Support multiple cameras simultaneously

**Steps**:
1. Open `CameraManager.java`
2. Modify to support multiple devices
3. Create array of cameras:
   ```java
   private CameraManager[] cameras;
   ```
4. Initialize multiple cameras in `initialize()`
5. Capture from all cameras in main loop

---

## Integration Guide

### How to Use These Modules in Your Own Project

#### Step 1: Copy the Modules

Copy these files to your project:
- `AprilTagProcessor.java`
- `CameraManager.java`
- `DisplayManager.java`
- `AprilTagDetection.java`

#### Step 2: Add Dependencies

Add FTC Vision libraries to your project's classpath:
```bash
javac -cp lib/* src/YourFile.java
```

#### Step 3: Import and Use

In your Java file:
```java
import java.util.List;

public class MyAprilTagApp {
    public static void main(String[] args) {
        // Initialize camera
        CameraManager camera = new CameraManager(true, 640, 480, 30);
        camera.initialize();
        
        // Initialize processor
        AprilTagProcessor processor = new AprilTagProcessor(3);
        processor.initialize();
        
        // Initialize display
        DisplayManager display = new DisplayManager(640, 480);
        display.initialize();
        
        // Main loop
        while (true) {
            byte[] frame = camera.captureFrame();
            List<AprilTagDetection> detections = processor.processFrame(frame);
            display.displayFrame(frame, detections);
            
            if (display.shouldQuit()) break;
        }
        
        // Cleanup
        display.close();
        processor.close();
        camera.close();
    }
}
```

#### Step 4: Customize as Needed

Modify parameters in each module to suit your needs.

---

## File Editing Checklist

### When Adding a New Feature

- [ ] Create new Java file in `src/`
- [ ] Add documentation to `MAKING.md`
- [ ] Update `README.md` with new feature
- [ ] Add usage example to `TUTORIAL.md`
- [ ] Update `AprilTagWebcamScanner.java` to use new feature
- [ ] Test thoroughly
- [ ] Update version number in files

### When Modifying Existing Code

- [ ] Update relevant documentation
- [ ] Test all affected functionality
- [ ] Update examples if needed
- [ ] Check for breaking changes
- [ ] Update version number

### When Adding New Configuration

- [ ] Add parameter to appropriate Java file
- [ ] Document in `MAKING.md`
- [ ] Add to configuration table
- [ ] Provide example values
- [ ] Update `TUTORIAL.md` if user-facing

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2024 | Initial release |
| 1.1 | TBD | Multiple camera support |
| 1.2 | TBD | Recording functionality |
| 2.0 | TBD | GUI interface |

---

## Contributing

To contribute improvements:

1. Test your changes thoroughly
2. Update documentation
3. Follow existing code style
4. Add comments for complex logic
5. Update version number
6. Create a summary of changes

---

## Support and Resources

- **FTC AprilTag Documentation**: https://ftc-docs.firstinspires.org/apriltag
- **AprilTag Official Site**: https://april.eecs.umich.edu/software/apriltag/
- **OpenCV Documentation**: https://docs.opencv.org/
- **Java Documentation**: https://docs.oracle.com/javase/11/

---

**Last Updated**: 2024  
**Version**: 1.0  
**Status**: Complete
