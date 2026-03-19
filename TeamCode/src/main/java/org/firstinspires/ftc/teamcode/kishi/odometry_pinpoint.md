# goBILDA 4-Bar Odometry Pack + Pinpoint Computer
### What it adds to this robot and why it matters

---

## What the Pack Contains

| Component | Qty | What it does |
|-----------|-----|--------------|
| **4-Bar Odometry Pod** (SKU 3110-0001-0002) | 2 | Spring-loaded dead wheel that rides the floor and measures precise rolling distance via a quadrature encoder |
| **Pinpoint Odometry Computer** | 1 | Dedicated co-processor that reads both encoders at 1500 Hz and fuses them with an internal IMU to compute robot position + heading |

The pods connect to the Pinpoint via 4-pin JST cables. The Pinpoint connects to any REV Hub I²C port (avoid port 0 — reserved for the built-in IMU).

> **These are now wired in.** `pod_x` (forward/back) and `pod_y` (left/right) are mapped  
> in `HardwareMapConfig.java` and exposed through the new `Odometry.java` subsystem class.  
> `Robot.java` holds an `odometry` field — use `robot.odometry.*` in any OpMode.

---

## Hardware Specs (for configuration)

### Pinpoint Computer
| Spec | Value |
|------|-------|
| Interface | I²C (3.3–5V) |
| Update rate | **1500 Hz** (~0.65ms per cycle) |
| Current draw | 100 mA |
| Weight | 40g (with cable + hardware) |
| Error detection | CRC8 over I²C |
| DS config type | I²C Device |
| DS config name | `pinpoint` |

### 4-Bar Odometry Pods
| Spec | Value |
|------|-------|
| Wheel diameter | 32 mm |
| Encoder resolution | **2000 CPR** (Countable Events per Revolution) |
| Ticks per mm | **≈ 19.894 ticks/mm** (2000 ÷ (π × 32)) |
| Wheel material | Plastic with rubber rollers (50A durometer) |
| Spring tension | Fine-tuned for near-linear downforce |
| Profile | 43mm × 43mm (fits 1120 U-Channel) |
| Weight | 84g each |
| DS config name (pod_x) | `pod_x` — forward/back pod |
| DS config name (pod_y) | `pod_y` — left/right (strafe) pod |

> The 19.894 ticks/mm figure means at 1500 Hz the Pinpoint can theoretically detect movement as small as **~0.05 mm per update cycle** — far finer than anything a drive motor encoder could provide.

---

## What Data You Get

Right now the robot has **no idea where it is on the field**. Motors run open-loop (no position feedback), the REV IMU gives heading only, and if the robot is bumped nothing corrects it. The Pinpoint changes all of that.

### Every loop you can read:

| Data | Type | Units | API call |
|------|------|-------|----------|
| **X position** | `double` | mm (or inches) | `odo.getPosition().getX(DistanceUnit.MM)` |
| **Y position** | `double` | mm (or inches) | `odo.getPosition().getY(DistanceUnit.MM)` |
| **Heading (yaw)** | `double` | radians or degrees | `odo.getPosition().getHeading(AngleUnit.DEGREES)` |
| **X velocity** | `double` | mm/s | `odo.getVelocity().getX(...)` |
| **Y velocity** | `double` | mm/s | `odo.getVelocity().getY(...)` |
| **Angular velocity** | `double` | rad/s or deg/s | `odo.getVelocity().getHeading(...)` |
| **Raw encoder ticks (pod 0)** | `int` | ticks | `odo.getEncoderX()` |
| **Raw encoder ticks (pod 1)** | `int` | ticks | `odo.getEncoderY()` |
| **Loop time** | `double` | ms | `odo.getLoopTime()` |
| **Update frequency** | `double` | Hz | `odo.getFrequency()` |
| **Device status** | `enum` | READY / CALIBRATING / NOT_READY | `odo.getDeviceStatus()` |

All position is returned as a **`Pose2D`** object (X, Y, heading in one package).

---

## What This Enables vs. What We Have Now

### Current limitations (no odometry)
```
Robot has no idea where it is.
Autonomous = timed drives ("go forward for 1.2 seconds, hope for the best").
Any bump or wheel slip is unrecoverable.
Heading drifts slowly because the REV IMU isn't fused with movement.
No way to aim the shooter based on field position.
```

### With Pinpoint attached
```
Robot knows its X/Y position on the field in millimetres.
Autonomous = "go to coordinate (800mm, 400mm) then turn to 90°".
Bumps and slippage are detected and corrected.
Heading is fused-IMU quality — ~0.002% error margin.
Shooter can use field position + heading to auto-aim.
```

---

## Specific Data Insights for Each Subsystem

### Drivetrain
| Insight | What you can do with it |
|---------|------------------------|
| Real-time X/Y position | Drive to exact field coordinates, not just "forward N seconds" |
| Heading (fused IMU) | Hold a straight heading even when pushed; auto-correct drift |
| X/Y velocity | Implement velocity-based braking — slow down as you approach a target |
| Pose at end of auto | Know where the robot ended up so TeleOp can use it as a starting reference |

**Practical example:** Instead of `drive(1.0, 0, 0)` for 800ms, you'd write:
```java
// drive until X position reaches 800mm
while (odo.getPosition().getX(DistanceUnit.MM) < 800) {
    robot.drivetrain.drive(0.6, 0, headingCorrection);
    odo.update();
}
```

### Shooter
| Insight | What you can do with it |
|---------|------------------------|
| Field X/Y position | Calculate distance to the goal automatically |
| Heading | Know which direction the robot is facing; combine with turret angle for auto-aim |
| Distance-to-goal | Automatically select `VELOCITY_HIGH` vs `VELOCITY_LOW` (or interpolate a custom RPM) |

**Practical example:** Auto-select flywheel speed based on distance:
```java
double distMM = Math.hypot(pos.getX() - GOAL_X, pos.getY() - GOAL_Y);
double targetVelocity = distMM > 2000 ? Shooter.VELOCITY_HIGH : Shooter.VELOCITY_LOW;
robot.shooter.spinUp(targetVelocity);
```

### Autonomous Routines
| What becomes possible |
|-----------------------|
| Reliable multi-step paths without timing (coordinate-based) |
| Compatible with **Road Runner** and **Pedro Pathing** for smooth curved paths |
| Re-localization: combine with the existing `webcam` (AprilTags in `Vision.java`) to correct odometry drift mid-match |
| Repeatable autonomous every run — same result regardless of starting battery voltage |

---

## Why 1500 Hz Matters

Most FTC dead-wheel setups update at 100–300 Hz because they read encoders inside the main OpMode loop. The Pinpoint runs on its own processor:

```
Standard FTC loop:   100–300 Hz → can miss fast movements    → cumulative error builds up
Pinpoint internal:   1500 Hz    → captures every tiny motion → errors stay tiny
```

Between two Pinpoint updates (0.65ms apart), a robot moving at full speed (~1.5 m/s) travels only **~1mm**. At 100 Hz (10ms), that same robot moves **~15mm between readings** — multiplied over a 30-second auto, errors compound fast.

---

## How It Connects to the Existing Code

**Everything is already wired in.** Here's what was added and where:

### `HardwareMapConfig.java` — hardware lookup (done)
```java
// Pinpoint computer (I2C device named "pinpoint" in Driver Station config)
public GoBildaPinpointDriver pinpoint;

// In constructor:
pinpoint = hw.get(GoBildaPinpointDriver.class, "pinpoint");
pinpoint.setOffsets(-84.0, -168.0);  // mm from robot center to each pod — TUNE THESE
pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
pinpoint.setEncoderDirections(
    GoBildaPinpointDriver.EncoderDirection.FORWARD,  // pod_x
    GoBildaPinpointDriver.EncoderDirection.FORWARD   // pod_y — flip to REVERSED if Y reads backwards
);
pinpoint.resetPosAndIMU();
```

### `Odometry.java` — new dedicated subsystem class (done)
Wrapper class around `GoBildaPinpointDriver` that provides clean, named methods:
```java
robot.odometry.update();               // call every loop — polls Pinpoint over I2C
robot.odometry.getX();                 // mm
robot.odometry.getY();                 // mm
robot.odometry.getHeadingDegrees();    // degrees
robot.odometry.getVelocityX();         // mm/s
robot.odometry.getVelocityY();         // mm/s
robot.odometry.distanceTo(x, y);       // straight-line distance to any point
robot.odometry.angleTo(x, y);          // bearing to any point (for turret aim)
robot.odometry.getRawTicksX();         // raw pod_x encoder count
robot.odometry.getRawTicksY();         // raw pod_y encoder count
robot.odometry.displayTelemetry(t);    // prints all of the above to Driver Station
```

### `Robot.java` — wired in (done)
```java
public final Odometry odometry;  // added field
// in constructor:
odometry = new Odometry(config);
```

### Using it in an OpMode
```java
robot = new Robot(hardwareMap);  // odometry is automatically initialized
waitForStart();

while (opModeIsActive()) {
    robot.odometry.update();  // always first — poll the Pinpoint
    robot.drivetrain.drive(drive, strafe, rotate);
    // ... rest of your loop ...
    robot.displayTelemetry(telemetry);  // includes odometry data
}
```

### Driver Station Configuration
Add these **three** devices to your robot config:
| DS Name | Type | Port |
|---------|------|------|
| `pinpoint` | I²C Device | Any I²C port except port 0 |
| `pod_x` | (no separate DS entry — plugged into Pinpoint physically) | Pinpoint connector |
| `pod_y` | (no separate DS entry — plugged into Pinpoint physically) | Pinpoint connector |

The pods wire physically into the Pinpoint, not into the Control Hub directly.  
Only `pinpoint` needs a Driver Station config entry.

---

## Summary Table

| Category | Without Pinpoint | With Pinpoint |
|----------|-----------------|---------------|
| Position | Unknown | X, Y in mm (±~2mm) |
| Heading | REV IMU only (drifts) | Fused IMU (0.002% error) |
| Velocity | None | X, Y, angular (continuous) |
| Autonomous method | Timed / encoder-based | Coordinate-based |
| Bump recovery | None | Instant re-localization |
| Shooter aim assist | Manual only | Distance-calculated RPM |
| Path following libraries | Not practical | Road Runner / Pedro Pathing ready |
| Loop CPU impact | — | Minimal (offloaded to Pinpoint chip) |

---

> **Bottom line:** Attaching the Pinpoint pack turns the robot from "driving blind by feel" into a robot that always knows exactly where it stands on the field. Every subsystem — drivetrain precision, shooter aim, and autonomous routing — gets a direct and concrete upgrade from that single piece of data.
