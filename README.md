# KCLMS FTC ASYMPTOTES — Robot Code (2025-2026)

**Team:** KCLMS FTC ASYMPTOTES  
**School:** King's College London Mathematics School, London SE11 6NJ  
**Season:** FIRST Tech Challenge — DECODE Challenge (2025-2026)  
**Contact:** kavan.vyas@kcl.ac.uk  
**Website:** https://kclms-ftc.github.io/ftc_web/

---

## About the Team

KCLMS FTC ASYMPTOTES is King's College London Mathematics School's First Tech Challenge robotics team, founded in 2025 by students Kavan Vyas and Carskey Chung. As a rookie team competing in the DECODE Challenge, we are building a legacy of engineering at KCLMS and promoting collaborative STEM for students of all backgrounds.

Our name, *ASYMPTOTES*, reflects our philosophy: always approaching excellence, always improving, never settling.

---

## About This Repository

This repository contains all robot control software for our competition robot. It is forked from the official [FTC Robot Controller SDK](https://github.com/FIRST-Tech-Challenge/FtcRobotController) and all team code lives in:

```
TeamCode/src/main/java/org/firstinspires/ftc/teamcode/
```

The project is Android Studio based and targets the REV Control Hub running the FTC Robot Controller app.

---

## Robot Hardware

| Subsystem | Hardware |
|---|---|
| Drivetrain | 4-wheel Mecanum (wheels: wheel_0 to wheel_3) |
| Odometry | GoBilda Pinpoint (X/Y pods + IMU) |
| Intake | Single roller motor (intake_motor) |
| Shooter | Dual-flywheel motors (shooter_motor_0, shooter_motor_1) |
| Feeder | Servo (feeder_servo) |
| Stopper | Servo (stopper_servo) |
| Vision | USB webcam (AprilTag detection) |

All hardware names are mapped in `HardwareMapConfig.java`. The strings there must exactly match the Driver Station configuration.

---

## Code Structure

```
teamcode/
    HardwareMapConfig.java   — single place where all hardwareMap.get() calls live
    Drivetrain.java          — mecanum drive with odometry-based auto-move
    Intake.java              — roller intake with mode cycling (off / low / high / outtake)
    Shooter.java             — state machine: spin up, open stopper, feed balls
    Teleop.java              — main driver-controlled OpMode (uses subsystem classes)
    Teleop2.java             — fully manual OpMode with direct hardware control
    Auto.java                — autonomous OpMode
    mechanisms/
        AprilTagWebcam.java  — AprilTag vision processing
```

### Key Design Decisions

- `HardwareMapConfig` is the only class that ever calls `hardwareMap.get()`. All subsystems receive this object in their constructor and pull references from it. This keeps hardware names in one place and makes unit testing easier.
- Iterative OpModes extend `OpMode` (not `LinearOpMode`), with a `loop()` method called ~50 times per second.
- `Shooter.java` uses a state machine (IDLE -> SPINNING_UP -> READY -> FEEDING -> DONE) to sequence the 3-ball shooting cycle without blocking the main loop.

---

## OpModes

### Teleop2 — Manual (primary driver OpMode)

All controls are direct and immediate. No automated sequences.

| Input (PS4) | Action |
|---|---|
| Left stick | Drive (forward/back + strafe) |
| Right stick X | Rotation |
| Triangle (y) | Cycle intake: off -> low (0.2) -> high (1.0) -> off |
| L2 (left_trigger full) | Both shooter motors fire at full power |
| X (cross) | Feeder servo toggle (down / up) |
| R1 (right_bumper) | Stopper servo toggle (closed / open) |

### Teleop — Subsystem OpMode

Uses the `Drivetrain`, `Intake`, and `Shooter` subsystem classes. The shooter is triggered by pressing X, which starts the full 3-ball automated sequence.

### Auto

Autonomous OpMode for the DECODE Challenge. Uses the GoBilda Pinpoint odometry driver for position tracking.

---

## Branches

| Branch | Purpose |
|---|---|
| master | Stable, competition-ready code |
| hf-prime | Active development branch |
| kishi | Secondary development / testing |
| hf-beta, hf-epsilon | Feature branches |

---

## Getting Started

### Requirements

- Android Studio Ladybug (2024.2) or later
- Android SDK (installed via Android Studio)
- REV Control Hub or Robot Controller phone

### Build and Deploy

1. Clone this repository
2. Open in Android Studio ("Import project (Eclipse ADT, Gradle, etc.)")
3. Connect the Control Hub via USB
4. Run the `TeamCode` configuration to build and deploy

### Driver Station Configuration

The hardware config on the Driver Station must use the exact names defined in `HardwareMapConfig.java`:

```
wheel_0, wheel_1, wheel_2, wheel_3
shooter_motor_0, shooter_motor_1
intake_motor
feeder_servo, stopper_servo
webcam
pinpoint
```

---

## FTC SDK

This project is built on top of the official FTC SDK (v11.1). The SDK provides all hardware abstraction, communication with the Control Hub, telemetry, and the OpMode lifecycle. Original SDK documentation is available at:

- [FTC Documentation](https://ftc-docs.firstinspires.org/index.html)
- [FTC SDK Javadoc](https://javadoc.io/doc/org.firstinspires.ftc)
- [FTC Community Forum](https://ftc-community.firstinspires.org/)

---

*FIRST and FIRST Tech Challenge are registered trademarks of For Inspiration and Recognition of Science and Technology (FIRST).*
