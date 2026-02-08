# Webcam AprilTag Scanner - Complete Index

## 📋 Documentation Files

### Getting Started
- **[README.md](README.md)** - Project overview and quick start
- **[QUICK_START.md](QUICK_START.md)** - 5-minute quick start guide
- **[TUTORIAL.md](TUTORIAL.md)** - Complete setup and usage tutorial
- **[MAKING.md](MAKING.md)** - Detailed file documentation and customization guide

### Resources
- **[resources/sample_apriltags.md](resources/sample_apriltags.md)** - AprilTag reference and printing guide

---

## 🚀 Quick Navigation

### I want to...

**Get started immediately**
→ Read [QUICK_START.md](QUICK_START.md) (5 minutes)

**Understand the project**
→ Read [README.md](README.md)

**Set up step-by-step**
→ Read [TUTORIAL.md](TUTORIAL.md)

**Customize the code**
→ Read [MAKING.md](MAKING.md)

**Learn about AprilTags**
→ Read [resources/sample_apriltags.md](resources/sample_apriltags.md)

**Run the application**
→ Execute `./run.sh` (Mac/Linux) or `run.bat` (Windows)

---

## 📁 Directory Structure

```
webcam_test/
│
├── 📄 Documentation
│   ├── README.md                    # Project overview
│   ├── QUICK_START.md              # 5-minute quick start
│   ├── TUTORIAL.md                 # Complete tutorial
│   ├── MAKING.md                   # File documentation
│   └── INDEX.md                    # This file
│
├── 🔧 Executable Scripts
│   ├── run.sh                      # Mac/Linux launcher
│   └── run.bat                     # Windows launcher
│
├── 📦 Source Code (src/)
│   ├── AprilTagWebcamScanner.java  # Main application (EDIT THIS)
│   ├── CameraManager.java          # Webcam control
│   ├── AprilTagProcessor.java      # Detection algorithm
│   ├── DisplayManager.java         # Visualization
│   └── AprilTagDetection.java      # Data structure
│
├── 📚 Libraries (lib/)
│   ├── ftc-vision-*.jar            # FTC Vision library
│   ├── opencv-*.jar                # OpenCV library
│   ├── apriltag-*.jar              # AprilTag library
│   └── (other dependencies)
│
├── 📋 Resources (resources/)
│   ├── sample_apriltags.md         # AprilTag reference
│   ├── apriltags/                  # AprilTag images
│   │   ├── tag_1.png
│   │   ├── tag_2.png
│   │   └── ...
│   └── config/
│       └── default_config.properties
│
├── 📊 Output (output/)
│   ├── detections.log              # Detection log
│   ├── frames/                     # Captured frames
│   └── statistics.txt              # Performance stats
│
└── 📝 Logs (logs/)
    ├── debug.log                   # Debug output
    ├── errors.log                  # Error messages
    └── performance.log             # Performance metrics
```

---

## 🎯 Main Components

### 1. AprilTagWebcamScanner.java
**Purpose**: Main application entry point  
**Edit**: Yes - customize detection parameters  
**Key Settings**:
- `USE_WEBCAM` - Use external or built-in camera
- `CAMERA_WIDTH` / `CAMERA_HEIGHT` - Resolution
- `CAMERA_FPS` - Frame rate
- `APRILTAG_DECIMATION` - Detection speed vs accuracy
- `DEBUG_MODE` - Verbose output

### 2. CameraManager.java
**Purpose**: Webcam initialization and frame capture  
**Edit**: Optional - advanced camera control  
**Key Methods**:
- `initialize()` - Set up camera
- `captureFrame()` - Get next frame
- `close()` - Release camera

### 3. AprilTagProcessor.java
**Purpose**: AprilTag detection algorithm  
**Edit**: Optional - advanced detection settings  
**Key Methods**:
- `initialize()` - Set up detector
- `processFrame()` - Detect tags in frame
- `close()` - Release resources

### 4. DisplayManager.java
**Purpose**: Visualization and display  
**Edit**: Optional - customize appearance  
**Key Methods**:
- `initialize()` - Create display window
- `displayFrame()` - Show frame with detections
- `shouldQuit()` - Check for quit command

### 5. AprilTagDetection.java
**Purpose**: Data structure for detection results  
**Edit**: Optional - add custom fields  
**Key Data**:
- Tag ID
- Position (X, Y, Z)
- Orientation (Pitch, Roll, Yaw)
- Distance measurements

---

## 🔧 Configuration Guide

### For High-Speed Detection
Edit `AprilTagWebcamScanner.java`:
```java
private static final int CAMERA_WIDTH = 320;
private static final int CAMERA_HEIGHT = 240;
private static final int APRILTAG_DECIMATION = 4;
private static final int CAMERA_FPS = 60;
```

### For High-Accuracy Detection
Edit `AprilTagWebcamScanner.java`:
```java
private static final int CAMERA_WIDTH = 1280;
private static final int CAMERA_HEIGHT = 720;
private static final int APRILTAG_DECIMATION = 1;
private static final int CAMERA_FPS = 30;
```

### For Debugging
Edit `AprilTagWebcamScanner.java`:
```java
private static final boolean DEBUG_MODE = true;
```

---

## 📖 Reading Order

### For First-Time Users
1. [README.md](README.md) - Understand what this is
2. [QUICK_START.md](QUICK_START.md) - Get it running
3. [TUTORIAL.md](TUTORIAL.md) - Learn how to use it
4. [resources/sample_apriltags.md](resources/sample_apriltags.md) - Learn about AprilTags

### For Developers
1. [MAKING.md](MAKING.md) - Understand the code
2. Source files in `src/` - Study the implementation
3. [TUTORIAL.md](TUTORIAL.md) - Advanced configuration
4. Customize and extend as needed

### For Integration
1. [MAKING.md](MAKING.md) - Module descriptions
2. Copy modules to your project
3. Integrate into your application
4. Customize as needed

---

## 🚀 Running the Application

### Quick Start
```bash
cd /Users/kv/Documents/ftc_main/frc/webcam_test
./run.sh          # Mac/Linux
# or
run.bat           # Windows
```

### Manual Compilation
```bash
javac -cp lib/* src/*.java
java -cp lib/*:src AprilTagWebcamScanner
```

### With Custom Settings
Edit `AprilTagWebcamScanner.java` first, then:
```bash
javac -cp lib/* src/*.java
java -cp lib/*:src AprilTagWebcamScanner
```

---

## 🔍 Troubleshooting

### Common Issues

**"Java not found"**
- Install Java 11+ from [java.com](https://www.java.com)

**"Webcam not found"**
- Check USB connection
- Try different USB port
- Verify camera permissions

**"No tags detected"**
- Ensure AprilTag is in focus
- Improve lighting
- Move closer to tag
- Check tag quality

**"Library not found"**
- Add FTC Vision JARs to `lib/` directory
- Verify JAR files are present

**"Compilation failed"**
- Check Java version (11+)
- Verify all source files present
- Check for syntax errors

See [TUTORIAL.md](TUTORIAL.md) for detailed troubleshooting.

---

## 📚 Learning Resources

### Official Documentation
- [FTC AprilTag Docs](https://ftc-docs.firstinspires.org/apriltag)
- [AprilTag Official](https://april.eecs.umich.edu/software/apriltag/)
- [OpenCV Docs](https://docs.opencv.org/)
- [Java Documentation](https://docs.oracle.com/javase/11/)

### Tutorials
- [FTC AprilTag Tutorial](https://ftc-docs.firstinspires.org/apriltag/vision_portal/apriltag_intro/apriltag-intro.html)
- [Pose Estimation](https://ftc-docs.firstinspires.org/apriltag-detection-values)

---

## �� Learning Path

### Beginner
1. Read README.md
2. Run QUICK_START.md
3. Point webcam at AprilTag
4. Observe console output

### Intermediate
1. Read TUTORIAL.md
2. Modify camera settings
3. Experiment with different AprilTags
4. Analyze detection output

### Advanced
1. Read MAKING.md
2. Study source code
3. Customize modules
4. Integrate into your project

---

## 📝 File Editing Checklist

### When Customizing
- [ ] Read relevant documentation
- [ ] Understand current behavior
- [ ] Make small changes
- [ ] Test thoroughly
- [ ] Document changes
- [ ] Update version number

### When Adding Features
- [ ] Create new Java file
- [ ] Add documentation
- [ ] Update README.md
- [ ] Add usage examples
- [ ] Test thoroughly
- [ ] Update version

---

## 🔗 Quick Links

| Resource | Link |
|----------|------|
| Quick Start | [QUICK_START.md](QUICK_START.md) |
| Full Tutorial | [TUTORIAL.md](TUTORIAL.md) |
| File Documentation | [MAKING.md](MAKING.md) |
| AprilTag Reference | [resources/sample_apriltags.md](resources/sample_apriltags.md) |
| FTC Docs | https://ftc-docs.firstinspires.org/apriltag |
| AprilTag Official | https://april.eecs.umich.edu/software/apriltag/ |

---

## 📞 Support

### Getting Help
1. Check [TUTORIAL.md](TUTORIAL.md) troubleshooting section
2. Review [MAKING.md](MAKING.md) for file details
3. Check console output for error messages
4. Enable DEBUG_MODE for verbose output

### Reporting Issues
1. Note the error message
2. Check troubleshooting guide
3. Review relevant documentation
4. Try suggested solutions

---

## 📊 Project Statistics

- **Total Files**: 10+
- **Source Code Files**: 5
- **Documentation Files**: 5
- **Lines of Code**: 2000+
- **Lines of Documentation**: 3000+
- **Supported Platforms**: Mac, Windows, Linux
- **Java Version Required**: 11+

---

## 🎯 Next Steps

1. **Get Started**: Run [QUICK_START.md](QUICK_START.md)
2. **Learn**: Read [TUTORIAL.md](TUTORIAL.md)
3. **Understand**: Study [MAKING.md](MAKING.md)
4. **Customize**: Edit source files
5. **Integrate**: Use modules in your project

---

## 📄 Version Information

- **Version**: 1.0
- **Last Updated**: 2024
- **Status**: Complete and Ready to Use
- **License**: FIRST Tech Challenge License

---

**Welcome to the AprilTag Webcam Scanner!**  
**Start with [QUICK_START.md](QUICK_START.md) to get up and running in 5 minutes.**
