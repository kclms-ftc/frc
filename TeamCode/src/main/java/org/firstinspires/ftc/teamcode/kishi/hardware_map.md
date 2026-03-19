# Kishi Hardware Map Reference

This document shows every hardware device used by the `kishi` package, what it is named in the Driver Station configuration, which Java field it maps to, and which subsystem uses it.

All device names in `HardwareMapConfig.java` **must exactly match** what is entered in the Driver Station's robot configuration.  
If a name is wrong, the app will crash on init with a `DeviceNotFoundException`.

---

## Drivetrain — Mecanum Wheels

| DS Config Name | Java Field         | Type       | Physical Location | Used By         |
|----------------|--------------------|------------|-------------------|-----------------|
| `wheel_0`      | `robot.wheel_0`    | `DcMotorEx` | Front-Right (NE) | `Drivetrain.java` |
| `wheel_1`      | `robot.wheel_1`    | `DcMotorEx` | Back-Right  (SE) | `Drivetrain.java` |
| `wheel_2`      | `robot.wheel_2`    | `DcMotorEx` | Back-Left   (SW) | `Drivetrain.java` |
| `wheel_3`      | `robot.wheel_3`    | `DcMotorEx` | Front-Left  (NW) | `Drivetrain.java` |

**Wheel layout (top-down):**
```
NW (wheel_3) ──── NE (wheel_0)
      |   ROBOT   |
SW (wheel_2) ──── SE (wheel_1)
```

**Motor directions set in code:**
| Wheel    | Direction | Reason |
|----------|-----------|--------|
| `wheel_0` (NE) | FORWARD | Right side |
| `wheel_1` (SE) | FORWARD | Right side |
| `wheel_2` (SW) | REVERSE | Left side — mirrored |
| `wheel_3` (NW) | REVERSE | Left side — mirrored |

**Speed modes:**
| Mode      | Multiplier | Trigger                        |
|-----------|------------|--------------------------------|
| NORMAL    | 1.0×       | Default (no bumper held)       |
| PRECISION | 0.3×       | Left bumper held               |
| TURBO     | 1.0×       | Right bumper held              |

---

## Shooter — Flywheel + Feeder

| DS Config Name    | Java Field              | Type        | Role                        | Used By        |
|-------------------|-------------------------|-------------|-----------------------------|----------------|
| `shooter_motor_0` | `robot.shooter_motor_0` | `DcMotorEx` | Flywheel motor A (FORWARD)  | `Shooter.java` |
| `shooter_motor_1` | `robot.shooter_motor_1` | `DcMotorEx` | Flywheel motor B (REVERSE)  | `Shooter.java` |
| `feeder_servo`    | `robot.feeder_servo`    | `Servo`     | Pushes ball into flywheels  | `Shooter.java` |

**Flywheel velocity presets:**
| Preset           | Value (ticks/s) | Use case           |
|------------------|-----------------|--------------------|
| `VELOCITY_HIGH`  | 1800            | High goal shot     |
| `VELOCITY_LOW`   | 1350            | Low goal / lob     |

**Feeder servo positions:**
| Position name    | Servo value | State              |
|------------------|-------------|--------------------|
| `FEEDER_RETRACT` | 0.0         | Ball held back     |
| `FEEDER_PUSH`    | 0.55        | Ball pushed in     |

**Feeder state machine flow:**
```
IDLE ──(fireBallNonBlocking)──► PUSHING (180ms)
                                    │
                                    ▼
                              RETRACTING (170ms)
                                    │
                                    ▼
                                  IDLE
```

---

## Other Subsystems (mapped but not in Robot.java for kishi)

| DS Config Name  | Java Field           | Type          | Role                         | Status |
|-----------------|----------------------|---------------|------------------------------|--------|
| `turret_motor`  | `robot.turret_motor` | `DcMotorEx`   | Rotates turret azimuth       | Mapped, no subsystem class yet |
| `intake_motor`  | `robot.intake_motor` | `DcMotorEx`   | Pulls balls into robot       | Mapped, no subsystem class yet |
| `angle_servo`   | `robot.angle_servo`  | `Servo`       | Adjusts shooter launch angle | Mapped, no subsystem class yet |
| `webcam`        | `robot.webcam`       | `WebcamName`  | USB camera for vision        | Mapped, no subsystem class yet |

> To use one of these, add a new subsystem class and pass it the same `HardwareMapConfig` — no hardware lookup changes needed.

---

## Odometry — Pinpoint Computer + Dead Wheel Pods

| DS Config Name | Java Field        | Type                      | Role                                   | Used By         |
|----------------|-------------------|---------------------------|----------------------------------------|-----------------|
| `pinpoint`     | `robot.pinpoint`  | `GoBildaPinpointDriver`   | I²C co-processor, fuses pods + IMU at 1500 Hz | `Odometry.java` |
| `pod_x`        | (Pinpoint port)   | Physical pod (no DS entry) | Forward/back dead wheel, measures Y travel  | `Odometry.java` |
| `pod_y`        | (Pinpoint port)   | Physical pod (no DS entry) | Left/right dead wheel, measures X travel    | `Odometry.java` |

**Pod specs:**
| Spec | Value |
|------|-------|
| Wheel diameter | 32 mm |
| Encoder resolution | 2000 CPR |
| Ticks per mm | ≈ 19.894 |
| Pinpoint update rate | 1500 Hz |
| I²C port restriction | Any port **except port 0** |

**Data exposed via `Odometry.java` → `robot.odometry.*`:**

| Method | Returns | Unit |
|--------|---------|------|
| `getX()` | Position along X axis | mm |
| `getY()` | Position along Y axis | mm |
| `getHeadingDegrees()` | Robot facing angle | ° |
| `getVelocityX()` | Lateral velocity | mm/s |
| `getVelocityY()` | Forward velocity | mm/s |
| `getAngularVelocityDegPerSec()` | Turn rate | °/s |
| `getRawTicksX()` | Raw pod_x encoder count | ticks |
| `getRawTicksY()` | Raw pod_y encoder count | ticks |
| `distanceTo(x, y)` | Distance to any field point | mm |
| `angleTo(x, y)` | Bearing to any field point | ° |
| `getPinpointFrequencyHz()` | Pinpoint internal rate | Hz |
| `getStatus()` | Device health | READY/CALIBRATING/NOT_READY |


---

## Hardware Independence Notes

- **`HardwareMapConfig.java`** is the **only** place that calls `hw.get(...)`.  
- **`Drivetrain.java`** and **`Shooter.java`** receive a `HardwareMapConfig` object and never touch `HardwareMap` directly.  
- This means both subsystems are fully portable — you can instantiate them with any `HardwareMapConfig` (real or mock) without modifying their internal logic.
- If you add or rename a device, change the string in `HardwareMapConfig.java` only — nothing else breaks.
