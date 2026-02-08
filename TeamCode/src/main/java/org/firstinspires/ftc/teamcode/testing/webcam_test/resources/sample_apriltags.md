# AprilTag Reference Guide

## What is an AprilTag?

An AprilTag is a visual fiducial marker that can be detected and tracked by computer vision systems. It's similar to a QR code but optimized for robotics applications.

### Key Characteristics

- **Unique ID**: Each tag has a unique identifier (0-587)
- **Robust Detection**: Works in various lighting conditions
- **Pose Estimation**: Provides position and orientation
- **Fast Processing**: Can be detected in real-time
- **Scalable**: Works at various distances and sizes

---

## AprilTag Families

Different tag families have different properties:

### TAG_36h11 (Recommended for FTC)
- **Bits**: 36 bits
- **Hamming Distance**: 11
- **ID Range**: 0-586
- **Robustness**: High
- **Speed**: Medium
- **Use Case**: General purpose, FTC standard

### TAG_16h5
- **Bits**: 16 bits
- **Hamming Distance**: 5
- **ID Range**: 0-29
- **Robustness**: Medium
- **Speed**: Fast
- **Use Case**: High-speed applications

### TAG_25h9
- **Bits**: 25 bits
- **Hamming Distance**: 9
- **ID Range**: 0-242
- **Robustness**: High
- **Speed**: Medium
- **Use Case**: Balanced performance

---

## Standard FTC AprilTags (IDs 1-12)

These are the standard tags used in FTC competitions:

### Tag ID 1
```
┌─────────────────┐
│                 │
│    ┌─────────┐  │
│    │ 1       │  │
│    │         │  │
│    └─────────┘  │
│                 │
└─────────────────┘
```
- **Name**: Backdrop Tag 1
- **Use**: Field localization
- **Size**: 4" x 4" (recommended)

### Tag ID 2
- **Name**: Backdrop Tag 2
- **Use**: Field localization
- **Size**: 4" x 4" (recommended)

### Tag ID 3
- **Name**: Backdrop Tag 3
- **Use**: Field localization
- **Size**: 4" x 4" (recommended)

### Tag ID 4
- **Name**: Backdrop Tag 4
- **Use**: Field localization
- **Size**: 4" x 4" (recommended)

### Tag ID 5
- **Name**: Backdrop Tag 5
- **Use**: Field localization
- **Size**: 4" x 4" (recommended)

### Tag ID 6
- **Name**: Backdrop Tag 6
- **Use**: Field localization
- **Size**: 4" x 4" (recommended)

### Tag ID 7
- **Name**: Backdrop Tag 7
- **Use**: Field localization
- **Size**: 4" x 4" (recommended)

### Tag ID 8
- **Name**: Backdrop Tag 8
- **Use**: Field localization
- **Size**: 4" x 4" (recommended)

### Tag ID 9
- **Name**: Backdrop Tag 9
- **Use**: Field localization
- **Size**: 4" x 4" (recommended)

### Tag ID 10
- **Name**: Backdrop Tag 10
- **Use**: Field localization
- **Size**: 4" x 4" (recommended)

### Tag ID 11
- **Name**: Backdrop Tag 11
- **Use**: Field localization
- **Size**: 4" x 4" (recommended)

### Tag ID 12
- **Name**: Backdrop Tag 12
- **Use**: Field localization
- **Size**: 4" x 4" (recommended)

---

## Test Tags (IDs 200+)

These tags are useful for testing and development:

### Tag ID 200
- **Name**: Test Tag 1
- **Use**: Development and testing
- **Size**: 2" x 2" to 8" x 8"

### Tag ID 201
- **Name**: Test Tag 2
- **Use**: Development and testing
- **Size**: 2" x 2" to 8" x 8"

### Tag ID 202
- **Name**: Test Tag 3
- **Use**: Development and testing
- **Size**: 2" x 2" to 8" x 8"

---

## Printing AprilTags

### Step 1: Download Tag Image

Download AprilTag images from:
- Official: https://april.eecs.umich.edu/software/apriltag/
- FTC: https://ftc-docs.firstinspires.org/apriltag

### Step 2: Print Settings

**Recommended Settings**:
- **Size**: 4" x 4" (minimum 2" x 2")
- **Quality**: High quality or best photo
- **Color**: Black and white
- **Paper**: Matte or glossy (avoid reflective)
- **Scaling**: 100% (no scaling)

**Print Command** (Mac/Linux):
```bash
lp -o media=Letter -o scaling=100 tag_1.pdf
```

### Step 3: Mounting

- Mount on rigid surface (foam board, cardboard)
- Ensure flat and not wrinkled
- Avoid glare and reflections
- Mount at eye level for best detection

### Step 4: Lighting

- Ensure good lighting (avoid shadows)
- Avoid direct sunlight (causes glare)
- Use diffuse lighting if possible
- Minimum 200 lux recommended

---

## Detection Quality Guidelines

### Good Detection Conditions

✅ **Optimal**:
- Tag is in focus
- Good lighting (200+ lux)
- Tag is straight (not at extreme angles)
- Tag is 1-10 feet from camera
- No glare or reflections
- High contrast (black on white)

### Poor Detection Conditions

❌ **Problematic**:
- Tag is blurry or out of focus
- Poor lighting (shadows, too dark)
- Tag at extreme angle (>60°)
- Tag too close (<6 inches) or too far (>15 feet)
- Glare or reflections on tag
- Low contrast or faded tag

---

## Troubleshooting Detection

### Problem: Tag Not Detected

**Possible Causes**:
1. Tag is out of focus
2. Lighting is too dark
3. Tag is at extreme angle
4. Tag is too far away
5. Tag is damaged or faded
6. Wrong tag family selected

**Solutions**:
1. Adjust camera focus
2. Improve lighting
3. Move closer or adjust angle
4. Move within 2-3 feet
5. Print new tag
6. Verify tag family in code

### Problem: Inconsistent Detection

**Possible Causes**:
1. Lighting varies
2. Camera focus changes
3. Tag is partially visible
4. Camera is moving too fast

**Solutions**:
1. Use consistent lighting
2. Lock camera focus
3. Ensure full tag visibility
4. Slow down camera movement

### Problem: Incorrect Position Data

**Possible Causes**:
1. Camera not calibrated
2. Tag is at extreme angle
3. Decimation too high
4. Tag size incorrect

**Solutions**:
1. Calibrate camera
2. Adjust angle
3. Reduce decimation
4. Verify tag size

---

## Distance and Size Relationships

### Detection Range by Tag Size

| Tag Size | Optimal Range | Maximum Range |
|----------|---------------|---------------|
| 2" x 2" | 1-3 feet | 5 feet |
| 4" x 4" | 2-8 feet | 15 feet |
| 6" x 6" | 3-12 feet | 25 feet |
| 8" x 8" | 4-15 feet | 30 feet |

### Recommended Setup

- **Tag Size**: 4" x 4"
- **Distance**: 2-8 feet
- **Angle**: 0-45 degrees
- **Lighting**: 300-500 lux
- **Camera**: 640x480 resolution

---

## Coordinate System

AprilTags use a standard 3D coordinate system:

```
        Z (Up)
        |
        |
        +------ X (Right)
       /
      /
     Y (Forward)
```

### Position Values

- **X**: Horizontal distance (right is positive)
- **Y**: Forward distance (forward is positive)
- **Z**: Vertical distance (up is positive)

### Orientation Values

- **Pitch**: Rotation around X-axis (-180° to 180°)
- **Roll**: Rotation around Y-axis (-180° to 180°)
- **Yaw**: Rotation around Z-axis (-180° to 180°)

### Distance Measurements

- **Range**: Total distance from camera to tag
- **Bearing**: Horizontal angle to tag (-180° to 180°)
- **Elevation**: Vertical angle to tag (-90° to 90°)

---

## Generating Custom AprilTags

### Using Official Generator

1. Visit: https://april.eecs.umich.edu/software/apriltag/
2. Select tag family (TAG_36h11 recommended)
3. Enter tag ID (1-586 for TAG_36h11)
4. Download PNG or PDF
5. Print at desired size

### Using Command Line

```bash
# Generate tag 1 in TAG_36h11 family
apriltag_generate_svg.py --family tag36h11 --id 1 --output tag_1.svg

# Convert to PNG
convert tag_1.svg tag_1.png
```

### Using Python

```python
import apriltag

# Create detector
detector = apriltag.Detector(apriltag.DetectorOptions(families="tag36h11"))

# Generate tag
tag = detector.generate_tag(1)
```

---

## Best Practices

### For Reliable Detection

1. **Use Standard Tags**: Stick to TAG_36h11 family
2. **Consistent Size**: Use 4" x 4" tags
3. **Good Lighting**: Ensure 300+ lux
4. **Clean Tags**: Keep tags clean and undamaged
5. **Proper Mounting**: Mount flat and secure
6. **Camera Calibration**: Calibrate camera for accuracy
7. **Test First**: Test detection before deployment

### For Optimal Performance

1. **Multiple Tags**: Use multiple tags for redundancy
2. **Strategic Placement**: Place tags at known locations
3. **Backup Detection**: Have fallback detection method
4. **Logging**: Log all detections for analysis
5. **Monitoring**: Monitor detection quality metrics

---

## Common Issues and Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| No detection | Tag not visible | Check focus, lighting, angle |
| Intermittent detection | Lighting varies | Use consistent lighting |
| Wrong position | Camera not calibrated | Calibrate camera |
| Slow detection | High resolution | Reduce resolution or increase decimation |
| Multiple detections | Reflections | Reduce glare, use matte surface |
| Jittery data | Noise in detection | Increase decimation or improve lighting |

---

## Resources

### Official Documentation
- **AprilTag Official**: https://april.eecs.umich.edu/software/apriltag/
- **FTC AprilTag Docs**: https://ftc-docs.firstinspires.org/apriltag
- **OpenCV Docs**: https://docs.opencv.org/

### Tools
- **Tag Generator**: https://april.eecs.umich.edu/software/apriltag/
- **Detector Library**: https://github.com/AprilRobotics/apriltag
- **FTC SDK**: https://github.com/FIRST-Tech-Challenge/FtcRobotController

### Tutorials
- **FTC AprilTag Tutorial**: https://ftc-docs.firstinspires.org/apriltag/vision_portal/apriltag_intro/apriltag-intro.html
- **AprilTag Detection**: https://april.eecs.umich.edu/software/apriltag/
- **Pose Estimation**: https://ftc-docs.firstinspires.org/apriltag-detection-values

---

## Quick Reference

### Recommended Settings for Testing

```
Tag Family: TAG_36h11
Tag ID: 1-12 (standard FTC tags)
Tag Size: 4" x 4"
Distance: 2-8 feet
Lighting: 300-500 lux
Camera Resolution: 640x480
Frame Rate: 30 FPS
Decimation: 3 (balanced)
```

### Recommended Settings for High Accuracy

```
Tag Family: TAG_36h11
Tag ID: 1-12
Tag Size: 6" x 6"
Distance: 3-10 feet
Lighting: 500+ lux
Camera Resolution: 1280x720
Frame Rate: 30 FPS
Decimation: 1 (high accuracy)
```

### Recommended Settings for High Speed

```
Tag Family: TAG_16h5
Tag ID: 1-12
Tag Size: 4" x 4"
Distance: 2-6 feet
Lighting: 300+ lux
Camera Resolution: 320x240
Frame Rate: 60 FPS
Decimation: 4 (fast)
```

---

**Last Updated**: 2024  
**Version**: 1.0  
**Status**: Complete
